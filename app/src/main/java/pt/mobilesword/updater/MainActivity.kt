package pt.mobilesword.updater

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

import pt.mobilesword.silentupdate.SilentUpdate

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val textView = findViewById(R.id.textViewVersion) as TextView
        textViewVersion.text = BuildConfig.VERSION_NAME

        btnJava.setOnClickListener {
            SilentUpdate.clearCache()
            startActivity(Intent(this, UpdaterActivity::class.java))
        }
    }
}
