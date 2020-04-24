package pt.mobilesword.silentupdate.core

import android.content.Context
import com.google.gson.Gson
import pt.mobilesword.silentupdate.BuildConfig

internal object SPCenter {
	private val sp by lazy { ContextCenter.getAppContext().getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE) }
	private val mGson by lazy { Gson() }

	/**
      * Corresponding to downloadManager
      * Whether downloading task ID
      */
	private val DOWNLOAD_TASK_ID = "download_task_id"

	internal fun clearDownloadTaskId() {
		sp.edit().remove(DOWNLOAD_TASK_ID).apply()
	}

	internal fun setDownloadTaskId(apkTaskID: Long) {
		sp.edit().putLong(DOWNLOAD_TASK_ID, apkTaskID).apply()
	}

	internal fun getDownloadTaskId(): Long {
		return sp.getLong(DOWNLOAD_TASK_ID, -1L)
	}

	/**
	 * Dialog display interval
	 */
	private val DIALOG_TIME = "dialogTime"

	//Get storage time
	fun getDialogTime(): Long {
		return sp.getLong(DIALOG_TIME, 0L)
	}

	//Modify storage time
	fun modifyDialogTime(storeTime: Long) {
		sp.edit().putLong(DIALOG_TIME, storeTime).apply()
	}

	//Clear storage time
	fun clearDialogTime() {
		sp.edit().remove(DIALOG_TIME).apply()
	}

	/**
	 * Updated content
	 */
	private val UPDATE_INFO = "updateInfo"

	//Get updates
	fun getUpdateInfo(): UpdateInfo = mGson.fromJson(sp.getString(UPDATE_INFO, "") as String,UpdateInfo::class.java)

	//Modify the update
	fun modifyUpdateInfo(updateInfo: UpdateInfo) {
		sp.edit().putString(UPDATE_INFO, mGson.toJson(updateInfo)).apply()
	}

	//Clear updates
	fun clearUpdateInfo() {
		sp.edit().remove(UPDATE_INFO).apply()
	}

}
