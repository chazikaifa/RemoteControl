package chazi.remotecontrol.WidgetView;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import chazi.remotecontrol.entity.Widget;

import static java.lang.Math.abs;

/**
 * Created by 595056078 on 2017/4/28.
 */

public class WidgetView extends RelativeLayout {

    protected Widget widget;
    protected boolean isEdit = false;
    protected boolean isFocus = false;
    protected String[] contents = new String[0];
    protected Context context;
    protected float initX, initY, disX, disY,dX,dY;
    protected boolean mouseMoved;
    protected Thread longClickThread,editLongClickThread;

    public static final String[] WidgetStyleList = {
            "按键",
            "状态按键",
            "滚轮",
            "触摸板",
            "输入框",
            "组合键",
            "摇杆"
    };

    public static final String[] WidgetSytleDescription = {
            "完全模拟电脑键盘的单个按键",
            "使键盘单个按键按下或松开",
            "模拟电脑鼠标滚轮滚动",
            "模拟电脑触摸板",
            "发送输入的字符串到电脑并执行粘贴命令",
            "执行连续的一串指令",
            "模拟手柄的摇杆"
    };

    //根据widget的类型，生成不同的视图
    public static WidgetView Creator(Context context, Widget widget) {

        WidgetView v;
        switch (widget.getType()) {
            case 1:
                v = new ButtonView(context, widget);
                break;
            case 2:
                v = new StateButtonView(context, widget);
                break;
            case 3:
                v = new WheelView(context, widget);
                break;
            case 4:
                v = new MousePadView(context, widget);
                break;
            case 5:
                v = new InputView(context, widget);
                break;
            case 6:
                v = new ButtonGroupView(context, widget);
                break;
            case 7:
                v = new RockerView(context, widget);
                break;
            default:
                v = new ButtonView(context, widget);
                break;
        }
        return v;
    }

    public WidgetView(Context context) {
        super(context);
    }

    public WidgetView(Context context, Widget widget) {
        super(context);
        this.context = context;
        this.widget = new Widget(widget);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        setX(widget.getX());
        setY(widget.getY());

        setOnTouchListener(listener);
    }

    protected OnTouchListener listener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            int action = motionEvent.getActionMasked();

            if (!isEdit) {

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        onDown(motionEvent);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        onPointerDown(motionEvent);
                        break;

                    case MotionEvent.ACTION_POINTER_UP:
                        onPointerUp(motionEvent);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        onMove(motionEvent);
                        break;

                    case MotionEvent.ACTION_UP:
                        onUp(motionEvent);
                        break;
                }

            } else {
//                if (isFocus) {
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        onEditDown(motionEvent);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        onEditPointerDown(motionEvent);
                        break;

                    case MotionEvent.ACTION_POINTER_UP:
                        onEditPointerUp(motionEvent);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        onEditMove(motionEvent);
                        break;
                    case MotionEvent.ACTION_UP:
                        onEditUp(motionEvent);
                        break;
                }
//                }
            }

            return true;
        }
    };

    protected void onDown(MotionEvent motionEvent) {
        initX = motionEvent.getX(0);
        initY = motionEvent.getY(0);
        mouseMoved = false;

        longClickThread = new LongClickThread(false);
        longClickThread.start();
    }

    protected void onMove(MotionEvent motionEvent) {
        disX = motionEvent.getX(0) - initX;
        disY = motionEvent.getY(0) - initY;
        initX = motionEvent.getX(0);
        initY = motionEvent.getY(0);

        //当移动范围小，当做点击,取消长按
        if (abs(disX) > 2 && abs(disY) > 2) {
            mouseMoved = true;
            longClickThread.interrupt();
        }
    }

    protected void onUp(MotionEvent motionEvent) {
        longClickThread.interrupt();
        if (!mouseMoved) {
            onClick(motionEvent);
        }
    }

    protected void onPointerDown(MotionEvent motionEvent) {
        longClickThread.interrupt();
    }

    protected void onPointerUp(MotionEvent motionEvent) {

    }

    protected void onClick(MotionEvent motionEvent) {

    }

    protected void onEditDown(MotionEvent motionEvent) {
        initX = motionEvent.getRawX();
        initY = motionEvent.getRawY();

        dX = initX - getX();
        dY = initY - getY();

        editLongClickThread = new LongClickThread(true);
        editLongClickThread.start();
    }

    protected void onEditMove(MotionEvent motionEvent) {
        disX = motionEvent.getRawX() - initX;
        disY = motionEvent.getRawY() - initY;

        initX = motionEvent.getRawX();
        initY = motionEvent.getRawY();
        //当移动范围小，当做点击,取消长按
        if (abs(disX) > 2 && abs(disY) > 2) {
            editLongClickThread.interrupt();
        }

        Log.i("move", "X=" + initX + "   Y=" + initY);

        setX(initX - dX);
        setY(initY - dY);

    }

    protected void onEditUp(MotionEvent motionEvent) {
        widget.setX(getX());
        widget.setY(getY());

        editLongClickThread.interrupt();
    }

    protected void onEditPointerDown(MotionEvent motionEvent) {
        editLongClickThread.interrupt();
    }

    protected void onEditPointerUp(MotionEvent motionEvent) {

    }

    public interface OnLongClickListener{
        void onLongClick();
    }
    public interface OnEditLongClickListener{
        void onEditLongClick();
    }

    protected OnLongClickListener onLongClick = new OnLongClickListener() {
        @Override
        public void onLongClick() {

        }
    };

    protected OnEditLongClickListener onEditLongClick = new OnEditLongClickListener() {
        @Override
        public void onEditLongClick() {

        }
    };

    public void setContent(String name, String value) {
        for (int i = 0; i < contents.length - 1; i++) {
            if (contents[i].equals(name)) {
                contents[i + 1] = value;
                break;
            }
        }
        widget.setContent(linkContent());
    }

    public String linkContent() {
        String content = "";
        for (String s : contents) {
            if (content.equals("")) {
                content = s;
            } else {
                content += "~" + s;
            }
        }
        return content;
    }

    public Widget getWidget() {
        return widget;
    }

    public void setWidget(Widget widget) {
        this.widget = new Widget(widget);
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public boolean isFocus() {
        return isFocus;
    }

    public void setFocus(boolean focus) {
        isFocus = focus;
    }

    public void setOnLongClickListener(OnLongClickListener listener){
        onLongClick = listener;
    }

    public void setOnEditLongClickListener(OnEditLongClickListener listener){
        onEditLongClick = listener;
    }

    private class LongClickThread extends Thread{
        private boolean isEdit;

        private LongClickThread(boolean isEdit) {
            this.isEdit = isEdit;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                if(!isInterrupted()){
                    if(isEdit()){
                        onEditLongClick.onEditLongClick();
                    }else {
                        onLongClick.onLongClick();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
