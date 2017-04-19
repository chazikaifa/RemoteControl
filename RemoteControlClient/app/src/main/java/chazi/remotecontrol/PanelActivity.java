package chazi.remotecontrol;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            panel = savedInstanceState.getParcelable("panel");

            if(panel.isVertical()){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            widgetList = RealmDb.getWidgetsByPanelId(panel.getId());
        }else {
            Toast.makeText(getApplicationContext(),"面板数据传输错误！",Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void AddWidgetView(Widget widget){

        View v = new View(PanelActivity.this);

        v.setX(widget.getX());
        v.setY(widget.getY());


        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
        lp.width = widget.getWidth();
        lp.height = widget.getHeight();

        v.setLayoutParams(lp);

        addContentView(v,lp);

    }

    @Override
    public void onBackPressed() {
        if(!backFlag){
            backFlag = true;
            Toast.makeText(getApplicationContext(),"再按一次退出面板",Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backFlag = false;
                }
            },2000);
        }else {
            super.onBackPressed();
        }
    }
}
