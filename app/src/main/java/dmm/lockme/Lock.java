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


    // when user uses application - lock screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        performLock();
    }

    @Override
    protected void onResume() {
        super.onResume();
        performLock();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        performLock();
    }

    //activate lock widget and lock screen

    private void performLock(){
        //create DevicePolicyManager
        DevicePolicyManager mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        //create component of lock class
        ComponentName mDeviceAdminSample = new ComponentName(this, LockReceiver.class);

        //if you have permission to display alerts (and admin permission) - start lock widget service
        if (permissionToStartService(mDPM,mDeviceAdminSample)){
            startService(new Intent(Lock.this, FloatingWidgetService.class));
        }

        //if you have admin permission - lock screen
        if(permissionToLock(mDPM,mDeviceAdminSample)){
            lockScreen(mDPM);
        }

        //close task
        finish();
    }

    //use DevicePolicyManager to lock screen
    public static void lockScreen(DevicePolicyManager devicePolicyManager){
        devicePolicyManager.lockNow();
    }

    //display request for alert permission
    private void askForDraw(){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE);
        finish();
    }

    //display request for admin permission
    private void askForAdmin(ComponentName mDeviceAdminSample){
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
        finish();
    }

    //function checks if app has permission to display alerts
    private boolean permissionToStartService(DevicePolicyManager mDPM,ComponentName mDeviceAdminSample){
        //check if app has admin permission
        //it is important - service will lock screen and needs this permission
        if(permissionToLock(mDPM,mDeviceAdminSample)){
            //check if app has permission to draw alerts
            if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this))){
                //if does not - display request
                askForDraw();
            }
            else {
                return true;
            }
            //if app didn't have permission at first, check if user added permission by request above
            if(!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this))){
                return true;
            }
        }
        return false;
    }

    private boolean permissionToLock(DevicePolicyManager mDPM,ComponentName mDeviceAdminSample){
        if(!mDPM.isAdminActive(mDeviceAdminSample)){
            askForAdmin(mDeviceAdminSample);
        }
        else {
            return true;
        }
        if (mDPM.isAdminActive(mDeviceAdminSample)){
            return true;
        }
        return false;
    }


    //DeviceAdminReceiver declaration - necessary to start service
    public static class LockReceiver extends DeviceAdminReceiver{
    }


}
