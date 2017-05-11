package chazi.remotecontrol;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import chazi.remotecontrol.db.RealmDb;
import chazi.remotecontrol.entity.Panel;

/**
 * Created by 595056078 on 2017/5/1.
 */

public class PanelListActivity extends Activity implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener{

    private List<Panel> panelList = new ArrayList<>();
    private ListView panelListView;
    private PanelListAdapter adapter;
    private ImageView btn_back,btn_edit;
    private TextView title,btn_new_panel,btn_delete_panels,btn_export_panels;
    private boolean isEdit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RealmDb.initRealm(getApplicationContext());

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
                isEdit = !isEdit;
                adapter.changeEdit();
            }
        });

        btn_new_panel = (TextView) findViewById(R.id.new_panel);
        btn_delete_panels = (TextView) findViewById(R.id.delete_panel);
        btn_export_panels = (TextView) findViewById(R.id.export_panel);

        btn_new_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewPanelDialog newPanelDialog = new NewPanelDialog(PanelListActivity.this);
                newPanelDialog.show();
            }
        });

        btn_delete_panels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"删除选中面板！",Toast.LENGTH_SHORT).show();
            }
        });

        btn_export_panels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"导出选中面板！",Toast.LENGTH_SHORT).show();
            }
        });

        panelListView = (ListView) findViewById(R.id.panel_list_view);
        panelList.addAll(RealmDb.getAllPanels());
//        panelList.addAll(RealmDb.getTestPanels());
        adapter = new PanelListAdapter(getApplicationContext(),R.layout.item_panel,panelList);

        panelListView.setAdapter(adapter);
        panelListView.setOnItemClickListener(this);
        panelListView.setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(!isEdit) {
            Intent intent = new Intent();
            intent.setClass(PanelListActivity.this, PanelActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("panel", panelList.get(position));
            intent.putExtras(bundle);
            startActivity(intent);
        }else {
            panelList.get(position).setSelect(!panelList.get(position).isSelect());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        PanelDialog panelDialog = new PanelDialog(PanelListActivity.this,position);
        panelDialog.show();

        return false;
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
                        RealmDb.deletePanelById(panelList.get(position).getId());

                        panelList.remove(position);

                        adapter.notifyDataSetChanged();
//                        Toast.makeText(context,"删除面板",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.export:
                        Toast.makeText(context,"导出面板",Toast.LENGTH_SHORT).show();
                        break;
                }
                dismiss();
            }
        };
    }

    //新建面板弹窗
    private class NewPanelDialog extends Dialog{

        Context context;
        EditText panel_name;
        Switch is_vertical;
        Button btn_confirm,btn_cancel;

        public NewPanelDialog(@NonNull Context context) {
            super(context);

            this.context = context;

            init();
        }

        void init(){
            //没有标题
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_new_panel);

            //点击外部销毁
            setCanceledOnTouchOutside(true);

            //宽度为屏幕的宽度
            int width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.width = width;
            getWindow().setAttributes(lp);

            panel_name = (EditText) findViewById(R.id.panel_name);
            is_vertical = (Switch) findViewById(R.id.is_vertical);
            btn_confirm = (Button) findViewById(R.id.btn_confirm);
            btn_cancel = (Button) findViewById(R.id.btn_cancel);

            is_vertical.setChecked(true);

            btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("confirm",is_vertical.isChecked()+"");

                    Panel panel = new Panel();
                    panel.setName(panel_name.getText().toString());
                    panel.setVertical(is_vertical.isChecked());
                    RealmDb.savePanel(panel);
                    panelList.clear();
                    panelList.addAll(RealmDb.getAllPanels());
                    adapter.notifyDataSetChanged();

                    dismiss();
                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }
}
