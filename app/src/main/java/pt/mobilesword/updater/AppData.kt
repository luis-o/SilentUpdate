package pt.mobilesword.updater

import android.app.AlertDialog
import android.content.ContextWrapper
import android.view.View
import pt.mobilesword.silentupdate.SilentUpdate
import pt.mobilesword.silentupdate.core.DialogShowAction
import pt.mobilesword.silentupdate.core.UpdateInfo

import android.app.Application
import kotlin.properties.Delegates

abstract class OriginAppData : Application() {

    companion object {
        var context: OriginAppData by Delegates.notNull()
    }

    abstract fun isDebug(): Boolean

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}

class AppData : OriginAppData() {

    override fun isDebug(): Boolean = BuildConfig.DEBUG
    override fun onCreate() {
        super.onCreate()

        SilentUpdate.init(this)
	    // Interval pop-up window reminding time-default reminder after 7 days

        SilentUpdate.intervalDay = 7
        SilentUpdate.downLoadDialogShowAction = object : DialogShowAction {
            override fun show(context: ContextWrapper, updateInfo: UpdateInfo, positiveClick: () -> Unit, negativeClick: () -> Unit) {
                val dialog = AlertDialog.Builder(context)
                        .setCancelable(!updateInfo.isForce)
                        .setTitle(updateInfo.title)
                        .setMessage("Download prompt to remove ${updateInfo.msg}")
                        .setPositiveButton("update immediately", null)
                        .setNegativeButton("Later", null)
                        .create()
                dialog.setOnShowListener {
                    //positive
                    val posBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    posBtn.setOnClickListener {
                        if (!updateInfo.isForce) dialog.dismiss()
                        positiveClick()
                    }
                    val negBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    //negative
                    if (updateInfo.isForce) {
                        negBtn.visibility = View.GONE
                    } else {
                        negBtn.setOnClickListener {
                            dialog.dismiss()
                            negativeClick()
                        }
                    }
                }
                dialog.show()
            }
        }

        SilentUpdate.installDialogShowAction = object : DialogShowAction {
            override fun show(context: ContextWrapper, updateInfo: UpdateInfo, positiveClick: () -> Unit, negativeClick: () -> Unit) {
                val dialog = AlertDialog.Builder(context)
                        .setCancelable(!updateInfo.isForce)
                        .setTitle(updateInfo.title)
                        .setMessage("Installation prompt ${updateInfo.msg}")
                        .setPositiveButton("install now", null)
                        .setNegativeButton("Later", null)
                        .create()
                dialog.setOnShowListener {
                    //positive
                    val posBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    posBtn.setOnClickListener {
                        if (!updateInfo.isForce) dialog.dismiss()
                        positiveClick()
                    }
                    val negBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    //negative
                    if (updateInfo.isForce) {
                        negBtn.visibility = View.GONE
                    } else {
                        negBtn.setOnClickListener {
                            dialog.dismiss()
                            negativeClick()
                        }
                    }
                }
                dialog.show()
            }
        }
    }
}
