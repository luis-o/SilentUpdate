package www.weimu.io.silentupdatedemo

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import com.pmm.silentupdate.SilentUpdate
import com.pmm.ui.helper.RxSchedulers
import io.reactivex.Observable
import java.io.Serializable

/**
 *How to call kotlin
 */
class KotlinDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_demo)
        checkPermission()
    }


// Check permission step1
    private fun checkPermission() {
        val d = RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { granted ->
                    if (granted) getLatestApk()
                }
    }

    class CheckVersionResultPO(
            val apkUrl: String,
            val latestVersion: String
    ) : Serializable

    //Get download link step2
    private fun getLatestApk() {
        //Specific network request steps
        val d = Observable.just(CheckVersionResultPO(
                apkUrl = "https://download.sj.qq.com/upload/connAssitantDownload/upload/MobileAssistant_1.apk",
                latestVersion = "1.1.2"
        )).compose(RxSchedulers.toMain())
                .subscribe {
                    //Determine the version number
                    if (it.latestVersion > BuildConfig.VERSION_NAME) {
                        Toast.makeText(this@KotlinDemoActivity, "Starting download...", Toast.LENGTH_SHORT).show()

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
