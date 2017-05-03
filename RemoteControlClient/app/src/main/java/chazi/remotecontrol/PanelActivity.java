package chazi.remotecontrol;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
    private TextView btn_newWidget;

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

        btn_edit = (ImageView) findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEdit = !isEdit;
                String s;
                if (isEdit) {
                    s = "进入编辑状态!";
                    menu_ll.setVisibility(View.VISIBLE);
                } else {
                    s = "退出编辑状态!";
                    menu_ll.setVisibility(View.GONE);

                    widgetList = new ArrayList<>();
                    for (WidgetView widgetView : widgetViewList) {
                        widgetList.add(widgetView.getWidget());
                    }
                    RealmDb.saveWidgets(widgetList, panel.getId());
                }

                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                for (WidgetView widgetView : widgetViewList) {
                    widgetView.setEdit(isEdit);
                }
            }
        });

        panelView = (RelativeLayout) findViewById(R.id.panelView);


        for (Widget widget : widgetList) {
            WidgetView v = WidgetView.Creator(getApplicationContext(), widget);
            widgetViewList.add(v);
            panelView.addView(v);
        }

        menu_ll = (LinearLayout) findViewById(R.id.menu_ll);
        btn_newWidget = (TextView) findViewById(R.id.new_widget);

        btn_newWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Widget widget = new Widget(panel.getId(),0,0,100,50,6,"","新建按键");

                List<Widget> widgets = new ArrayList<>();
                widgets.add(widget);

                RealmDb.addWidgets(widgets);
                WidgetView widgetView = WidgetView.Creator(getApplicationContext(),widget);
                widgetView.setEdit(true);
                widgetViewList.add(widgetView);

                panelView.addView(widgetView);

                widgetList = new ArrayList<>();
                widgetList.addAll(RealmDb.getWidgetsByPanelId(panel.getId()));

                Intent intent = new Intent();
                intent.setClass(PanelActivity.this,EditWidgetActivity.class);
                intent.putExtra("index",widgetViewList.size()-1);
                startActivityForResult(intent,0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle bundle = data.getExtras();
        int index = bundle.getInt("index");

        try {
            if (requestCode == 0 && resultCode == 0) {
                String name = bundle.getString("name");
                String content = bundle.getString("content");

                Log.i("onResult","name = "+name + " content = "+content);

                RealmDb.realm.beginTransaction();
                widgetList.get(index).setName(name);
                widgetList.get(index).setContent(content);

                widgetViewList.get(index).setWidget(widgetList.get(index));
                RealmDb.realm.commitTransaction();
            } else {
                RealmDb.realm.beginTransaction();
                widgetList.remove(index);
                RealmDb.realm.commitTransaction();
            }
        }catch (Exception e){
            Log.i("onResult",e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        if (!backFlag) {
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
