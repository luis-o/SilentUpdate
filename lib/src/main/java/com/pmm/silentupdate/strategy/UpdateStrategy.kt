package com.pmm.silentupdate.strategy

import java.io.File

internal interface UpdateStrategy {

    //Update
    fun update(apkUrl: String, latestVersion: String)

}
