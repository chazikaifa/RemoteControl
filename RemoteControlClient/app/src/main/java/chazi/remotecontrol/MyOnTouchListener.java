package chazi.remotecontrol;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by 595056078 on 2017/5/15.
 */

public class MyOnTouchListener implements View.OnTouchListener {

    private Context context;
    private int img_normal = 0,img_selected = 0;

    public MyOnTouchListener(Context context){
        this.context = context;
    }

    public MyOnTouchListener(Context context,int img_normal, int img_selected){
        this.context = context;
        this.img_normal = img_normal;
        this.img_selected = img_selected;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_BUTTON_PRESS:
                if(v.getClass().equals(ImageView.class)){
                    if(img_selected!=0) {
                        ((ImageView) v).setImageResource(img_selected);
                    }
                }
                v.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
                break;
            case MotionEvent.ACTION_BUTTON_RELEASE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(v.getClass().equals(ImageView.class)) {
                    if (img_normal != 0) {
                        ((ImageView) v).setImageResource(img_normal);
                    }
                }
                v.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                break;
        }

        return false;
    }
}
