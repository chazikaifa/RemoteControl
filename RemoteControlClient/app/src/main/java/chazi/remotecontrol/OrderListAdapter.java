package chazi.remotecontrol;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 595056078 on 2017/6/6.
 */

public class OrderListAdapter extends ArrayAdapter<String> {

    private Context context;
    private int res;
    private List<String> strings;

    public OrderListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
        super(context, resource, objects);

        this.context = context;
        res = resource;
        strings = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null){
            view = LayoutInflater.from(context).inflate(res,null);
            viewHolder.order = (TextView) view.findViewById(R.id.order);
            viewHolder.btn_delete = (ImageView) view.findViewById(R.id.btn_delete);

            view.setTag(viewHolder);
        }
        else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.order.setText(getItem(position));
        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strings.remove(position);
                notifyDataSetChanged();
            }
        });
        viewHolder.btn_delete.setOnTouchListener(new MyOnTouchListener(context,R.drawable.reduce,R.drawable.reduce_press,R.color.transparent,R.color.gray));

        return view;
    }

    private class ViewHolder{
        TextView order;
        ImageView btn_delete;
    }
}
