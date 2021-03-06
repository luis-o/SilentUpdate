package pt.mobilesword.updater;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import pt.mobilesword.silentupdate.SilentUpdate;
import pt.mobilesword.silentupdate.core.UpdateInfo;

public class UpdaterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_demo);
        getLatestApk();
    }
    
    public void getLatestApk() {
        final String apkUrl = "https://github.com/luis-o/SilentUpdate/raw/cdb58885e26c86947e7816eab7bd55c899f5c24c/app/build/outputs/apk/debug/app-debug.apk";
        final String latestVersion = "0.0.1";  // The latest version number field passed by the server

        String currentVersion = BuildConfig.VERSION_NAME;

        if (latestVersion.compareTo(currentVersion) > 0) {
            SilentUpdate.INSTANCE.update(new Function1<UpdateInfo, Unit>() {
                @Override
                public Unit invoke(UpdateInfo updateInfo) {
                    updateInfo.setApkUrl(apkUrl);
                    updateInfo.setLatestVersion(latestVersion);
                    updateInfo.setTitle("SWORD update");
                    updateInfo.setMsg("Must update reason #1");
                    updateInfo.setForce(true);
                    updateInfo.setExtra(new Bundle());
                    return Unit.INSTANCE;
                }
            });
        }
    }
}
