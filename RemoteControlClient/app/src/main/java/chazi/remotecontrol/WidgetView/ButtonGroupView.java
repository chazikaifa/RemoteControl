package chazi.remotecontrol.WidgetView;

import android.content.Context;
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
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        btn.setPadding(10,10,10,10);

        btn.setWidth(widget.getWidthInPx(context));
        btn.setHeight(widget.getHeightInPx(context));

        addView(btn);
    }

    @Override
    protected void onUp(MotionEvent motionEvent) {
        super.onUp(motionEvent);

        Connect.SendMessage(widget.getContent());
    }

    public void setOperation(String op){
        operation = op;
        widget.setContent(op);
    }
}
