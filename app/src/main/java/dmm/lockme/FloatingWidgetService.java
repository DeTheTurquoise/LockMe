package dmm.lockme;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by ddabrowa on 2018-02-02.
 */

public class FloatingWidgetService extends Service implements View.OnClickListener {

    private WindowManager windowManager;
    private View floatingWidgetView;
    private final static int MOVE_MARGIN = 5;
    private final static int TIME_MARGIN = 300;
    private WindowManager.LayoutParams params;
    private int xWhenPressed = 0;
    private int yWhenPressed = 0;
    private long timeWhenPressed = 0;

//    public FloatingWidgetService(WindowManager.LayoutParams params) {
//        this.params = params;
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        //initialize WindowManager and LayoutInflater
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        //prepare floating widget
        addFloatingWidgetView(inflater);

        //
        floatingWidgetOnTouchListener();
    }

    private void addFloatingWidgetView(LayoutInflater inflater) {
        floatingWidgetView = inflater.inflate(R.layout.lock_widget, null);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.END;

        //Add the view to the window
        windowManager.addView(floatingWidgetView, params);
    }


    private void floatingWidgetOnTouchListener(){
        floatingWidgetView.findViewById(R.id.floating_widget_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                long timeNow = System.currentTimeMillis();
//                int xNow = (int) event.getRawX();
//                int yNow = (int) event.getRawY();
//
//                switch (event.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        xWhenPressed = xNow;
//                        yWhenPressed = yNow;
//                        timeWhenPressed = System.currentTimeMillis();
//                        return true;
//                    case MotionEvent.ACTION_UP:
//                        if (xNow > xWhenPressed + MOVE_MARGIN){
//                            params.gravity = Gravity.START;
//                        }
//                        else if(xNow + MOVE_MARGIN < xWhenPressed){
//                            params.gravity = Gravity.END;
//                        }
//
//                        if (yNow > yWhenPressed + MOVE_MARGIN){
//                            params.gravity = Gravity.BOTTOM;
//                        }
//                        else if(yNow + MOVE_MARGIN < yWhenPressed){
//                            params.gravity = Gravity.TOP;
//                        }
//                        if(timeNow - timeWhenPressed > TIME_MARGIN){
//                            Lock.lockScreen((DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE));
//                        }
//                        return true;
//                }
                Lock.lockScreen((DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE));
                return false;
            }
        });
    }
}
