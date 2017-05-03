package chazi.remotecontrol.WidgetView;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chazi.remotecontrol.R;
import chazi.remotecontrol.entity.Widget;

/**
 * Created by 595056078 on 2017/5/3.
 */

public class SearchAdapter extends ArrayAdapter<Widget> {

    private Context context;
    private int res;
    private List<Widget> searchList;
    private String word;

    public SearchAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Widget> objects) {
        super(context, resource, objects);

        this.context = context;
        res = resource;
        searchList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        TextView name;
        if (convertView == null){
            view = LayoutInflater.from(context).inflate(res,null);
            name = (TextView) view.findViewById(R.id.widget_name);
            view.setTag(name);
        }
        else{
            view = convertView;
            name = (TextView) view.getTag();
        }
        Pattern pattern = Pattern.compile(word);
        Matcher matcher = pattern.matcher(getItem(position).getName());
        String coloredTitle = matcher.replaceAll("<font color='#3191E8'>"+word+"</font>");
        //目前使用的颜色#3191E8对应color文件里面的main_blue

        //将加上HTML标签的字符串经过HTML解析之后设置到title上
        name.setText(Html.fromHtml(coloredTitle));

        return view;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
