package chazi.remotecontrol;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import chazi.remotecontrol.entity.Order;
import chazi.remotecontrol.utils.ContentCreator;

/**
 * Created by 595056078 on 2017/6/5.
 */

public class OrderListAdapter extends ArrayAdapter<Order> {

    private Context context;
    private int res;
    private List<Order> orderList;

    public OrderListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Order> objects) {
        super(context, resource, objects);

        this.context = context;
        this.res = resource;
        this.orderList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Order order = getItem(position);
        ViewHolder viewHolder;
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(res, null);
            viewHolder = new ViewHolder();

            viewHolder.order_type = (ImageView) view.findViewById(R.id.order_type);
            viewHolder.up_or_down = (ImageView) view.findViewById(R.id.order_up_or_down);
            viewHolder.order_param = (EditText) view.findViewById(R.id.order_param);
            viewHolder.container = (RelativeLayout) view.findViewById(R.id.container);

            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        assert order != null;

        viewHolder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                remove(order);
                notifyDataSetChanged();
                return false;
            }
        });

        switch (order.getOrderType()){
            case ContentCreator.ORDER_TYPE_MOUSE_MOVE:
                viewHolder.order_type.setImageResource(R.drawable.mouse_move);
                viewHolder.order_type.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        order.setOrderType(ContentCreator.ORDER_TYPE_MOUSE_MOVE_ABS);
                        notifyDataSetChanged();
                    }
                });
                viewHolder.order_type.setOnTouchListener(new MyOnTouchListener(context));
                viewHolder.up_or_down.setVisibility(View.INVISIBLE);
                viewHolder.order_param.setEnabled(true);
                viewHolder.order_param.addTextChangedListener(new MyTextWatcher(order));
                break;
            case ContentCreator.ORDER_TYPE_MOUSE_MOVE_ABS:
                viewHolder.order_type.setImageResource(R.drawable.mouse_move_abs);
                viewHolder.order_type.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        order.setOrderType(ContentCreator.ORDER_TYPE_MOUSE_MOVE);
                        notifyDataSetChanged();
                    }
                });
                viewHolder.order_type.setOnTouchListener(new MyOnTouchListener(context));
                viewHolder.up_or_down.setVisibility(View.INVISIBLE);
                viewHolder.order_param.setEnabled(true);
                viewHolder.order_param.addTextChangedListener(new MyTextWatcher(order));
                break;
            case ContentCreator.ORDER_TYPE_MOUSE_CLICK:
                viewHolder.order_type.setImageResource(R.drawable.mouse);
                viewHolder.up_or_down.setVisibility(View.VISIBLE);
                viewHolder.up_or_down.setImageResource(R.drawable.order_down_and_up);
                viewHolder.order_param.setEnabled(false);
                viewHolder.order_param.setClickable(true);
                viewHolder.order_param.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(order.getOrderParam().equals("right")){
                            order.setOrderParam("left");
                        }else {
                            order.setOrderParam("right");
                        }
                    }
                });
                break;
            case ContentCreator.ORDER_TYPE_MOUSE_HOLD:
                viewHolder.order_type.setImageResource(R.drawable.mouse);
                viewHolder.up_or_down.setVisibility(View.VISIBLE);
                viewHolder.up_or_down.setImageResource(R.drawable.order_down);
                viewHolder.order_param.setEnabled(false);
                viewHolder.order_param.setClickable(true);
                viewHolder.order_param.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(order.getOrderParam().equals("right")){
                            order.setOrderParam("left");
                        }else {
                            order.setOrderParam("right");
                        }
                        notifyDataSetChanged();
                    }
                });
                break;
            case ContentCreator.ORDER_TYPE_MOUSE_RELEASE:
                viewHolder.order_type.setImageResource(R.drawable.mouse);
                viewHolder.up_or_down.setVisibility(View.VISIBLE);
                viewHolder.up_or_down.setImageResource(R.drawable.order_up);
                viewHolder.order_param.setEnabled(false);
                viewHolder.order_param.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(order.getOrderParam().equals("right")){
                            order.setOrderParam("left");
                        }else {
                            order.setOrderParam("right");
                        }
                        notifyDataSetChanged();
                    }
                });
                break;
            case ContentCreator.ORDER_TYPE_KEY:
                viewHolder.order_type.setImageResource(R.drawable.keyboard);
                viewHolder.up_or_down.setVisibility(View.VISIBLE);
                viewHolder.up_or_down.setImageResource(R.drawable.order_down_and_up);
                viewHolder.order_param.setEnabled(true);
                viewHolder.order_param.addTextChangedListener(new MyTextWatcher(order));
                break;
            case ContentCreator.ORDER_TYPE_KEY_DOWN:
                viewHolder.order_type.setImageResource(R.drawable.keyboard);
                viewHolder.up_or_down.setVisibility(View.VISIBLE);
                viewHolder.up_or_down.setImageResource(R.drawable.order_down);
                viewHolder.order_param.setEnabled(true);
                viewHolder.order_param.addTextChangedListener(new MyTextWatcher(order));
                break;
            case ContentCreator.ORDER_TYPE_KEY_UP:
                viewHolder.order_type.setImageResource(R.drawable.keyboard);
                viewHolder.up_or_down.setVisibility(View.VISIBLE);
                viewHolder.up_or_down.setImageResource(R.drawable.order_up);
                viewHolder.order_param.setEnabled(true);
                viewHolder.order_param.addTextChangedListener(new MyTextWatcher(order));
                break;
            case ContentCreator.ORDER_TYPE_WHEEL:
                viewHolder.order_type.setImageResource(R.drawable.wheel);
                viewHolder.up_or_down.setVisibility(View.INVISIBLE);
                viewHolder.order_param.setEnabled(true);
                viewHolder.order_param.addTextChangedListener(new MyTextWatcher(order));
                break;
            case ContentCreator.ORDER_TYPE_STRING:
                viewHolder.order_type.setImageResource(R.drawable.string);
                viewHolder.up_or_down.setVisibility(View.INVISIBLE);
                viewHolder.order_param.setEnabled(true);
                viewHolder.order_param.addTextChangedListener(new MyTextWatcher(order));
                break;
            case ContentCreator.ORDER_TYPE_DELAY:
                viewHolder.order_type.setImageResource(R.drawable.delay);
                viewHolder.up_or_down.setVisibility(View.INVISIBLE);
                viewHolder.order_param.setEnabled(true);
                viewHolder.order_param.addTextChangedListener(new MyTextWatcher(order));
                break;
        }

        viewHolder.up_or_down.setOnClickListener(new TypeOnClickListener(order));
        viewHolder.up_or_down.setOnTouchListener(new MyOnTouchListener(context));

        viewHolder.order_param.setText(order.getOrderParam());

        return view;
    }

    private class ViewHolder{
        ImageView order_type,up_or_down;
        EditText order_param;
        RelativeLayout container;
    }

    private class TypeOnClickListener implements View.OnClickListener {

        Order order;

        TypeOnClickListener(Order o){
            order = o;
        }

        @Override
        public void onClick(View v) {
            switch (order.getOrderType()){
                case ContentCreator.ORDER_TYPE_MOUSE_MOVE:
                    order.setOrderType(ContentCreator.ORDER_TYPE_MOUSE_MOVE_ABS);
                    break;
                case ContentCreator.ORDER_TYPE_MOUSE_MOVE_ABS:
                    order.setOrderType(ContentCreator.ORDER_TYPE_MOUSE_MOVE);
                    break;
                case ContentCreator.ORDER_TYPE_MOUSE_CLICK:
                    order.setOrderType(ContentCreator.ORDER_TYPE_MOUSE_HOLD);
                    break;
                case ContentCreator.ORDER_TYPE_MOUSE_HOLD:
                    order.setOrderType(ContentCreator.ORDER_TYPE_MOUSE_RELEASE);
                    break;
                case ContentCreator.ORDER_TYPE_MOUSE_RELEASE:
                    order.setOrderType(ContentCreator.ORDER_TYPE_MOUSE_CLICK);
                    break;
                case ContentCreator.ORDER_TYPE_KEY:
                    order.setOrderType(ContentCreator.ORDER_TYPE_KEY_DOWN);
                    break;
                case ContentCreator.ORDER_TYPE_KEY_DOWN:
                    order.setOrderType(ContentCreator.ORDER_TYPE_KEY_UP);
                    break;
                case ContentCreator.ORDER_TYPE_KEY_UP:
                    order.setOrderType(ContentCreator.ORDER_TYPE_KEY);
                    break;
                case ContentCreator.ORDER_TYPE_WHEEL:
                    break;
                case ContentCreator.ORDER_TYPE_STRING:
                    break;
                case ContentCreator.ORDER_TYPE_DELAY:
                    break;
            }

            notifyDataSetChanged();
        }
    }

    private class MyTextWatcher implements TextWatcher{

        Order order;

        MyTextWatcher(Order o){
            order = o;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            //order.setOrderParam(s.toString());
        }
    }

}