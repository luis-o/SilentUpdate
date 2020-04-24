package pt.mobilesword.silentupdate.core

import android.os.Environment

internal object Const {
	//Update file address Specify the default folder [download]
	val UPDATE_FILE_DIR = Environment.getExternalStorageDirectory().toString() + "/" + Environment.DIRECTORY_DOWNLOADS + "/"
	//Notified channel
	const val NOTIFICATION_CHANNEL_ID = "silentUpdate_Notification_Channel_ID"
}
