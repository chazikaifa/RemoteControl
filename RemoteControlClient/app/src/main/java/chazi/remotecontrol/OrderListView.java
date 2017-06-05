package chazi.remotecontrol;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import chazi.remotecontrol.entity.Order;
import chazi.remotecontrol.utils.ContentCreator;
import chazi.remotecontrol.utils.DensityUtil;

/**
 * Created by 595056078 on 2017/6/5.
 */

public class OrderListView extends LinearLayout {

    private final int ORDER_TYPE_COUNT = 6;

    private Context context;
    private GridView orderView;
    private List<Order> orderList = new ArrayList<>();
    private OrderListAdapter adapter;
    private ImageView[] newOrder = new ImageView[ORDER_TYPE_COUNT];
    private int imageResource[] = new int[]{
            R.drawable.mouse,
            R.drawable.mouse_move,
            R.drawable.keyboard,
            R.drawable.wheel,
            R.drawable.string,
            R.drawable.delay
    };

    public OrderListView(Context context) {
        super(context);

        this.context = context;

        init();
    }

    public OrderListView(Context context,List<Order> orders) {
        super(context);

        this.context = context;

        orderList.addAll(orders);

        init();
    }

    public OrderListView(Context context,String orders) {
        super(context);

        this.context = context;

        orderList.addAll(Order.getOrderListFromContent(orders));

        init();
    }

    public OrderListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        init();
    }

    private void init(){
        setOrientation(VERTICAL);

        LinearLayout new_order_ll = new LinearLayout(context);
        LayoutParams lp_new_order_ll = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        new_order_ll.setLayoutParams(lp_new_order_ll);
        for(int i=0;i<ORDER_TYPE_COUNT;i++){
            newOrder[i] = new ImageView(context);
            newOrder[i].setTag(i);
            newOrder[i].setScaleType(ImageView.ScaleType.FIT_CENTER);
            newOrder[i].setPadding(5,5,5,5);
            LayoutParams lp = new LayoutParams(0, DensityUtil.dip2px(context,50),1);
            newOrder[i].setLayoutParams(lp);
            newOrder[i].setImageResource(imageResource[i]);
            newOrder[i].setOnClickListener(listener);
            newOrder[i].setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action){
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_BUTTON_PRESS:
                            v.setBackgroundResource(R.color.dark_blue);
                            break;
                        case MotionEvent.ACTION_BUTTON_RELEASE:
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            v.setBackgroundResource(R.color.transparent);
                            break;
                    }
                    return false;
                }
            });
            new_order_ll.addView(newOrder[i]);
        }
        new_order_ll.setBackgroundResource(R.drawable.blue_released_background);
        addView(new_order_ll);

        adapter = new OrderListAdapter(context,R.layout.item_order,orderList);

        orderView = new GridView(context);
        LayoutParams lp_gv = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        orderView.setLayoutParams(lp_gv);
        orderView.setNumColumns(2);
        orderView.setAdapter(adapter);

        addView(orderView);
    }

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            Order order =  new Order();
            switch (index){
                case 0:
                    order.setOrderType(ContentCreator.ORDER_TYPE_MOUSE_CLICK);
                    order.setOrderParam("left");
                    break;
                case 1:
                    order.setOrderType(ContentCreator.ORDER_TYPE_MOUSE_MOVE);
                    order.setOrderParam("500,500");
                    break;
                case 2:
                    order.setOrderType(ContentCreator.ORDER_TYPE_KEY);
                    order.setOrderParam("a");
                    break;
                case 3:
                    order.setOrderType(ContentCreator.ORDER_TYPE_STRING);
                    order.setOrderParam("string");
                    break;
                case 4:
                    order.setOrderType(ContentCreator.ORDER_TYPE_DELAY);
                    order.setOrderParam("1000");
                    break;
            }
            Log.i("adapters","order count is "+adapter.getCount());
            adapter.add(order);
            adapter.notifyDataSetChanged();
        }
    };
}
