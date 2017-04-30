package chazi.remotecontrol.WidgetView;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import chazi.remotecontrol.entity.Widget;
import chazi.remotecontrol.utils.Connect;
import chazi.remotecontrol.utils.ContentCreator;
import chazi.remotecontrol.utils.DensityUtil;
import chazi.remotecontrol.utils.Global;

import static java.lang.Math.abs;

/**
 * Created by 595056078 on 2017/4/22.
 */

public class MousePadView extends WidgetView{

    private TextView pad;
    private LinearLayout btn_ll;
    private Button btn_left, btn_right;
    private ImageView spliter;
    private float initX, initY, disX, disY;

    private float dX,dY;
    private float baseDistance;
    private int baseWidth,baseHeight;

    private boolean mouseMoved;
    private int moveFingerId;


    public MousePadView(Context context, Widget widget) {
        super(context,widget);

        pad = new TextView(context);
        btn_ll = new LinearLayout(context);
        btn_left = new Button(context);
        btn_right = new Button(context);
    }

    public Widget getWidget() {
        return widget;
    }

    public void setWidget(Widget widget) {
        this.widget = widget;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        pad.setId(generateViewId());
        pad.setWidth(widget.getWidthInPx(context));
        pad.setHeight(widget.getHeightInPx(context));
        pad.setBackgroundColor(0xff0099cc);
        //pad.setOnTouchListener(this);

        addView(pad);

        btn_ll.setOrientation(LinearLayout.HORIZONTAL);

        LayoutParams ll_lp = new LayoutParams(DensityUtil.dip2px(context, widget.getWidth()), ViewGroup.LayoutParams.WRAP_CONTENT);
        ll_lp.addRule(RelativeLayout.BELOW,pad.getId());
        btn_ll.setLayoutParams(ll_lp);

        int btn_width = (int) (0.5 * DensityUtil.dip2px(context, widget.getWidth() - 5));
        int btn_height = DensityUtil.dip2px(context, 40);

        LayoutParams lp_left = new LayoutParams(btn_width, btn_height);
        lp_left.setMargins(0, 0, 0, 0);
        btn_left.setLayoutParams(lp_left);

        LayoutParams lp_right = new LayoutParams(btn_width, btn_height);
        lp_left.setMargins(0, 0, 0, 0);
        btn_right.setLayoutParams(lp_right);

        spliter = new ImageView(context);
        int spliter_width = DensityUtil.dip2px(context,10);
        LinearLayout.LayoutParams lp_s = new LinearLayout.LayoutParams(spliter_width,btn_height);
        spliter.setLayoutParams(lp_s);
        spliter.setBackgroundColor(Color.BLACK);

        btn_ll.addView(btn_left);
        btn_ll.addView(spliter);
        btn_ll.addView(btn_right);
        addView(btn_ll);
    }

    @Override
    protected void onMove(MotionEvent motionEvent) {
        super.onMove(motionEvent);

        if (disX != 0 || disY != 0) {
            Connect.SendMessage(ContentCreator.move(disX,disY));
        }
    }

    @Override
    protected void onClick(MotionEvent motionEvent) {
        super.onClick(motionEvent);

        Connect.SendMessage(Global.MOUSE_LEFT_CLICK);
    }

    @Override
    protected void onEditPointerDown(MotionEvent motionEvent) {
        super.onEditPointerDown(motionEvent);

//        if(motionEvent.getPointerCount() == 2){
//            dX = motionEvent.getX(1)-motionEvent.getX(0);
//            dY = motionEvent.getY(1)-motionEvent.getY(0);
//
//            baseDistance = Math.abs(dX * dX + dY * dY);
//            baseHeight = pad.getHeight();
//            baseWidth = pad.getWidth();
//
//            Log.i("pointer_down",baseDistance+"");
//        }
    }

    @Override
    protected void onEditMove(MotionEvent motionEvent) {
        super.onEditMove(motionEvent);

//        if(motionEvent.getPointerCount() == 2){
//            dX = motionEvent.getX(1)-motionEvent.getRawX();
//            dY = motionEvent.getY(1)-motionEvent.getRawY();
//
//            float distance = Math.abs(dX * dX + dY * dY);
//            float scale = distance/baseDistance;
//            scaleView(scale);
//
//            Log.i("scale",scale+"");
//        }
    }

    @Override
    protected void onEditUp(MotionEvent motionEvent) {
        super.onEditUp(motionEvent);

//        widget.setHeightInPx(pad.getHeight(),context);
//        widget.setWidthInPx(pad.getWidth(),context);
    }

    public void scaleView(float scale){
        pad.setHeight((int) (baseHeight*scale));
        pad.setWidth((int) (baseWidth*scale));

        if(pad.getHeight()<=200){
            pad.setHeight(200);
        }

        if(pad.getWidth()<=200){
            pad.setWidth(200);
        }

        int base_btn_width = (int) (0.5 * DensityUtil.dip2px(context, baseWidth - 5));

        LayoutParams lp_left = (LayoutParams) btn_left.getLayoutParams();
        LayoutParams lp_right = (LayoutParams) btn_right.getLayoutParams();

        lp_left.width = (int) (base_btn_width * scale);
        lp_right.width = (int) (base_btn_width * scale);

        if(lp_left.width <= 95){
            lp_left.width = 95;
            lp_right.width = 95;
        }

        btn_left.setLayoutParams(lp_left);
        btn_right.setLayoutParams(lp_right);
    }
}
