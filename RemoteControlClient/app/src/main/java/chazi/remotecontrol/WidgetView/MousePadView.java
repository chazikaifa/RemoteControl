package chazi.remotecontrol.WidgetView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import chazi.remotecontrol.MyOnTouchListener;
import chazi.remotecontrol.R;
import chazi.remotecontrol.entity.Widget;
import chazi.remotecontrol.utils.Connect;
import chazi.remotecontrol.utils.ContentCreator;
import chazi.remotecontrol.utils.DensityUtil;
import chazi.remotecontrol.utils.Global;

import static java.lang.Math.abs;

/**
 * Created by 595056078 on 2017/4/22.
 */

public class MousePadView extends WidgetView {


    private TextView pad;
    private LinearLayout btn_ll;
    private RelativeLayout pad_rl;
    private Button btn_left, btn_right;

    private GradientView gradientView;
    private final int GRADIENT_VIEW_HEIGHT = 30;

    private float sensitive;

//    private float baseDistance;
//    private int baseWidth, baseHeight;

    public MousePadView(Context context, Widget widget) {
        super(context, widget);

        sensitive = Integer.parseInt(widget.getContent()) / 1000f;

        pad = new TextView(context);
        pad_rl = new RelativeLayout(context);
        gradientView = new GradientView(context, context.getResources().getColor(R.color.dark_blue));
        btn_ll = new LinearLayout(context);
        btn_left = new Button(context);
        btn_right = new Button(context);

        btn_left.setOnTouchListener(listener_left);
        btn_right.setOnTouchListener(listener_right);
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

        //由于触摸板包含不同控件，取消本体的触摸监听，让pad监听
        setOnTouchListener(null);

        LayoutParams pad_lp = new LayoutParams(widget.getWidthInPx(context), widget.getHeightInPx(context));
        pad_rl.setLayoutParams(pad_lp);
        pad_rl.setId(generateViewId());

        pad.setId(generateViewId());
        pad.setWidth(widget.getWidthInPx(context));
        pad.setHeight(widget.getHeightInPx(context));
        pad.setBackgroundResource(R.drawable.round_top);
        pad.setOnTouchListener(listener);

        pad_rl.addView(pad);

        LayoutParams gv_lp = new LayoutParams(DensityUtil.dip2px(context, GRADIENT_VIEW_HEIGHT), DensityUtil.dip2px(context, GRADIENT_VIEW_HEIGHT));
        gradientView.setLayoutParams(gv_lp);
        gradientView.setVisibility(GONE);

        pad_rl.addView(gradientView);

        addView(pad_rl);

        ImageView v = new ImageView(context);
        v.setId(generateViewId());
        RelativeLayout.LayoutParams v_lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context, 2));
        v_lp.addRule(RelativeLayout.BELOW, pad_rl.getId());
        v.setLayoutParams(v_lp);
        v.setBackgroundColor(getResources().getColor(R.color.background_gray));

        addView(v);

        btn_ll.setOrientation(LinearLayout.HORIZONTAL);

        LayoutParams ll_lp = new LayoutParams(DensityUtil.dip2px(context, widget.getWidth()), ViewGroup.LayoutParams.WRAP_CONTENT);
        ll_lp.addRule(RelativeLayout.BELOW, v.getId());
        btn_ll.setLayoutParams(ll_lp);

        int btn_width = (int) (0.5 * DensityUtil.dip2px(context, widget.getWidth() - 2));
        int btn_height = DensityUtil.dip2px(context, 40);

        LayoutParams lp_left = new LayoutParams(btn_width, btn_height);
        lp_left.setMargins(0, 0, 0, 0);
        btn_left.setLayoutParams(lp_left);
        btn_left.setBackgroundResource(R.drawable.round_bottom_left);

        LayoutParams lp_right = new LayoutParams(btn_width, btn_height);
        lp_left.setMargins(0, 0, 0, 0);
        btn_right.setLayoutParams(lp_right);
        btn_right.setBackgroundResource(R.drawable.round_bottom_right);

        ImageView spliter = new ImageView(context);
        int spliter_width = DensityUtil.dip2px(context, 2);
        LinearLayout.LayoutParams lp_s = new LinearLayout.LayoutParams(spliter_width, btn_height);
        spliter.setLayoutParams(lp_s);
        spliter.setBackgroundColor(getResources().getColor(R.color.background_gray));

        btn_ll.addView(btn_left);
        btn_ll.addView(spliter);
        btn_ll.addView(btn_right);
        addView(btn_ll);
    }

    @Override
    protected void onDown(MotionEvent motionEvent) {
        super.onDown(motionEvent);

        gradientView.setVisibility(VISIBLE);
        gradientView.setX(motionEvent.getX() - GRADIENT_VIEW_HEIGHT);
        gradientView.setY(motionEvent.getY() - GRADIENT_VIEW_HEIGHT);
    }

    @Override
    protected void onUp(MotionEvent motionEvent) {
        super.onUp(motionEvent);

        gradientView.setVisibility(GONE);
    }

    @Override
    protected void onMove(MotionEvent motionEvent) {
        super.onMove(motionEvent);

        gradientView.setX(motionEvent.getX() - GRADIENT_VIEW_HEIGHT);
        gradientView.setY(motionEvent.getY() - GRADIENT_VIEW_HEIGHT);

        if (disX != 0 || disY != 0) {
            Connect.SendMessage(ContentCreator.move(disX * sensitive, disY * sensitive));
        }
    }

    @Override
    protected void onClick(MotionEvent motionEvent) {
        super.onClick(motionEvent);

        Connect.SendMessage(ContentCreator.Click(ContentCreator.MOUSE_CLICK_LEFT));
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

//    public void scaleView(float scale) {
//        pad.setHeight((int) (baseHeight * scale));
//        pad.setWidth((int) (baseWidth * scale));
//
//        if (pad.getHeight() <= 200) {
//            pad.setHeight(200);
//        }
//
//        if (pad.getWidth() <= 200) {
//            pad.setWidth(200);
//        }
//
//        int base_btn_width = (int) (0.5 * DensityUtil.dip2px(context, baseWidth - 5));
//
//        LayoutParams lp_left = (LayoutParams) btn_left.getLayoutParams();
//        LayoutParams lp_right = (LayoutParams) btn_right.getLayoutParams();
//
//        lp_left.width = (int) (base_btn_width * scale);
//        lp_right.width = (int) (base_btn_width * scale);
//
//        if (lp_left.width <= 95) {
//            lp_left.width = 95;
//            lp_right.width = 95;
//        }
//
//        btn_left.setLayoutParams(lp_left);
//        btn_right.setLayoutParams(lp_right);
//    }


    //自定义View，一个颜色渐变的圆圈
    private class GradientView extends android.support.v7.widget.AppCompatImageView {
        private int color = 0xffffff;
        private int height, width, radius;
        private float circleRadius, X, Y;
        private Paint paint;

        public GradientView(Context context, int backgroundColor) {
            super(context);
            color = backgroundColor;

            init();
        }

        public GradientView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);

            init();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

//        setBackgroundColor(0xff3191E8);

            for (int i = 100; i > 0; i--) {
                paint.setAlpha(100 - i);
                canvas.drawCircle(X, Y, circleRadius * i, paint);
            }
        }

        private void init() {
            paint = new Paint();
            paint.setAntiAlias(true);//设置画笔去锯齿，没有此语句，画的线或图片周围不圆滑
            paint.setColor(color);

            ViewTreeObserver vto = getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    height = getMeasuredHeight();
                    width = getMeasuredWidth();

                    radius = height > width ? width : height;
                    circleRadius = radius / 200f;

                    X = width / 2f;
                    Y = height / 2f;

                    paint.setStrokeWidth(circleRadius);
                    return true;
                }
            });
        }

        public int getColor() {
            return color;
        }

        public void setColor(int backgroundColor) {
            color = backgroundColor;
            paint.setColor(color);
            invalidate();
        }
    }

    private OnTouchListener listener_left = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_BUTTON_PRESS:
                case MotionEvent.ACTION_DOWN:
                    if(!isEdit()) {
                        Connect.SendMessage(ContentCreator.Click(ContentCreator.MOUSE_PRESS_LEFT));
                    }
                    v.setBackgroundResource(R.drawable.round_bottom_left_pressed);
                    break;
                case MotionEvent.ACTION_BUTTON_RELEASE:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_HOVER_EXIT:
                    if(!isEdit()) {
                        Connect.SendMessage(ContentCreator.Click(ContentCreator.MOUSE_RELEASE_LEFT));
                    }
                    v.setBackgroundResource(R.drawable.round_bottom_left);
                    break;
            }

            return false;
        }
    };

    private OnTouchListener listener_right = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_BUTTON_PRESS:
                case MotionEvent.ACTION_DOWN:
                    if(!isEdit()) {
                        Connect.SendMessage(ContentCreator.Click(ContentCreator.MOUSE_PRESS_RIGHT));
                    }
                    v.setBackgroundResource(R.drawable.round_bottom_right_pressed);
                    break;
                case MotionEvent.ACTION_BUTTON_RELEASE:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_HOVER_EXIT:
                    if(!isEdit()) {
                        Connect.SendMessage(ContentCreator.Click(ContentCreator.MOUSE_RELEASE_RIGHT));
                    }
                    v.setBackgroundResource(R.drawable.round_bottom_right);
                    break;
            }

            return false;
        }
    };
}
