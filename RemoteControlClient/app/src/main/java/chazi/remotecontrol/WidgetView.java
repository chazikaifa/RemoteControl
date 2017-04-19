package chazi.remotecontrol;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import chazi.remotecontrol.entity.Widget;

/**
 * Created by 595056078 on 2017/4/18.
 */

public class WidgetView extends View {

    private Widget widget;
    private boolean isEditting;

    public WidgetView(Context context) {
        super(context);
    }

    public WidgetView(Context context, Widget widget) {
        super(context);
        new WidgetView(context, widget, false);
    }

    public WidgetView(Context context, final Widget widget, boolean isEdit) {
        super(context);

        this.widget = widget;
        isEditting = isEdit;

        setX(widget.getX());
        setY(widget.getY());

        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.height = widget.getHeight();
        lp.width = widget.getWidth();

        setOnClickListener(new OnClickListener() {
            private boolean isClick = false;

            @Override
            public void onClick(View view) {
                switch (widget.getType()){
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    default:
                }
            }
        });
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {



        return super.onTouchEvent(event);
    }
}
