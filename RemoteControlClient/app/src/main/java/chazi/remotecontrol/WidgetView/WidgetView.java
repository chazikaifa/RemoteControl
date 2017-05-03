package chazi.remotecontrol.WidgetView;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import chazi.remotecontrol.db.RealmDb;
import chazi.remotecontrol.entity.Widget;
import chazi.remotecontrol.utils.Connect;
import chazi.remotecontrol.utils.Global;
import io.realm.Realm;

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

    protected float initX, initY, disX, disY;
    protected boolean mouseMoved;

    //根据widget的类型，生成不同的视图
    public static WidgetView Creator(Context context, Widget widget) {

        WidgetView v = null;
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
//        this.widget = widget;
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
    }

    protected void onMove(MotionEvent motionEvent) {
        disX = motionEvent.getX(0) - initX;
        disY = motionEvent.getY(0) - initY;
        initX = motionEvent.getX(0);
        initY = motionEvent.getY(0);

        //当移动范围小，当做点击
        if (abs(disX) > 2 && abs(disY) > 2) {
            mouseMoved = true;
        }
    }

    protected void onUp(MotionEvent motionEvent) {
        if (!mouseMoved) {
            onClick(motionEvent);
        }
    }

    protected void onPointerDown(MotionEvent motionEvent) {

    }

    protected void onPointerUp(MotionEvent motionEvent) {

    }

    protected void onClick(MotionEvent motionEvent) {

    }

    protected void onEditDown(MotionEvent motionEvent) {
        initX = motionEvent.getRawX();
        initY = motionEvent.getRawY();

        disX = initX - getX();
        disY = initY - getY();
    }

    protected void onEditMove(MotionEvent motionEvent) {
        initX = motionEvent.getRawX();
        initY = motionEvent.getRawY();

        Log.i("move", "X=" + initX + "   Y=" + initY);

        setX(initX - disX);
        setY(initY - disY);
    }

    protected void onEditUp(MotionEvent motionEvent) {
        widget.setX(getX());
        widget.setY(getY());
    }

    protected void onEditPointerDown(MotionEvent motionEvent) {

    }

    protected void onEditPointerUp(MotionEvent motionEvent) {

    }

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
}
