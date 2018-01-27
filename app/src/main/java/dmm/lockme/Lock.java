package dmm.lockme;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Lock extends AppCompatActivity{
    protected static final int REQUEST_CODE_ENABLE_ADMIN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DevicePolicyManager mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mDeviceAdminSample = new ComponentName(this, LockReceiver.class);
        if (!mDPM.isAdminActive(mDeviceAdminSample)){
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
            startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
        }
        else {
            mDPM.lockNow();
        }
        this.finishAffinity();
    }

    public class LockReceiver extends DeviceAdminReceiver{

    }
}
