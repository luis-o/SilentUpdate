package pt.mobilesword.updater;

import android.Manifest;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import pt.mobilesword.silentupdate.SilentUpdate;
import pt.mobilesword.silentupdate.core.UpdateInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import pt.mobilesword.updater.R;

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
        final String apkUrl = "https://dl-web.dropbox.com/cd/0/get/A2vxrgpA5V0TpSF9T2csAhLnryzRJHssYkK91251Zm6J21WxoIYwz7wClCDe9KlxM-JeUaSxT1QLSkpfSqDAxgdAhwQbeADmpymLGrrhJ3_PHrc2OYFgclg3GWL9jqekUVs/file?_download_id=60301464889702877457229655221649128202159641787387349396720678167&_notify_domain=www.dropbox.com";
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
                    updateInfo.setForce(false);
                    updateInfo.setExtra(new Bundle());
                    return Unit.INSTANCE;
                }
            });
        }
    }
}
