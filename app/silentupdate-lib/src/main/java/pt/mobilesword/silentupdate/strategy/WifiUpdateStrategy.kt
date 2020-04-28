package pt.mobilesword.silentupdate.strategy

import pt.mobilesword.silentupdate.core.*
import java.io.File

/**
 * Wifi situation
 */
internal class WifiUpdateStrategy : UpdateStrategy {
	init {
		//After the download is complete
		DownLoadCenter.onDownloadComplete = {
		// todo hammer, Nubia will not pop up
		val activity = ContextCenter.getTopActivity()
		activity.showInstallNotification(it) //Update Notification
		activity.showInstallDialog(it) //Show installation popup
		}
	}

	//In case of upgrade operation WIFI
	override fun update(apkUrl: String, latestVersion: String) {
	try {
			apkUrl.checkUpdateUrl()
		} catch (e: Exception) {
		e.printStackTrace()
		return
	}

	val context = ContextCenter.getAppContext()
	val activity = ContextCenter.getTopActivity()
	val fileName = "${context.getAppName()}_v$latestVersion.apk"
	val path = Const.UPDATE_FILE_DIR + fileName

		val taskId = SPCenter.getDownloadTaskId()
		loge("==============")
		loge("taskID=$taskId")
		if (File(path).isFileExist()) {
			loge("【This file already exists】")

			if (DownLoadCenter.isDownTaskSuccess(taskId)) {
				loge("The task has been downloaded")
				// Status: complete
				activity.showInstallDialog(File(path)) //Pop up dialog
			} else if (DownLoadCenter.isDownTaskPause(taskId)) {
				loge("The task has been suspended")
				//// Start download
				loge("Continue download")
				DownLoadCenter.addRequest(apkUrl, fileName, false)
			} else if (DownLoadCenter.isDownTaskProcessing(taskId)) {
				loge("The task is being executed")
			} else {
				loge("install download")
				activity.showInstallDialog(File(path)) //Pop up dialog
			}
		} else {
			loge("start download")
			// There is no direct download
			DownLoadCenter.addRequest(apkUrl, fileName)
		}
	}


}
