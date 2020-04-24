package pt.mobilesword.silentupdate.strategy

import android.os.Handler
import pt.mobilesword.silentupdate.core.*
import java.io.File

/**
 * Traffic situation
 */
internal class MobileUpdateStrategy : UpdateStrategy {

    init {
        //After the download is complete
        DownLoadCenter.onDownloadComplete = {
            Handler().postDelayed({
                val activity = ContextCenter.getTopActivity()
                activity.showInstallDialog(it)//Show installation popup
                ContextCenter.getAppContext().openApkByFilePath(it)
            }, 200)
        }
    }


    //Upgrade operation in the case of traffic
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
            loge("This file already exists")
            if (DownLoadCenter.isDownTaskSuccess(taskId)) {
                loge("The task has been downloaded")
                activity.showInstallDialog(File(path)) //pop up dialog
            } else if (DownLoadCenter.isDownTaskPause(taskId)) {
                loge("The task has been suspended")
                //Start download
                loge("Continue download")
                DownLoadCenter.addRequest(apkUrl, fileName, true)
            } else if (DownLoadCenter.isDownTaskProcessing(taskId)) {
                loge("The task is being executed")
            } else {
                activity.showInstallDialog(File(path)) //pop dialog
            }
        } else {
            loge("Show download popup")
            activity.showDownloadDialog(apkUrl, fileName) //Show download popup
        }
    }


}
