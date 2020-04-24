package pt.mobilesword.silentupdate.core

import android.app.*
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import pt.mobilesword.silentupdate.BuildConfig
import pt.mobilesword.silentupdate.R
import pt.mobilesword.silentupdate.SilentUpdate
import java.io.File
import java.util.*

/**
  * Uri to get files
  * Compatible with 7.0
  * @param file
  */

private fun getUri4File(context: Context, file: File?): Uri {
	//Get the package name of the current app
	val fileProviderAuth = "${context.packageName}.fileprovider"
	checkNotNull(file)
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
		FileProvider.getUriForFile(context.applicationContext, fileProviderAuth, file)
	} else {
		Uri.fromFile(file)
	}
}

/**
 * / Construct Intent to open APK
 */
internal fun Context.constructOpenApkIntent(file: File): Intent {
	val intent = Intent(Intent.ACTION_VIEW)
	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)//7.0 effective
		intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)//7.0 effective
	}
	val uri = getUri4File(this, file)
	intent.setDataAndType(uri, "application/vnd.android.package-archive")
	return intent
}

/**
 * Open APK directly
 */
internal fun Context.openApkByFilePath(file: File) {
	//Prevent some systems from forcibly closing crashes caused by installing apps from unknown sources
	try {
		startActivity(constructOpenApkIntent(file))
	} catch (e: Exception) {
		e.printStackTrace()
	}
}

//log
internal fun Any.loge(message: String) {
	if (BuildConfig.DEBUG) Log.e("silentUpdate", message)
}

//Check for updated URL
internal fun String.checkUpdateUrl() {
	val url = this
	if (!url.contains("http") && !url.contains("https")) {
		throw IllegalArgumentException("url must start with http or https")
	}
}

//Display system built-in download popup
internal fun ContextWrapper?.showSystemDownloadDialog(apkUrl: String, fileName: String) {
	if (this == null) return
	val updateInfo = SPCenter.getUpdateInfo()
	val dialog = AlertDialog.Builder(this)
			.setCancelable(!updateInfo.isForce)
			.setTitle(updateInfo.title)
			.setMessage(updateInfo.msg)
			.setPositiveButton(getString(R.string.module_silentupdate_update), null)
			.setNegativeButton(getString(R.string.module_silentupdate_hold_on), null)
			.create()
	dialog.setOnShowListener {
		//positive
		val posBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
		posBtn.setOnClickListener {
			if (!updateInfo.isForce) dialog.dismiss()
			//this? .toast ("Start downloading ...")
			DownLoadCenter.addRequest(apkUrl, fileName, true)
		}
		val negBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
		//negative
		if (updateInfo.isForce) {
			negBtn.visibility = View.GONE
		} else {
			negBtn.setOnClickListener {
				dialog.dismiss()
				SPCenter.modifyDialogTime(Calendar.getInstance().time.time)
			}
		}
	}
	dialog.show()
}

// Display system built-in pop-up window
internal fun ContextWrapper?.showSystemInstallDialog(updateInfo: UpdateInfo, file: File) {
	if (this == null) return
	val dialog = AlertDialog.Builder(this)
			.setCancelable(!updateInfo.isForce)
			.setTitle(updateInfo.title)
			.setMessage(updateInfo.msg)
			.setPositiveButton(getString(R.string.module_silentupdate_install), null)
			.setNegativeButton(getString(R.string.module_silentupdate_hold_on), null)
			.create()
	dialog.setOnShowListener {
		//positive
		val posBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
		posBtn.setOnClickListener {
			if (!updateInfo.isForce) dialog.dismiss()
			this.openApkByFilePath(file)
		}
		val negBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
		//negative
		if (updateInfo.isForce) {
			negBtn.visibility = View.GONE
		} else {
			negBtn.setOnClickListener {
				dialog.dismiss()
				SPCenter.modifyDialogTime(Calendar.getInstance().time.time)

			}
		}
	}
	dialog.show()
}

