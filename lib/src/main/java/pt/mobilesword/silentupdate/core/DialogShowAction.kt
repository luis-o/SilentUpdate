package pt.mobilesword.silentupdate.core

import android.content.Context
import android.content.ContextWrapper
import java.io.File

interface DialogShowAction {

	fun show(context: ContextWrapper,
			 updateInfo: UpdateInfo,
			 positiveClick: (() -> Unit),
			 negativeClick: (() -> Unit))
}
