package chazi.remotecontrol.WidgetView;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

import chazi.remotecontrol.entity.Widget;
import chazi.remotecontrol.utils.Connect;
import chazi.remotecontrol.utils.ContentCreator;

/**
 * Created by 595056078 on 2017/4/29.
 */

public class ButtonGroupView extends WidgetView {

    private String operation;
    private Button btn;

    public ButtonGroupView(Context context, Widget widget) {
        super(context, widget);

        btn = new Button(context);

        btn.setClickable(false);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        btn.setPadding(10,10,10,10);

        btn.setWidth(widget.getWidthInPx(context));
        btn.setHeight(widget.getHeightInPx(context));

        btn.setText(widget.getName());
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(20);

        addView(btn);
    }

    @Override
    protected void onUp(MotionEvent motionEvent) {
        super.onUp(motionEvent);

        Log.i("ButtonGroup",widget.getContent());

        Connect.SendMessage(widget.getContent());
    }

    public void setOperation(String op){
        operation = op;
        widget.setContent(op);
    }

    @Override
    public void setWidget(Widget widget) {
        super.setWidget(widget);

        setX(widget.getX());
        setY(widget.getY());
        btn.setWidth(widget.getWidthInPx(context));
        btn.setHeight(widget.getHeightInPx(context));

        btn.setText(widget.getName());
    }
}
