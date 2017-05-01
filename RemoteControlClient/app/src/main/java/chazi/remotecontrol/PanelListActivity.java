package chazi.remotecontrol;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import chazi.remotecontrol.db.RealmDb;
import chazi.remotecontrol.entity.Panel;

/**
 * Created by 595056078 on 2017/5/1.
 */

public class PanelListActivity extends Activity implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{

    private List<Panel> panelList;
    private ListView panelListView;
    private PanelListAdapter adapter;
    private ImageView btn_back,btn_edit;
    private TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_panel_list);

        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_edit = (ImageView) findViewById(R.id.btn_edit);
        title = (TextView) findViewById(R.id.title);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.changeEdit();
            }
        });

        panelListView = (ListView) findViewById(R.id.panel_list_view);

//        panelList = RealmDb.getAllPanels();
        panelList = RealmDb.getTestPanels();
        adapter = new PanelListAdapter(getApplicationContext(),R.layout.item_panel,panelList);

        panelListView.setAdapter(adapter);
        panelListView.setOnItemClickListener(this);
        panelListView.setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.setClass(PanelListActivity.this,PanelActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("panel",panelList.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        PanelDialog panelDialog = new PanelDialog(getApplicationContext(),position);
        panelDialog.show();

        return false;
    }

    //列表的适配器
    private class PanelListAdapter extends ArrayAdapter<Panel>{

        private Context context;
        private int res;
        private List<Panel> panelList;
        private boolean isEdit;

        PanelListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Panel> objects) {
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

        private void changeEdit(){
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

    //长按弹出选项
    private class PanelDialog extends Dialog {

        private TextView edit,copy,delete,export;
        private Panel panel;
        private int position;
        private Context context;

        public PanelDialog(@NonNull Context context,int index) {
            super(context);

            this.context = context;
            this.position = index;
            panel = panelList.get(position);

            init();
        }

        private void init(){
            //没有标题
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_panel_menu);

            //点击外部销毁
            setCanceledOnTouchOutside(true);

            //宽度为屏幕的5/6
            int width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.width = width/6*5;
            getWindow().setAttributes(lp);

            edit = (TextView) findViewById(R.id.edit);
            copy = (TextView) findViewById(R.id.copy);
            delete = (TextView) findViewById(R.id.delete);
            export = (TextView) findViewById(R.id.export);

            edit.setOnClickListener(myListener);
            copy.setOnClickListener(myListener);
            delete.setOnClickListener(myListener);
            export.setOnClickListener(myListener);
        }

        private View.OnClickListener myListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id){
                    case R.id.edit:
                        Toast.makeText(context,"跳转编辑面板",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.copy:
                        Toast.makeText(context,"复制面板",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.delete:
                        Toast.makeText(context,"删除面板",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.export:
                        Toast.makeText(context,"导出面板",Toast.LENGTH_SHORT).show();
                        break;
                }
                dismiss();
            }
        };
    }
}
