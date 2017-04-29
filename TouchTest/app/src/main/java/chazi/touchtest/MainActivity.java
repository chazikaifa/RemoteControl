package chazi.touchtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private RelativeLayout container;

    private float initX =0;
    private float initY =0;

    private float disX =0;
    private float disY =0;

    private float initX_2 = 0;
    private float initY_2 = 0;

    private float dX_1 = 0;
    private float dY_1 = 0;

    private float dX_2 = 0;
    private float dY_2 = 0;

    private float dX = 0;
    private float dY = 0;

    float baseValue;
    int baseWidth,baseHeight;

    private int container_height,container_width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.iv);
        container = (RelativeLayout) findViewById(R.id.container);

        //container.setOnTouchListener(listener);
        imageView.setOnTouchListener(listener);

        ViewTreeObserver greenObserver = container.getViewTreeObserver();
        greenObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                container.getViewTreeObserver().removeOnPreDrawListener(this);
                container_height = container.getHeight();
                container_width = container.getWidth();

                Log.i("container","height = "+container_height+"  width="+container_width);
                return true;
            }
        });
    }

    private View.OnTouchListener listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getActionMasked();
            int count = motionEvent.getPointerCount();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    //save X and Y positions when user touches the TextView

                    initX = motionEvent.getRawX();
                    initY = motionEvent.getRawY();

                    disX = initX - imageView.getX();
                    disY = initY - imageView.getY();

                    Log.i("down","X="+initX+"   Y="+initY);

                    break;

                case MotionEvent.ACTION_POINTER_DOWN:

                    if(count == 2){
//                        dX = Math.abs(motionEvent.getX(1)-motionEvent.getX());
//                        dY = Math.abs(motionEvent.getY(1)-motionEvent.getY());
                        dX = motionEvent.getX(1)-motionEvent.getX();
                        dY = motionEvent.getY(1)-motionEvent.getY();


                        baseValue = (float) Math.sqrt(dX * dX + dY * dY);
                        baseHeight = imageView.getHeight();
                        baseWidth = imageView.getWidth();

                        Log.i("Pointer_Down","dX = "+dX+" dy="+ dY);
                    }

                case MotionEvent.ACTION_MOVE:

                    initX = motionEvent.getRawX();
                    initY = motionEvent.getRawY();

                    Log.i("move", "X=" + initX + "   Y=" + initY);

                    imageView.setX(initX - disX);
                    imageView.setY(initY - disY);

                    if(count == 2){
                        dX = motionEvent.getX(1)-motionEvent.getX();
                        dY = motionEvent.getY(1)-motionEvent.getY();


                        float value = (float) Math.sqrt(dX * dX + dY * dY);// 计算两点的距离
//                        if (baseValue == 0) {
//                            baseValue = value;
//                        }
//                        else {
                            if (value - baseValue >= 10 || value - baseValue <= -10) {
                                float scale = value / baseValue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
                                //img_scale(scale);  //缩放图片

                                ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                                lp.width = (int) (baseWidth * scale);
                                lp.height = (int) (baseHeight * scale);

                                if(lp.width <= 0)
                                    lp.width = 1;

                                if(lp.height <= 0)
                                    lp.height = 1;

                                imageView.setLayoutParams(lp);
                            }
//                        }
//                        float ddX = Math.abs(motionEvent.getX(1)-motionEvent.getX())-dX;
//                        float ddY = Math.abs(motionEvent.getY(1)-motionEvent.getY())-dY;
//
//                        Log.i("move dd","ddX = "+ddX+" ddy="+ ddY);
//
//                        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
//                        lp.width = (int)(lp.width + ddX);
//                        if(lp.width>container_width){
//                            lp.width = container_width;
//                        }
//
//                        if(lp.width<0){
//                            lp.width = 1;
//                        }
//
//                        lp.height = (int)(lp.height + ddY);
//                        if(lp.height>container_height){
//                            lp.height = container_height;
//                        }
//
//                        if(lp.height<0){
//                            lp.height = 1;
//                        }
//                        imageView.setLayoutParams(lp);
//
//                        dX = motionEvent.getX(1)-motionEvent.getX();
//                        dY = motionEvent.getY(1)-motionEvent.getY();
                    }
                    break;
                case MotionEvent.ACTION_UP:

                    break;
            }


            return true;
        }
    };


}
