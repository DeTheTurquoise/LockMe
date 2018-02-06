package dmm.lockme;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
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
    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
    private Point szWindow = new Point();

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

        //init WindowManager
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        //Init LayoutInflater
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        addFloatingWidgetView(inflater);
        floatingWidgetOnTouchListener();
    }

    private void addFloatingWidgetView(LayoutInflater inflater) {
        //Inflate the floating view layout we created
        floatingWidgetView = inflater.inflate(R.layout.lock_widget, null);

        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;

        //Add the view to the window
        windowManager.addView(floatingWidgetView, params);
    }


    private void floatingWidgetOnTouchListener(){
        floatingWidgetView.findViewById(R.id.floating_widget_layout).setOnTouchListener(new View.OnTouchListener() {

            long timeStart = 0;
            long timeEnd = 0;

            boolean isLongClick = false;//variable to judge if user click long press

            Handler handler_longClick = new Handler();
            Runnable runnable_longClick = new Runnable() {
                @Override
                public void run() {
                    //On Floating Widget Long Click

                    //Set isLongClick as true
                    isLongClick = true;

                //    onFloatingWidgetLongClick();
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Get Floating widget view params
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) floatingWidgetView.getLayoutParams();

                //get the touch location coordinates
                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();

                int x_cord_Destination, y_cord_Destination;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        timeStart = System.currentTimeMillis();

                        handler_longClick.postDelayed(runnable_longClick, 600);

                        x_init_cord = x_cord;
                        y_init_cord = y_cord;

                        //remember the initial position.
                        x_init_margin = layoutParams.x;
                        y_init_margin = layoutParams.y;

                        return true;
                    case MotionEvent.ACTION_UP:
                        isLongClick = false;
                        handler_longClick.removeCallbacks(runnable_longClick);

                        //Get the difference between initial coordinate and current coordinate
                        int x_diff = x_cord - x_init_cord;
                        int y_diff = y_cord - y_init_cord;

                        //The check for x_diff <5 && y_diff< 5 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5) {
                            timeEnd = System.currentTimeMillis();

                            //Also check the difference between start time and end time should be less than 300ms
                            if ((timeEnd - timeStart) < 300)
                                onFloatingWidgetClick();

                        }

                        y_cord_Destination = y_init_margin + y_diff;

//                        int barHeight = getStatusBarHeight();
                        int barHeight = 20;
                        if (y_cord_Destination < 0) {
                            y_cord_Destination = 0;
                        } else if (y_cord_Destination + (floatingWidgetView.getHeight() + barHeight) > szWindow.y) {
                            y_cord_Destination = szWindow.y - (floatingWidgetView.getHeight() + barHeight);
                        }

                        layoutParams.y = y_cord_Destination;

//                        inBounded = false;

                        //reset position if user drags the floating view
//                        resetPosition(x_cord);

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int x_diff_move = x_cord - x_init_cord;
                        int y_diff_move = y_cord - y_init_cord;

                        x_cord_Destination = x_init_margin + x_diff_move;
                        y_cord_Destination = y_init_margin + y_diff_move;

                        //If user long click the floating view, update remove view
                        if (isLongClick) {

                                //Update the layout with new X & Y coordinate
                                windowManager.updateViewLayout(floatingWidgetView, layoutParams);
                                break;
                            } else {
                                //If Floating window gets out of the Remove view update Remove view again
//                                inBounded = false;
                                onFloatingWidgetClick();
                            }


//                        layoutParams.x = x_cord_Destination;
//                        layoutParams.y = y_cord_Destination;

                        //Update the layout with new X & Y coordinate
                        windowManager.updateViewLayout(floatingWidgetView, layoutParams);
                        return true;
                }
                return false;
            }
        });
    }
    private void onFloatingWidgetClick() {
        Lock.lockScreen((DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE));
    }

}
