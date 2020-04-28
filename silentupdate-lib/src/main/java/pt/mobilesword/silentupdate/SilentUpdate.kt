package pt.mobilesword.silentupdate

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.text.TextUtils
import java.io.File

import pt.mobilesword.silentupdate.core.*
import pt.mobilesword.silentupdate.strategy.MobileUpdateStrategy
import pt.mobilesword.silentupdate.strategy.UpdateStrategy
import pt.mobilesword.silentupdate.strategy.WifiUpdateStrategy
import pt.mobilesword.silentupdate.R

object SilentUpdate {

	var downLoadDialogShowAction: DialogShowAction? = null // Custom Download Dialog-> Flow Mode
	var installDialogShowAction: DialogShowAction? = null // Custom Install Dialog-> Wireless mode, the file already exists
	var intervalDay = 7 // Interval popup reminder time-default reminder after 7 days-only applicable【isUseDefaultHint=true】

	private val mobileUpdateStrategy by lazy { MobileUpdateStrategy() }
	private val wifiUpdateStrategy by lazy { WifiUpdateStrategy() }

	/**
	 * Initialization of silent update
	 */
	fun init(context: Application) {
		ContextCenter.init(context)
		val channelName = context.getString(R.string.module_silentupdate_channelName)
		createNotificationChannel(
				context = context,
				channelId = Const.NOTIFICATION_CHANNEL_ID,
				channelName = channelName,
				channelDesc = channelName
		)
	}

	/**
	 * Channels with added notification bar can be deleted after the general library is updated
	 * @param importance NotificationManager.IMPORTANCE_LOW
	 */
	private fun createNotificationChannel(
			context: Context,
			channelId: String,
			channelName: String,
			channelDesc: String = "",
			importance: Int = 0,
			enableVibration: Boolean = true,
			lightColor: Int = Color.GREEN
	) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			/**
			 * channelId > Notification channel id
			 * channelName > The name of the notification channel that the user can see.
			 * importance > Description of notification channels that users can see
			 */
			val mChannel = NotificationChannel(channelId, channelName, importance)
			mChannel.description = channelDesc
			mChannel.enableLights(true)
			mChannel.lightColor = lightColor
			mChannel.enableVibration(enableVibration)
			mNotificationManager.createNotificationChannel(mChannel)
		}
	}

	/**
	 * Update operation
	 *-WIFI mode
	 * @param apkUrl app download address
	 * @param latestVersion The latest version number
	 */
	fun update(receive: UpdateInfo.() -> Unit) {
		val updateInfo = UpdateInfo()
		updateInfo.receive()
		val apkUrl = updateInfo.apkUrl
		val latestVersion = updateInfo.latestVersion
		if (apkUrl.isBlank() or latestVersion.isBlank()) return
		SPCenter.modifyUpdateInfo(updateInfo)

		val context = ContextCenter.getAppContext()
		val strategy: UpdateStrategy = when {
			isConnectWifi(context) -> wifiUpdateStrategy
			else -> mobileUpdateStrategy
		}
		strategy.update(apkUrl, latestVersion)
	}

	private fun isConnectWifi(context: Context): Boolean {
		val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val networkInfo = cm.activeNetworkInfo
		if (networkInfo != null && networkInfo.isConnected) {
			val type = networkInfo.type
			if (type == ConnectivityManager.TYPE_WIFI) {
				return true
			}
		}
		return false
	}

	/**
	 * Active update
	 * Check local files, there is a download popup window
	 */
	fun activeUpdate(receive: UpdateInfo.() -> Unit) {
		val updateInfo = UpdateInfo()
		updateInfo.receive()
		val apkUrl = updateInfo.apkUrl
		val latestVersion = updateInfo.latestVersion
		if (apkUrl.isBlank() or latestVersion.isBlank()) return
			SPCenter.modifyUpdateInfo(updateInfo)

		mobileUpdateStrategy.update(apkUrl, latestVersion)
	}


	/**
	 * Clear SP cached data
	 */
	fun clearCache() {
		SPCenter.clearDownloadTaskId()
		SPCenter.clearDialogTime()
		SPCenter.clearUpdateInfo()
	}

	/**
	 * Delete apk Manually delete the installed apk
	 * @param version
	 */
	fun deleteApk(version: String): Boolean {
		val context = ContextCenter.getAppContext()
		val path = "${Const.UPDATE_FILE_DIR}${context.getAppName()}_v$version.apk"
		return deleteFile(path)
	}


	/**
	 * delete file or directory
	 *
	 * if path is null or empty, return true
	 * if path not exist, return true
	 * if path exist, delete recursion. return true
	 *
	 *
	 * @param path file path
	 * @return Whether the deletion was successful
	 */
	private fun deleteFile(path: String): Boolean {
		if (TextUtils.isEmpty(path))
			return true
		val file = File(path)
		if (!file.exists())
			return true
		if (file.isFile)
			return file.delete()

		if (!file.isDirectory)
			return false

		for (f in file.listFiles()) {
			if (f.isFile) {
				f.delete()
			} else if (f.isDirectory) {
				deleteFile(f.absolutePath)
			}
		}
		return file.delete()
	}
}
