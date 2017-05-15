package chazi.remotecontrol;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import chazi.remotecontrol.WidgetView.MousePadView;
import chazi.remotecontrol.WidgetView.WheelView;
import chazi.remotecontrol.WidgetView.WidgetView;
import chazi.remotecontrol.db.RealmDb;
import chazi.remotecontrol.entity.Panel;
import chazi.remotecontrol.entity.Widget;

/**
 * Created by 595056078 on 2017/4/15.
 */

public class PanelActivity extends Activity {

    private Panel panel;
    private boolean backFlag = false;
    private List<Widget> widgetList = new ArrayList<>();
    private List<WidgetView> widgetViewList;
    private RelativeLayout panelView;
    private TextView title;
    private ImageView btn_edit, btn_back;
    private LinearLayout menu_ll;
    private LinearLayout btn_newWidget;

    private boolean isEdit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RealmDb.initRealm(getApplicationContext());

        Bundle bundle = getIntent().getExtras();

        if (bundle != null && (panel = bundle.getParcelable("panel")) != null) {

            if (panel.isVertical()) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            widgetList.addAll(RealmDb.getWidgetsByPanelId(panel.getId()));
            widgetViewList = new ArrayList<>();

        } else {
            Toast.makeText(getApplicationContext(), "面板数据传输错误！", Toast.LENGTH_LONG).show();
            finish();
        }

        setContentView(R.layout.activity_panel_view);

        title = (TextView) findViewById(R.id.title);
        title.setText(panel.getName());

        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_back.setOnTouchListener(new MyOnTouchListener(getApplicationContext(),R.drawable.back_normal,R.drawable.back_selected));

