package com.pmm.silentupdate

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.text.TextUtils
import com.pmm.silentupdate.core.*
import com.pmm.silentupdate.strategy.MobileUpdateStrategy
import com.pmm.silentupdate.strategy.UpdateStrategy
import com.pmm.silentupdate.strategy.WifiUpdateStrategy
import java.io.File


object SilentUpdate {

	//The following data can be configured
	var downLoadDialogShowAction: DialogShowAction? = null//Custom Download Dialog-> Flow Mode
	var installDialogShowAction: DialogShowAction? = null//Custom Install Dialog-> Wireless mode, the file already exists
	var intervalDay = 7//Interval popup reminder time-default reminder after 7 days-only applicable【isUseDefaultHint=true】

	private val mobileUpdateStrategy by lazy { MobileUpdateStrategy() }
	private val wifiUpdateStrategy by lazy { WifiUpdateStrategy() }

	/**
	 * Initialization of silent update
      	 * @param App context
	 */
	fun init(context: Application) {
		//Context initialization
		ContextCenter.init(context)
		//Add notification channel [compatible with 8.0]
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
			// channelId > Notification channel id
			// channelName > The name of the notification channel that the user can see.
			// importance > Description of notification channels that users can see
			val mChannel = NotificationChannel(channelId, channelName, importance)
			// Configure notification channel properties
			mChannel.description = channelDesc
			// Set the flashing light when the notification appears (if the android device supports it)
			mChannel.enableLights(true)
			mChannel.lightColor = lightColor
			// Set vibration when notification appears (if supported by android device)
			mChannel.enableVibration(enableVibration)
			//mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
			//Finally, create the notification channel in NotificationManager
			mNotificationManager.createNotificationChannel(mChannel)
		}
	}


	/**
	 * Update operation
	 *-Flow mode
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

		//Strategy Mode

		val strategy: UpdateStrategy = when {
			//WIFI
			isConnectWifi(context) -> wifiUpdateStrategy
			else -> mobileUpdateStrategy
		}
		strategy.update(apkUrl, latestVersion)
	}

	//Whether to connect to Wifi
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
	 * Active update Same flow mode
	 * Check local files, there is a download popup window
	 */

	fun activeUpdate(receive: UpdateInfo.() -> Unit) {
		val updateInfo = UpdateInfo()
		updateInfo.receive()
		val apkUrl = updateInfo.apkUrl
		val latestVersion = updateInfo.latestVersion
		if (apkUrl.isBlank() or latestVersion.isBlank()) return
		SPCenter.modifyUpdateInfo(updateInfo)

		//Strategy Mode
		mobileUpdateStrategy.update(apkUrl, latestVersion)
	}


	/**
	 * Clear sp cached data
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
