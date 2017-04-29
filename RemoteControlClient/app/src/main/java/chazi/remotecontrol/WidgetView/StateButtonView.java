package chazi.remotecontrol.WidgetView;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import chazi.remotecontrol.entity.Widget;
import chazi.remotecontrol.utils.Connect;

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

        if(isPress) {
            Connect.SendMessage("keyUp~" + operation);
        }else {
            Connect.SendMessage("keyDown~" + operation);
        }
    }

    public void setOperation(String op){
        operation = op;
        setContent("key",op);
    }

    public String getOperation(){
        return operation;
    }
}
