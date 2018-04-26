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
    private final static int TIME_MARGIN = 100;
    private WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);
    private int xWhenPressed = 0;
    private int yWhenPressed = 0;
    private long timeWhenPressed = 0;
    private boolean isTop = true;
    private boolean isRight = true;
//
//    public boolean isServiceActive() {
//        return isServiceActive;
//    }
//
//    private boolean isServiceActive = false;


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

        //prepare widget listener
        floatingWidgetOnTouchListener();
    }


    private void addFloatingWidgetView(LayoutInflater inflater) {
        //set layout to widget
        floatingWidgetView = inflater.inflate(R.layout.lock_widget, null);

        //Specify the view position - right top corner
        params.gravity = Gravity.TOP | Gravity.END;

        //Add the view to the window
        windowManager.addView(floatingWidgetView, params);
    }


    private void floatingWidgetOnTouchListener(){
        //implement onTouchListener
        floatingWidgetView.findViewById(R.id.floating_widget_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            //returns true when action was performed
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    //ACTION_DOWN - when widget is pressed
                    case MotionEvent.ACTION_DOWN:
                        //set values to basic variables - check position and time of click
                        xWhenPressed = (int) event.getRawX();
                        yWhenPressed = (int) event.getRawY();
                        timeWhenPressed = System.currentTimeMillis();
                        return true;
                    //ACTION_UP - when pressed gesture has finished
                    case MotionEvent.ACTION_UP:
                        //check current time
                        long timeNow = System.currentTimeMillis();
                        //calculate gesture position change when widget was pressed
                        int xDiff = (int) event.getRawX() - xWhenPressed;
                        int yDiff = (int) event.getRawY() - yWhenPressed;
                        //check if gesture was a move to top right corner
                        if(((xDiff > 0)&&(yDiff < 0))||
                                ((xDiff > 0)&& (yDiff == 0)&& isTop)||
                                ((xDiff == 0) && (yDiff < 0) && isRight)){
                            //change values of current widget position
                            isRight = true;
                            isTop = true;
                            //set new position to top right
                            params.gravity = Gravity.TOP | Gravity.END;
                        }
                        //check if gesture was a move to bottom right corner
                        else if(((xDiff > 0)&&(yDiff > 0))||
                                ((xDiff > 0)&& (yDiff == 0)&& !isTop)||
                                ((xDiff == 0) && (yDiff > 0) && isRight)){
                            isRight = true;
                            isTop = false;
                            params.gravity = Gravity.BOTTOM | Gravity.END;
                        }
                        //check if gesture was a move to bottom left corner
                        else if(((xDiff == 0) && (yDiff > 0) && !isRight)||
                                ((xDiff < 0)&& (yDiff == 0)&& !isTop)||
                                ((xDiff < 0)&& (yDiff > 0))){
                            isRight = false;
                            isTop = false;
                            params.gravity = Gravity.BOTTOM | Gravity.START;
                        }
                        //check if gesture was a move to top left
                        else if(((xDiff == 0) && (yDiff > 0)&& !isRight)||
                                ((xDiff < 0)&& (yDiff < 0))||
                                ((xDiff < 0)&& (yDiff == 0)&& isTop)){
                            isRight = false;
                            isTop = true;
                            params.gravity = Gravity.TOP | Gravity.START;
                        }

                        //update new widget position if it was changed
                        windowManager.updateViewLayout(floatingWidgetView,params);

                        //if user just clicked this widget then lock screen
                        if(timeNow - timeWhenPressed < TIME_MARGIN){
                            Lock.lockScreen((DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE));
                        }
                        return true;
                }
                return false;
            }
        });
    }
}
