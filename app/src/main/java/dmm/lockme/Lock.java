package dmm.lockme;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

public class Lock extends AppCompatActivity{
    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    private static final int DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE = 1222;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DevicePolicyManager mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mDeviceAdminSample = new ComponentName(this, LockReceiver.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE);
        }
        else{
            startService(new Intent(Lock.this, FloatingWidgetService.class));
        }

        if (!mDPM.isAdminActive(mDeviceAdminSample)){
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
            startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
        }
        else {
            lockScreen(mDPM);
        }
        this.finishAffinity();
    }

    public static void lockScreen(DevicePolicyManager devicePolicyManager){
        devicePolicyManager.lockNow();
    }

    public class LockReceiver extends DeviceAdminReceiver{

    }
}
