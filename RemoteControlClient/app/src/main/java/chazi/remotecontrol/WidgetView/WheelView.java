package chazi.remotecontrol.WidgetView;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import chazi.remotecontrol.entity.Widget;
import chazi.remotecontrol.utils.Connect;
import chazi.remotecontrol.utils.ContentCreator;

/**
 * Created by 595056078 on 2017/4/29.
 */

public class WheelView extends WidgetView {

    private int sensitivity = 1;
    private ImageView v;

    public WheelView(Context context, Widget widget) {
        super(context, widget);

        v = new ImageView(context);

        contents = widget.getContent().split("~");
        for (int i = 0; i < contents.length - 1; i++) {
            if (contents[i].equals("sen")) {
                sensitivity = Integer.valueOf(contents[i + 1]);
                break;
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        LayoutParams lp = new LayoutParams(widget.getWidthInPx(context), widget.getHeightInPx(context));
        v.setLayoutParams(lp);

    }

    @Override
    protected void onMove(MotionEvent motionEvent) {
        disY = motionEvent.getY(0) - initY;

        if (disY > widget.getHeightInPx(context) / 6) {
            initX = motionEvent.getY(0);

            int symbol;
            if (disY > 0) {
                symbol = -1;
            } else {
                symbol = 1;
            }
            Connect.SendMessage(ContentCreator.wheel(symbol*sensitivity));
        }
    }

    public int getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(int sensitivity) {
        this.sensitivity = sensitivity;

        setContent("sen", sensitivity + "");
    }


}
