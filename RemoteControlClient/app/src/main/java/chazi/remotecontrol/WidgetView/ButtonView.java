package chazi.remotecontrol.WidgetView;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import chazi.remotecontrol.entity.Widget;
import chazi.remotecontrol.utils.Connect;
import chazi.remotecontrol.utils.ContentCreator;

/**
 * Created by 595056078 on 2017/4/19.
 */

public class ButtonView extends WidgetView {

    private String operation;
    private Button btn;

    public ButtonView(Context context, Widget widget){
        super(context,widget);

        btn = new Button(context);

        contents = widget.getContent().split("~");
        for(int i = 0;i<contents.length-1;i++){
            if(contents[i].equals("key")){
                operation = contents[i+1];
                break;
            }
        }

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
    protected void onDown(MotionEvent motionEvent) {
        super.onDown(motionEvent);

        Connect.SendMessage(ContentCreator.key(ContentCreator.KEY_PRESS,operation));
    }

    @Override
    protected void onUp(MotionEvent motionEvent) {
        super.onUp(motionEvent);

        Connect.SendMessage(ContentCreator.key(ContentCreator.KEY_RELEASE,operation));
    }

    public void setOperation(String op){
        operation = op;
        setContent("key",op);
    }

    public String getOperation(){
        return operation;
    }


}
