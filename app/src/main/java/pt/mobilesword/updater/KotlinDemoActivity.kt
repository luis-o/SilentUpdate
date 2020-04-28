package pt.mobilesword.updater

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pmm.ui.helper.RxSchedulers
import io.reactivex.Observable
import pt.mobilesword.silentupdate.SilentUpdate
import java.io.Serializable

class KotlinDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_demo)
        //checkPermission()
        getLatestApk()
    }

    class CheckVersionResultPO(
            val apkUrl: String,
            val latestVersion: String
    ) : Serializable

    // Get download link step2
    private fun getLatestApk() {
        //Specific network request steps
        Observable.just(CheckVersionResultPO(
                //Specific network request steps
                apkUrl ="https://github.com/luis-o/SilentUpdate/raw/08e0a78695088a2439d7b0edbc77873aa94ef30e/app/build/outputs/apk/debug/app-debug.apk",
                latestVersion = "1.1.2"
        )).compose(RxSchedulers.toMain())
                .subscribe {
                    //Determine the version number
                    if (it.latestVersion > BuildConfig.VERSION_NAME) {
                        Toast.makeText(this@KotlinDemoActivity, "Starting download...", Toast.LENGTH_SHORT).show()
                        Log.d("swordupdate", "downloading from here ->" + it.apkUrl)
                        Log.d("swordupdate", "downloading this name->" + it.apkUrl)
                        Log.d("swordupdate", "downloading this version->" + it.latestVersion)
                        SilentUpdate.update {
                            this.apkUrl = it.apkUrl
                            this.latestVersion = it.latestVersion
                            this.msg = "1.bug fix"
                            this.isForce = false
                            this.extra = Bundle()
                        }
                    }
                }
    }
}
