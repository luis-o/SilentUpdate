package pt.mobilesword.silentupdate.core

import android.os.Bundle
import pt.mobilesword.silentupdate.R
import java.io.Serializable

class UpdateInfo : Serializable {
	var apkUrl: String = ""
	var latestVersion: String = ""
	var title = ContextCenter.getAppContext().getString(R.string.module_silentupdate_update_title)//Update title
	var msg = ContextCenter.getAppContext().getString(R.string.module_silentupdate_update_msg_default)//Updated content
	var isForce = false//Whether it is mandatory
	var extra: Bundle? = null//More parameters can be expanded
}
