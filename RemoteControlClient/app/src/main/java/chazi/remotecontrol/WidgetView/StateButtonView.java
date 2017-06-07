package chazi.remotecontrol.WidgetView;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;

import chazi.remotecontrol.R;
import chazi.remotecontrol.entity.Widget;
import chazi.remotecontrol.utils.Connect;
import chazi.remotecontrol.utils.ContentCreator;

/**
 * Created by 595056078 on 2017/4/29.
 */

public class StateButtonView extends WidgetView {

    private String operation;
    private boolean isPress = false;
    private Button btn;

    public StateButtonView(Context context, Widget widget) {
        super(context, widget);

        btn = new Button(context);

        contents = widget.getContent().split("~");
        for(int i = 0;i<contents.length-1;i++){
            if(contents[i].equals("key")){
                operation = contents[i+1];
                break;
            }
        }

        btn.setClickable(false);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        btn.setPadding(10,10,10,10);

        btn.setWidth(widget.getWidthInPx(context));
        btn.setHeight(widget.getHeightInPx(context));

        btn.setTextColor(Color.WHITE);

        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        btn.setText(widget.getName());

        btn.setBackgroundResource(R.drawable.blue_released_background);

        addView(btn);
    }

    @Override
    protected void onDown(MotionEvent motionEvent) {
        super.onDown(motionEvent);

        btn.setBackgroundResource(R.drawable.blue_pressed_background);
    }

    @Override
    protected void onUp(MotionEvent motionEvent) {
        super.onUp(motionEvent);

        btn.setBackgroundResource(R.drawable.blue_released_background);

        if(isPress) {
            Connect.SendMessage(ContentCreator.key(ContentCreator.KEY_RELEASE,operation));
            btn.setTextColor(Color.WHITE);
        }else {
            Connect.SendMessage(ContentCreator.key(ContentCreator.KEY_PRESS,operation));
            btn.setTextColor(Color.BLACK);
        }
        isPress = !isPress;
    }

    public void setOperation(String op){
        operation = op;
        setContent("key",op);
    }

    public String getOperation(){
        return operation;
    }

    @Override
    public void setWidget(Widget widget) {
        super.setWidget(widget);

        setX(widget.getX());
        setY(widget.getY());
        btn.setWidth(widget.getWidthInPx(context));
        btn.setHeight(widget.getHeightInPx(context));

        btn.setText(widget.getName());

        contents = widget.getContent().split("~");
        for(int i = 0;i<contents.length-1;i++){
            if(contents[i].equals("key")){
                operation = contents[i+1];
                break;
            }
        }
    }
}
