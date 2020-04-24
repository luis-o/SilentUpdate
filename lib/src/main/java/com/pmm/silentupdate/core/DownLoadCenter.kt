package com.pmm.silentupdate.core

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import java.io.File
import java.net.URI
import java.util.HashMap

internal object DownLoadCenter {

	private val downloadManager: DownloadManager by lazy { ContextCenter.getAppContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager }
	private val appUpdateReceiver: AppUpdateReceiver by lazy { AppUpdateReceiver() }
	var onDownloadComplete: ((file: File) -> Unit)? = null

	//Update apk Wifi & Mobile
	internal fun addRequest(apkUrl: String, fileName: String?, isMobileMode: Boolean = false) {
		bindReceiver() //Bind broadcast receiver
		val uri = Uri.parse(apkUrl)
		loge("url=$apkUrl")
		loge("uri=$uri")
		val request = DownloadManager.Request(uri)
		//Set under what network conditions to download
		request.setAllowedNetworkTypes(if (isMobileMode) DownloadManager.Request.NETWORK_MOBILE else DownloadManager.Request.NETWORK_WIFI)
		// Set the notification bar title
		request.setNotificationVisibility(if (isMobileMode) DownloadManager.Request.VISIBILITY_VISIBLE else DownloadManager.Request.VISIBILITY_HIDDEN)
		request.setTitle(fileName)
		request.setDescription(ContextCenter.getAppContext().packageName)
		request.setAllowedOverRoaming(false)
		request.setVisibleInDownloadsUi(true)
		//Set file storage directory
		//request.setDestinationInExternalFilesDir(AppData.getContext(), "download", "youudo_v" + version + ".apk");
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

		val id: Long
		try {
			id = downloadManager.enqueue(request)
			//Deposit to share
			SPCenter.setDownloadTaskId(id)
		} catch (e: Exception) {
			//e.printStackTrace()
		}
	}


	//Query task shape
	private fun queryTaskStatus(id: Long): String {
		val query = DownloadManager.Query()
		query.setFilterById(id)
		val cursor = downloadManager.query(query)
		while (cursor.moveToNext()) {
			return cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
		}
		cursor.close()
		return ""
	}

	//Whether the download task ends
	internal fun isDownTaskProcessing(id: Long) = queryTaskStatus(id) == "192"

	//Whether the download task is paused
	internal fun isDownTaskPause(id: Long) = queryTaskStatus(id) == "193"

	//Whether the download task was successful
	internal fun isDownTaskSuccess(id: Long) = queryTaskStatus(id) == "200"

	//Get file address by downloading id
	private fun getFilePathByTaskId(id: Long): String {
		var filePath = ""
		val query = DownloadManager.Query()
		query.setFilterById(id)
		val cursor = downloadManager.query(query)
		while (cursor.moveToNext()) {
			filePath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
					?: ""
		}
		cursor.close()
		return filePath
	}

	//Download completed
	private fun downloadComplete(intent: Intent) {
		loge("Download completed")
		val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
		// Determine whether the ID is consistent
		if (id != SPCenter.getDownloadTaskId()) return
		loge("Unregister recipient")
		unbindReceiver()//Unregister recipient
		try {
			val uri = Uri.parse(getFilePathByTaskId(id)).toString()
			if (uri.isBlank()) {
				loge("Invalid file downloaded, please determine whether the url can be successfully requested ")
				return
			}
			//must try-catch
			val file = File(URI(uri))
			onDownloadComplete?.invoke(file)
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}


	// Find the corresponding file address by downloading id
   @Deprecated ("Query the status of download tasks")
	private fun queryDownTaskById(id: Long): String? {
		var filePath: String? = null
		val query = DownloadManager.Query()

		query.setFilterById(id)
		val cursor = downloadManager.query(query)

		while (cursor.moveToNext()) {
			val downId = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_ID))
			val title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
			val address = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
			filePath = address
			val status = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
			val size = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
			val sizeTotal = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
			val map = HashMap<String, String>()
		}
		cursor.close()
		return filePath
	}


	//Bound broadcast receiver
	private fun bindReceiver() {
		//Broadcast receiver
		appUpdateReceiver.onDownloadComplete = {
			downloadComplete(it)
		}
		val filter = IntentFilter()
		filter.addAction("android.intent.action.DOWNLOAD_COMPLETE")
		filter.addAction("android.intent.action.VIEW_DOWNLOADS")
		ContextCenter.getAppContext().registerReceiver(appUpdateReceiver, filter)
	}

	//Unbind broadcast receiver
	internal fun unbindReceiver() {
		try {
			ContextCenter.getAppContext().unregisterReceiver(appUpdateReceiver)
		} catch (e: Exception) {
			//nothing
		}
	}


}