//Update Notification
internal fun Context?.showInstallNotification(file: File) {
	this?.loge("showInstallNotification")
	val activity = this ?: return
	val notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
	//Determine whether it is within the time interval
	val dialogTime = SPCenter.getDialogTime()
	if (dialogTime == 0L || checkMoreThanDays(dialogTime,SilentUpdate.intervalDay)) {
		val updateInfo = SPCenter.getUpdateInfo()
		val title = updateInfo.title
		val msg = updateInfo.msg
		val intent = activity.constructOpenApkIntent(file)
		val pIntent = PendingIntent.getActivity(activity, UUID.randomUUID().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

		val builder = NotificationCompat.Builder(activity,Const.NOTIFICATION_CHANNEL_ID).apply {
			this.setSmallIcon(android.R.drawable.stat_sys_download_done)// Set small icon
			this.setLargeIcon(BitmapFactory.decodeResource(activity.resources, getAppIcon(activity)))//Set big icons
			this.setTicker(title)// Tips on the phone status bar-the top one
			this.setWhen(System.currentTimeMillis())// Set time
			this.setContentTitle(title)// Set title
			this.setContentText(msg)// Set the content of the notification
			this.setContentIntent(pIntent)// Intent after clicking
			this.setDefaults(Notification.DEFAULT_ALL)// Setting tips all
			this.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)//Lock screen notification
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				this.setChannelId(Const.NOTIFICATION_CHANNEL_ID)
			}
		}

		val notification = builder.build()// 4.1 or more is required to work
		notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL// Click to cancel automatically
		//display
		notificationManager.notify(UUID.randomUUID().hashCode(), notification)
	}
}


/**
 * Status: The file already exists or the download is complete under Wifi
 * Display Dialog: prompt the user to install
 */
internal fun ContextWrapper?.showInstallDialog(file: File) {
	this?.loge("showInstallDialog")
	//Determine whether it is within the time interval
	val dialogTime = SPCenter.getDialogTime()
	if (dialogTime == 0L || checkMoreThanDays(dialogTime,SilentUpdate.intervalDay)) {
		val updateInfo = SPCenter.getUpdateInfo()
		if (SilentUpdate.installDialogShowAction != null) {
			this.showCustomInstallDialog(file)
		} else {
			this.showSystemInstallDialog(updateInfo, file)
		}
	}
}

//Display Custom-Install Popup
private fun ContextWrapper?.showCustomInstallDialog(file: File) {
	if (this == null) return
	SilentUpdate.installDialogShowAction?.show(
			context = this,
			updateInfo = SPCenter.getUpdateInfo(),
			positiveClick = { this.openApkByFilePath(file) },
			negativeClick = {
				SPCenter.modifyDialogTime(Calendar.getInstance().time.time)//recording
			}
	)
}


/**
* Status: Flow
* Display Dialog: prompt the user to download
 */
internal fun ContextWrapper?.showDownloadDialog(apkUrl: String, fileName: String) {
	this?.loge("showDownloadDialog")
	val dialogTime = SPCenter.getDialogTime()
	if (dialogTime == 0L || checkMoreThanDays(dialogTime,SilentUpdate.intervalDay)) {
		//Determine if there is a custom download popup
		if (SilentUpdate.downLoadDialogShowAction != null) {
			this.showCustomDownloadDialog(apkUrl, fileName)
		} else {
			this.showSystemDownloadDialog(apkUrl, fileName)
		}
	}
}

private fun ContextWrapper?.showCustomDownloadDialog(apkUrl: String, fileName: String) {
	if (this == null) return
	SilentUpdate.downLoadDialogShowAction?.show(
			context = this,
			updateInfo = SPCenter.getUpdateInfo(),
			positiveClick = { DownLoadCenter.addRequest(apkUrl, fileName, true) },
			negativeClick = {
				SPCenter.modifyDialogTime(Calendar.getInstance().time.time)//recording
			})
}

/**
* Comparison time is more than a few days
  * Unit: millisecond
 */
private fun checkMoreThanDays(timeMillis:Long, day: Int = 7): Boolean {
	val currentTime = Calendar.getInstance().time.time
	if (timeMillis == 0L) return true
	val differ = currentTime - timeMillis
	if (differ > 1000 * 60 * 60 * 24 * day) {
		return true
	}
	return false
}


/**
 * Get the picture of the app
 */
private fun getAppIcon(context:Context): Int {
	val pm: PackageManager = context.packageManager
	try {
		val info = pm.getApplicationInfo(context.packageName, 0)
		return info.icon
	} catch (e: PackageManager.NameNotFoundException) {
		e.printStackTrace()
	}
	return -1
}

/**
 * Get the name of the application
 */
internal fun Context.getAppName(): String? {
	val pm: PackageManager = packageManager
	try {
		val info = pm.getApplicationInfo(this.packageName, 0)
		return info.loadLabel(pm).toString()
	} catch (e: PackageManager.NameNotFoundException) {
		e.printStackTrace()
	}
	return ""
}

/**
 * Whether file exists
 */
internal fun File.isFileExist(): Boolean {
	if (TextUtils.isEmpty(this.path)) return false
	return this.exists() && this.isFile
}
