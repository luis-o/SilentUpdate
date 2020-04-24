package www.weimu.io.silentupdatedemo;

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

/**
 * Java calling method
 */
public class JavaDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_demo);
        checkPermission();

    }

    // Check permission step1
    private void checkPermission() {
        Disposable d = new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
                            getLatestApk();
                        }
                    }
                });
    }


    // Get download link step2
    public void getLatestApk() {
        //Specific network request steps
        final String apkUrl = "https://github.com/luis-o/SilentUpdate/blob/master/deploy/sword.apk";
        //Determine the version number
        final String latestVersion = "1.2.1";
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
