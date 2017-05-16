package chazi.remotecontrol;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import chazi.remotecontrol.WidgetView.WidgetView;
import chazi.remotecontrol.entity.Widget;

/**
 * Created by 595056078 on 2017/5/16.
 */

public class SelectWidgetActivity extends Activity implements AdapterView.OnItemClickListener{

    public static final int STATUS_OK = 0;
    public static final int STATUS_CANCEL = 1;

    private ImageView btn_back;
    private ListView widgetListView;
    private List<WidgetDes> widgetDesList = new ArrayList<>();
    private SelectListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_widget);

        btn_back = (ImageView) findViewById(R.id.btn_back);
        widgetListView = (ListView) findViewById(R.id.widget_list_view);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(STATUS_CANCEL);
                finish();
            }
        });
        btn_back.setOnTouchListener(new MyOnTouchListener(SelectWidgetActivity.this, R.drawable.back_normal, R.drawable.back_selected));

        int length = WidgetView.WidgetStyleList.length;
        for (int i = 0; i < length; i++) {
            WidgetDes wd = new WidgetDes(WidgetView.WidgetStyleList[i], WidgetView.WidgetSytleDescription[i]);
            widgetDesList.add(wd);
        }
        adapter = new SelectListAdapter(SelectWidgetActivity.this, R.layout.item_select_widget, widgetDesList);
        widgetListView.setAdapter(adapter);
        widgetListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra("style",position+1);
        setResult(STATUS_OK,intent);
        finish();
    }

    private class WidgetDes {
        private String style = "";
        private String description = "";

        private WidgetDes(String style, String description) {
            this.style = style;
            this.description = description;
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    private class SelectListAdapter extends ArrayAdapter<WidgetDes> {

        private Context context;
        private int res;

        public SelectListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<WidgetDes> objects) {
            super(context, resource, objects);
            this.context = context;
            res = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v;
            ViewHolder viewHolder;
            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(res, null);
                viewHolder = new ViewHolder();
                viewHolder.style = (TextView) v.findViewById(R.id.style);
                viewHolder.description = (TextView) v.findViewById(R.id.description);
                v.setTag(viewHolder);
            } else {
                v = convertView;
                viewHolder = (ViewHolder) v.getTag();
            }
            viewHolder.style.setText(getItem(position).getStyle());
            viewHolder.description.setText(getItem(position).getDescription());

            return v;
        }

        class ViewHolder {
            TextView style;
            TextView description;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(STATUS_CANCEL);
        finish();
    }
}