        btn_edit = (ImageView) findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEdit = !isEdit;
                if (isEdit) {
                    menu_ll.setVisibility(View.VISIBLE);
                    btn_edit.setImageResource(R.drawable.edit_selected);

                    Toast.makeText(getApplicationContext(), "进入编辑状态!", Toast.LENGTH_SHORT).show();

                    for (WidgetView widgetView : widgetViewList) {
                        widgetView.setEdit(isEdit);
                    }
                } else {
                    new AlertDialog.Builder(PanelActivity.this)
                            .setTitle("修改面板")
                            .setMessage("确认保存本次修改？")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    widgetList = new ArrayList<>();
                                    for (WidgetView widgetView : widgetViewList) {
                                        widgetList.add(widgetView.getWidget());
                                    }
                                    RealmDb.saveWidgets(widgetList, panel.getId());

                                    menu_ll.setVisibility(View.GONE);
                                    btn_edit.setImageResource(R.drawable.edit_normal);

                                    for (WidgetView widgetView : widgetViewList) {
                                        widgetView.setEdit(isEdit);
                                    }

                                    Toast.makeText(getApplicationContext(), "退出编辑状态!", Toast.LENGTH_SHORT).show();

                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    panelView.removeAllViews();
                                    widgetViewList.clear();
                                    widgetList.clear();
                                    widgetList.addAll(RealmDb.getWidgetsByPanelId(panel.getId()));
                                    for (Widget widget : widgetList) {
                                        final WidgetView v = WidgetView.Creator(getApplicationContext(), widget);
                                        widgetViewList.add(v);
                                        v.setOnEditLongClickListener(new WidgetView.OnEditLongClickListener() {
                                            @Override
                                            public void onEditLongClick() {
                                                Intent intent = new Intent();
                                                intent.setClass(PanelActivity.this,EditWidgetActivity.class);
                                                intent.putExtra("name",v.getWidget().getName());
                                                intent.putExtra("content",v.getWidget().getContent());
                                                intent.putExtra("index",widgetViewList.indexOf(v));
                                                startActivityForResult(intent,0);

                                            }
                                        });
                                        panelView.addView(v);
                                    }

                                    menu_ll.setVisibility(View.GONE);
                                    btn_edit.setImageResource(R.drawable.edit_normal);

                                    for (WidgetView widgetView : widgetViewList) {
                                        widgetView.setEdit(isEdit);
                                    }

                                    Toast.makeText(getApplicationContext(), "退出编辑状态!", Toast.LENGTH_SHORT).show();

                                    dialog.dismiss();
                                }
                            })
                            .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    isEdit = true;
                                }
                            })
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    dialog.dismiss();
                                    isEdit = true;
                                }
                            })
                            .show();
                }
            }
        });

        panelView = (RelativeLayout) findViewById(R.id.panelView);


        for (Widget widget:widgetList) {
            final WidgetView v = WidgetView.Creator(getApplicationContext(), widget);
            widgetViewList.add(v);
            v.setOnEditLongClickListener(new WidgetView.OnEditLongClickListener() {
                @Override
                public void onEditLongClick() {
                    Intent intent = new Intent();
                    intent.setClass(PanelActivity.this,EditWidgetActivity.class);
                    intent.putExtra("name",v.getWidget().getName());
                    intent.putExtra("content",v.getWidget().getContent());
                    intent.putExtra("index",widgetViewList.indexOf(v));
                    startActivityForResult(intent,0);

                }
            });
            panelView.addView(v);
        }

        menu_ll = (LinearLayout) findViewById(R.id.menu_ll);
        btn_newWidget = (LinearLayout) findViewById(R.id.new_widget);
        btn_newWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Widget widget = new Widget(panel.getId(),0,0,100,50,6,"","新建按键");

                final WidgetView widgetView = WidgetView.Creator(getApplicationContext(),widget);
                widgetView.setEdit(true);
                widgetViewList.add(widgetView);
                widgetView.setOnEditLongClickListener(new WidgetView.OnEditLongClickListener() {
                    @Override
                    public void onEditLongClick() {
                        Intent intent = new Intent();
                        intent.setClass(PanelActivity.this,EditWidgetActivity.class);
                        intent.putExtra("name",widgetView.getWidget().getName());
                        intent.putExtra("content",widgetView.getWidget().getContent());
                        intent.putExtra("index",widgetViewList.indexOf(widgetView));
                        startActivityForResult(intent,0);

                    }
                });

                panelView.addView(widgetView);

                Intent intent = new Intent();
                intent.setClass(PanelActivity.this,EditWidgetActivity.class);
                intent.putExtra("name",widgetView.getWidget().getName());
                intent.putExtra("content",widgetView.getWidget().getContent());
                intent.putExtra("index",widgetViewList.indexOf(widgetView));
                startActivityForResult(intent,0);
            }
        });
        btn_newWidget.setOnTouchListener(new MyOnTouchListener(PanelActivity.this));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle bundle = data.getExtras();
        int index = bundle.getInt("index");

        try {
            if (requestCode == 0 && resultCode == EditWidgetActivity.STATUS_OK) {
                String name = bundle.getString("name");
                String content = bundle.getString("content");

                Log.i("onResult","name = "+name + " content = "+content);

                Widget widget = new Widget(widgetViewList.get(index).getWidget());
                widget.setName(name);
                widget.setContent(content);
                widgetViewList.get(index).setWidget(widget);
            } else if(requestCode == 0 && resultCode == EditWidgetActivity.STATUS_DELETE){
                panelView.removeView(widgetViewList.get(index));
                widgetViewList.remove(index);
            }else if(requestCode == 0 && resultCode == EditWidgetActivity.STATUS_CANCEL){
                //取消编辑
            }
        }catch (Exception e){
            Log.i("onResult",e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        if(isEdit){
            new AlertDialog.Builder(PanelActivity.this)
                    .setTitle("警告")
                    .setMessage("正在进行编辑，是否保存修改？")
                    .setPositiveButton("保存并退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            widgetList = new ArrayList<>();
                            for (WidgetView widgetView : widgetViewList) {
                                widgetList.add(widgetView.getWidget());
                            }
                            RealmDb.saveWidgets(widgetList, panel.getId());

                            menu_ll.setVisibility(View.GONE);
                            btn_edit.setImageResource(R.drawable.edit_normal);

                            for (WidgetView widgetView : widgetViewList) {
                                widgetView.setEdit(isEdit);
                            }

                            dialog.dismiss();
                            PanelActivity.this.finish();
                        }
                    })
                    .setNegativeButton("直接退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            PanelActivity.this.finish();
                        }
                    })
                    .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }else if (!backFlag) {
            backFlag = true;
            Toast.makeText(getApplicationContext(), "再按一次退出面板", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backFlag = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
        }
    }
}
