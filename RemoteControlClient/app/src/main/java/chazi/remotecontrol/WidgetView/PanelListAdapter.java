package chazi.remotecontrol.WidgetView;

import android.content.Context;
import android.graphics.Color;
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

import chazi.remotecontrol.PanelListActivity;
import chazi.remotecontrol.R;
import chazi.remotecontrol.entity.Panel;

/**
 * Created by 595056078 on 2017/5/3.
 */

public class PanelListAdapter extends ArrayAdapter<Panel> {
    private Context context;
    private int res;
    private List<Panel> panelList;
    private boolean isEdit;

    public PanelListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Panel> objects) {
        super(context, resource, objects);

        this.context = context;
        res = resource;
        panelList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Panel panel = getItem(position);
        ViewHolder viewHolder;
        View view;

        if (convertView == null){
            view = LayoutInflater.from(context).inflate(res,null);
            viewHolder = new ViewHolder();

            viewHolder.name = (TextView) view.findViewById(R.id.panel_name);
            viewHolder.btn_select = (ImageView) view.findViewById(R.id.btn_select);

            view.setTag(viewHolder);
        }
        else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.name.setText(panel.getName()+"");
        if(isEdit){
            viewHolder.btn_select.setVisibility(View.VISIBLE);

            if(panel.isSelect()){
                viewHolder.btn_select.setBackgroundColor(Color.GREEN);
//                    viewHolder.btn_select.setImageResource();
            }else {
                viewHolder.btn_select.setBackgroundColor(Color.GRAY);
            }

            viewHolder.btn_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    panel.setSelect(!panel.isSelect());
                    notifyDataSetChanged();
                }
            });


        }else {
            viewHolder.btn_select.setVisibility(View.GONE);
        }

        return view;
    }

    public void changeEdit(){
        isEdit = !isEdit;
        if(!isEdit){
            for(Panel panel:panelList){
                panel.setSelect(false);
            }
        }
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView name;
        ImageView btn_select;
    }
}
