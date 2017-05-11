package chazi.remotecontrol;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import chazi.remotecontrol.entity.IP;

/**
 * Created by 595056078 on 2017/5/11.
 */

public class IPListAdapter extends ArrayAdapter<IP> {
    private Context context;
    private int res;
    private List<IP> IPList;

    public IPListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<IP> objects) {
        super(context, resource, objects);
        this.context = context;
        res = resource;
        IPList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v;
        TextView text;
        if(convertView == null){
            v = LayoutInflater.from(context).inflate(res,null);
            text = (TextView) v.findViewById(R.id.ip_port);
            v.setTag(text);
        }else {
            v = convertView;
            text = (TextView) v.getTag();
        }
        String ip = getItem(position).getIpAdr();
        String port = getItem(position).getPort()+"";
        text.setText(ip+" : "+port);
        return v;
    }
}
