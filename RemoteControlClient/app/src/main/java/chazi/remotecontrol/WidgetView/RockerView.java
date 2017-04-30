package chazi.remotecontrol.WidgetView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import chazi.remotecontrol.entity.Widget;
import chazi.remotecontrol.utils.Connect;
import chazi.remotecontrol.utils.ContentCreator;

/**
 * Created by 595056078 on 2017/4/30.
 */

public class RockerView extends WidgetView {

    private float padRadius, rockerRadius;
    private float centerX, centerY;
    private float rockerX, rockerY;
    private Paint padPaint, rockerPaint;


    private final double TAN_M_675 = Math.tan(-67.5);
    private final double TAN_M_225 = Math.tan(-22.5);
    private final double TAN_225 = Math.tan(22.5);
    private final double TAN_675 = Math.tan(67.5);

    private String[] keys = new String[]{ContentCreator.KEY_W,ContentCreator.KEY_S,ContentCreator.KEY_A,ContentCreator.KEY_D};

    private boolean[] position = new boolean[4],lastPosition = new boolean[4];
    //依次表示上下左右

    public RockerView(Context context, Widget widget) {
        super(context, widget);

        //算出圆心
        padRadius = 0.5f * widget.getWidthInPx(context);

        centerX = widget.getX() + padRadius;
        centerY = widget.getY() + padRadius;

        //初始化摇杆圆心为控件圆心
        rockerX = centerX;
        rockerY = centerY;

        //摇杆半径为整个控件半径的1/5
        rockerRadius = padRadius / 5;

        //看到的控件底板半径还要减去摇杆的半径
        padRadius = padRadius - rockerRadius;

        //底板颜色为淡蓝色
        padPaint = new Paint();
        padPaint.setColor(0xff0099cc);
        padPaint.setAntiAlias(true);

        //摇杆颜色为白色
        rockerPaint = new Paint();
        rockerPaint.setColor(Color.WHITE);
        rockerPaint.setAntiAlias(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        LayoutParams lp = new LayoutParams(widget.getWidthInPx(context), widget.getHeightInPx(context));
        setLayoutParams(lp);
    }

    //画出底板和摇杆
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(centerX, centerX, padRadius, padPaint);
        canvas.drawCircle(rockerX, rockerY, rockerRadius, rockerPaint);
    }

    @Override
    protected void onDown(MotionEvent motionEvent) {
        super.onDown(motionEvent);

        setRockerPosition(initX, initY);

    }

    @Override
    protected void onMove(MotionEvent motionEvent) {
        super.onMove(motionEvent);

        setRockerPosition(initX, initY);
    }

    @Override
    protected void onUp(MotionEvent motionEvent) {
        super.onUp(motionEvent);

        setRockerPosition(centerX, centerY);
    }

    private void setRockerPosition(float x, float y) {
        float dX = x - centerX;
        float dY = y - centerY;

        float r = (float) Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));

        if (r > padRadius) {
            float scale = padRadius / r;
            rockerX = centerX + dX * scale;
            rockerY = centerY + dY * scale;

        } else {
            rockerX = x;
            rockerY = y;
        }

        judgeDirection(dX, dY);

        invalidate();
    }

    private void judgeDirection(float x, float y) {
        lastPosition = position;
        position = new boolean[]{false,false,false,false};

        float r = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(x, 2));

        if (r > rockerRadius) {

            if (x == 0) {
                if (y > 0) {
                    position[1] = true;
                } else {
                    position[0] = true;
                }
            } else {
                double ratio = y / x;
                if (x > 0) {
                    if(ratio<TAN_M_675){
                        position[1] = true;
                    }else if(ratio<TAN_M_225){
                        position[1] = true;
                        position[3] = true;
                    }else if(ratio<TAN_225){
                        position[3] = true;
                    }else if(ratio<TAN_675){
                        position[3] = true;
                        position[1] = true;
                    }else {
                        position[1] = true;
                    }
                } else {
                    if(ratio<TAN_M_675){
                        position[1] = true;
                    }else if(ratio<TAN_M_225){
                        position[1] = true;
                        position[2] = true;
                    }else if(ratio<TAN_225){
                        position[2] = true;
                    }else if(ratio<TAN_675){
                        position[2] = true;
                        position[0] = true;
                    }else {
                        position[0] = true;
                    }
                }
            }
        }

        sendPosition();
    }

    private void sendPosition(){
        String message = "";
        for(int i=0;i<4;i++){
            if(lastPosition[i]&&!position[i]){
                message = ContentCreator.key(ContentCreator.KEY_RELEASE,keys[i],message);
            }
        }

        if(!message.equals("")){
            Connect.SendMessage(message);
        }

        message = "";

        for(int i=0;i<4;i++){
            if(!lastPosition[i]&&position[i]){
                message = ContentCreator.key(ContentCreator.KEY_PRESS,keys[i],message);
            }
        }

        if(!message.equals("")){
            Connect.SendMessage(message);
        }
    }
}
