package chazi.remotecontrol;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import chazi.remotecontrol.WidgetView.MousePadView;
import chazi.remotecontrol.db.RealmDb;
import chazi.remotecontrol.entity.Panel;
import chazi.remotecontrol.entity.Widget;

/**
 * Created by 595056078 on 2017/4/15.
 */

public class PanelActivity extends Activity {

    private Panel panel;
    private boolean backFlag = false;
    private List<Widget> widgetList;
    private RelativeLayout panelView;
    private TextView title;
    private ImageView btn_edit,btn_back;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null && (panel = bundle.getParcelable("panel")) != null) {

            if (panel.isVertical()) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            widgetList = RealmDb.getWidgetsByPanelId(panel.getId());


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

            }
        });

        panelView = (RelativeLayout) findViewById(R.id.panelView);

        Widget widget = new Widget("1");
        widget.setX(10);
        widget.setY(50);
        widget.setWidth(300);
        widget.setHeight(400);

        MousePadView testView = new MousePadView(getApplicationContext(),widget);
        testView.setEdit(true);
        testView.setFocus(true);

        panelView.addView(testView);
    }

    public void AddWidgetView(Widget widget) {

        View v = new View(PanelActivity.this);

        v.setX(widget.getX());
        v.setY(widget.getY());


        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
        lp.width = (int) widget.getWidth();
        lp.height = (int) widget.getHeight();

        v.setLayoutParams(lp);

        addContentView(v, lp);

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
