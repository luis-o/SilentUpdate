package www.weimu.io.silentupdatedemo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import pt.mobilesword.silentupdate.SilentUpdate

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //kotlin
        btnKotlin.setOnClickListener {
            startActivity(Intent(this, KotlinDemoActivity::class.java))
        }
        //java
        btnJava.setOnClickListener {
            startActivity(Intent(this, JavaDemoActivity::class.java))
        }
        //clear cache
        btnClearCache.setOnClickListener {
            SilentUpdate.clearCache()
            toast("Successfully cleared the cache")
        }
        //delete apk
        btnDeleteApk.setOnClickListener {
            if (SilentUpdate.deleteApk(version = "1.1.1"))
                toast("successfully deleted")
            else
                toast("failed to delete")
        }
    }


}
