package chazi.remotecontrol.WidgetView;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import chazi.remotecontrol.R;
import chazi.remotecontrol.entity.Widget;
import chazi.remotecontrol.utils.Connect;
import chazi.remotecontrol.utils.ContentCreator;
import chazi.remotecontrol.utils.DensityUtil;

/**
 * Created by 595056078 on 2017/4/29.
 */

public class WheelView extends WidgetView {

    private int sensitivity = 10;
    private ImageView up,down,split;

    public WheelView(Context context, Widget widget) {
        super(context, widget);

        up = new ImageView(context);
        down = new ImageView(context);
        split = new ImageView(context);

        up.setImageResource(R.drawable.up);
        split.setBackgroundColor(Color.WHITE);
        down.setImageResource(R.drawable.down);

        sensitivity = Integer.valueOf(widget.getContent());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        setBackgroundResource(R.drawable.blue_released_background);

        LayoutParams lp = new LayoutParams(widget.getWidthInPx(context), widget.getHeightInPx(context));
        setLayoutParams(lp);

        int padding = DensityUtil.dip2px(context,7);

        LayoutParams lp_up = new LayoutParams(widget.getWidthInPx(context),widget.getWidthInPx(context));
        lp_up.addRule(ALIGN_PARENT_TOP,TRUE);
        up.setPadding(padding,padding,padding,padding);
        up.setLayoutParams(lp_up);
        up.setScaleType(ImageView.ScaleType.FIT_CENTER);

        LayoutParams lp_sp = new LayoutParams(widget.getWidthInPx(context),DensityUtil.dip2px(context,1));
        lp_sp.addRule(CENTER_IN_PARENT,TRUE);
        lp_sp.setMargins(DensityUtil.dip2px(context,3),0,DensityUtil.dip2px(context,3),0);
        split.setLayoutParams(lp_sp);

        LayoutParams lp_down = new LayoutParams(widget.getWidthInPx(context),widget.getWidthInPx(context));
        lp_down.addRule(ALIGN_PARENT_BOTTOM,TRUE);
        down.setPadding(padding,padding,padding,padding);
        down.setLayoutParams(lp_down);
        down.setScaleType(ImageView.ScaleType.FIT_CENTER);

        addView(up);
        addView(split);
        addView(down);
    }

    @Override
    protected void onMove(MotionEvent motionEvent) {
        disY = motionEvent.getY(0) - initY;
        disX = motionEvent.getX(0) - initX;

        if (Math.abs(disY) > widget.getHeightInPx(context) / 6) {
            initY = motionEvent.getY(0);
            initX = motionEvent.getX(0);

            int symbol;
            if (disY > 0) {
                symbol = -1;
            } else {
                symbol = 1;
            }
            Connect.SendMessage(ContentCreator.wheel(symbol * sensitivity));
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
