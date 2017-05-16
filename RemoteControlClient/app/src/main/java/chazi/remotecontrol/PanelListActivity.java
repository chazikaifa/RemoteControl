package chazi.remotecontrol;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import chazi.remotecontrol.db.RealmDb;
import chazi.remotecontrol.entity.Panel;
import chazi.remotecontrol.utils.FileUtil;

/**
 * Created by 595056078 on 2017/5/1.
 */

public class PanelListActivity extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public static final int IMPORT_COMPLETE = 0;
    public static final int IMPORT_FAIL = 1;

    private List<Panel> panelList = new ArrayList<>();
    private ListView panelListView;
    private PanelListAdapter adapter;
    private ImageView btn_back, btn_edit;
    private TextView title;
    private LinearLayout btn_new_panel, btn_delete_panels, btn_export_panels, btn_import_panels;
    private boolean isEdit = false;
    private final int REQUEST_FILE = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IMPORT_COMPLETE:
                    adapter.clear();
                    adapter.addAll(RealmDb.getAllPanels());
                    adapter.notifyDataSetChanged();

                    Toast.makeText(getApplicationContext(), "导入完成！", Toast.LENGTH_SHORT).show();
                    break;
                case IMPORT_FAIL:
                    Toast.makeText(getApplicationContext(), "文件解析失败！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RealmDb.initRealm(getApplicationContext());

        setContentView(R.layout.activity_panel_list);

        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_edit = (ImageView) findViewById(R.id.btn_edit);
        title = (TextView) findViewById(R.id.title);
        btn_new_panel = (LinearLayout) findViewById(R.id.new_panel);
        btn_import_panels = (LinearLayout) findViewById(R.id.import_panel);
        btn_delete_panels = (LinearLayout) findViewById(R.id.delete_panel);
        btn_export_panels = (LinearLayout) findViewById(R.id.export_panel);
        panelListView = (ListView) findViewById(R.id.panel_list_view);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_back.setOnTouchListener(new MyOnTouchListener(getApplicationContext(), R.drawable.back_normal, R.drawable.back_selected));
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdit = !isEdit;
                adapter.setEdit(isEdit);

                if (isEdit) {
                    btn_new_panel.setVisibility(View.GONE);
                    btn_import_panels.setVisibility(View.GONE);
                    btn_delete_panels.setVisibility(View.VISIBLE);
                    btn_export_panels.setVisibility(View.VISIBLE);
                    btn_edit.setImageResource(R.drawable.edit_selected);
                } else {
                    btn_new_panel.setVisibility(View.VISIBLE);
                    btn_import_panels.setVisibility(View.VISIBLE);
                    btn_delete_panels.setVisibility(View.GONE);
                    btn_export_panels.setVisibility(View.GONE);
                    btn_edit.setImageResource(R.drawable.edit_normal);
                }

            }
        });
        btn_edit.setOnTouchListener(new MyOnTouchListener(getApplicationContext()));

        btn_new_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewPanelDialog newPanelDialog = new NewPanelDialog(PanelListActivity.this);
                newPanelDialog.show();
            }
        });
        btn_new_panel.setOnTouchListener(new MyOnTouchListener(getApplicationContext()));

        btn_import_panels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, REQUEST_FILE);
            }
        });
        btn_import_panels.setOnTouchListener(new MyOnTouchListener(getApplicationContext()));

        btn_delete_panels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(PanelListActivity.this)
                        .setTitle("批量删除")
                        .setMessage("确认要删除选中的面板吗？")
                        .setCancelable(true)
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (int i = 0; i < panelList.size(); i++) {
                                    Panel p = panelList.get(i);
                                    if (p.isSelect()) {
                                        RealmDb.deletePanelById(p.getId());
                                        panelList.remove(i);
                                        //删除之后i需减一保持一致
                                        i--;
                                    }
                                }
                                isEdit = false;
                                adapter.setEdit(isEdit);
                                adapter.notifyDataSetChanged();

                                btn_new_panel.setVisibility(View.VISIBLE);
                                btn_import_panels.setVisibility(View.VISIBLE);
                                btn_delete_panels.setVisibility(View.GONE);
                                btn_export_panels.setVisibility(View.GONE);
                                btn_edit.setImageResource(R.drawable.edit_normal);

                                Toast.makeText(getApplicationContext(), "已删除选中面板！", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        btn_delete_panels.setOnTouchListener(new MyOnTouchListener(getApplicationContext()));

        btn_export_panels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < panelList.size(); i++) {
                    Panel p = panelList.get(i);
                    if (p.isSelect()) {
                        FileUtil.savePanelIntoSDCard(PanelListActivity.this, p);
                    }
                }
                isEdit = false;
                adapter.setEdit(isEdit);
                adapter.notifyDataSetChanged();

                btn_new_panel.setVisibility(View.VISIBLE);
                btn_delete_panels.setVisibility(View.GONE);
                btn_export_panels.setVisibility(View.GONE);
                btn_edit.setImageResource(R.drawable.edit_normal);

                Toast.makeText(getApplicationContext(), "已导出选中面板！", Toast.LENGTH_SHORT).show();
            }
        });
        btn_export_panels.setOnTouchListener(new MyOnTouchListener(getApplicationContext()));

        panelList.addAll(RealmDb.getAllPanels());
        adapter = new PanelListAdapter(getApplicationContext(), R.layout.item_panel, panelList);

        panelListView.setAdapter(adapter);
        panelListView.setOnItemClickListener(this);
        panelListView.setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!isEdit) {
            Intent intent = new Intent();
            intent.setClass(PanelListActivity.this, PanelActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("panel", panelList.get(position));
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            panelList.get(position).setSelect(!panelList.get(position).isSelect());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        PanelDialog panelDialog = new PanelDialog(PanelListActivity.this, position);
        panelDialog.show();

        return false;
    }

    //长按弹出选项
    private class PanelDialog extends Dialog {

        private TextView edit, copy, delete, export;
        private Panel panel;
        private int position;
        private Context context;

        public PanelDialog(@NonNull Context context, int index) {
            super(context);

            this.context = context;
            this.position = index;
            panel = panelList.get(position);

            init();
        }

        private void init() {
            //没有标题
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_panel_menu);

            //点击外部销毁
            setCanceledOnTouchOutside(true);

            //宽度为屏幕的5/6
            int width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.width = width / 6 * 5;
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
                switch (id) {
                    case R.id.edit:
                        new NewPanelDialog(context, panel).show();
                        break;
                    case R.id.copy:
                        Panel p = new Panel(panel, false);

                        RealmDb.savePanel(p);
                        panelList.add(p);
                        adapter.notifyDataSetChanged();

                        Toast.makeText(context, "已复制面板", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.delete:
                        RealmDb.deletePanelById(panel.getId());

                        panelList.remove(position);

                        adapter.notifyDataSetChanged();
                        Toast.makeText(context, "面板删除成功！", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.export:
                        FileUtil.savePanelIntoSDCard(PanelListActivity.this, panel);
//                        Toast.makeText(context, "已导出面板"+panel.getName(), Toast.LENGTH_SHORT).show();
                        break;
                }
                dismiss();
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == REQUEST_FILE && resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
                Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。

                String path = uri.getPath().split(":")[1];
                File dir = Environment.getExternalStorageDirectory();
                path = dir.getPath() + "/" + path;

                Log.i("result", path);

                File file = new File(path);

                int status = FileUtil.readPanelFromSDCard(PanelListActivity.this, file, handler);
                if (status == FileUtil.STATUS_OK) {
                    adapter.clear();
                    adapter.addAll(RealmDb.getAllPanels());
                    adapter.notifyDataSetChanged();

                    Toast.makeText(getApplicationContext(), "导入完成！", Toast.LENGTH_SHORT).show();
                } else if (status == FileUtil.STATUS_FAIL) {
                    Toast.makeText(getApplicationContext(), "不是面板数据文件或文件已损坏！", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.i("result", e.toString());
        }

    }

    //新建面板弹窗
    //实际上修改面板也用这个Dialog，因为数据库中对面板的操作是copyToRealmOrUpdate()
    private class NewPanelDialog extends Dialog {

        Context context;
        Panel panel;
        EditText panel_name;
        Switch is_vertical;
        Button btn_confirm, btn_cancel;

        public NewPanelDialog(@NonNull Context context) {
            super(context);

            this.context = context;

            init();

            setTitle("创建新面板");
        }

        public NewPanelDialog(Context context, Panel p) {
            super(context);

            this.context = context;
            panel = new Panel(p, true);

            init();

            setTitle("修改面板");
            panel_name.setText(panel.getName());
            is_vertical.setChecked(panel.isVertical());
        }

        void init() {
            //没有标题
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

                    if (!panel_name.getText().toString().equals("")) {
                        String log = "修改";
                        if (panel == null) {
                            panel = new Panel();
                            log = "创建";
                        }
                        panel.setName(panel_name.getText().toString());
                        panel.setVertical(is_vertical.isChecked());
                        RealmDb.savePanel(panel);
                        panelList.clear();
                        panelList.addAll(RealmDb.getAllPanels());
                        adapter.notifyDataSetChanged();

                        Toast.makeText(context, log + "面板成功！", Toast.LENGTH_SHORT).show();

                        dismiss();
                    } else {
                        Toast.makeText(context, "请输入面板名称!", Toast.LENGTH_SHORT).show();
                    }

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
