package pt.mobilesword.updater;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import pt.mobilesword.silentupdate.SilentUpdate;
import pt.mobilesword.silentupdate.core.UpdateInfo;

public class JavaDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_demo);
       // checkPermission();
        getLatestApk();
    }

    // Get download link step2
    public void getLatestApk() {
        //Specific network request steps
        final String apkUrl = "https://github.com/luis-o/SilentUpdate/raw/08e0a78695088a2439d7b0edbc77873aa94ef30e/app/build/outputs/apk/debug/app-debug.apk";
        //Determine the version number
        final String latestVersion = "1.1.2";
        String currentVersion = BuildConfig.VERSION_NAME;

        //The latest version number field passed to you by the server to latestVersion
        if (latestVersion.compareTo(currentVersion) > 0) {
            Toast.makeText(JavaDemoActivity.this, "Starting download...", Toast.LENGTH_SHORT).show();
            SilentUpdate.INSTANCE.update(new Function1<UpdateInfo, Unit>() {
                @Override
                public Unit invoke(UpdateInfo updateInfo) {
                    updateInfo.setApkUrl(apkUrl);
                    updateInfo.setLatestVersion(latestVersion);
                    updateInfo.setTitle("This is a custom title");
                    updateInfo.setMsg("This is custom content");
                    updateInfo.setForce(true);
                    updateInfo.setExtra(new Bundle());
                    return Unit.INSTANCE;
                }
            });
        }
    }
}
