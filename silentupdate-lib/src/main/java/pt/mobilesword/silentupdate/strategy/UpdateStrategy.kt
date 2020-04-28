package pt.mobilesword.silentupdate.strategy

import java.io.File

internal interface UpdateStrategy {
    fun update(apkUrl: String, latestVersion: String)
}
