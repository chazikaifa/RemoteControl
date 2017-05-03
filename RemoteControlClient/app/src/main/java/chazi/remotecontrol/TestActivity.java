package chazi.remotecontrol;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import chazi.remotecontrol.db.RealmDb;
import chazi.remotecontrol.entity.Widget;
import chazi.remotecontrol.utils.Connect;
import chazi.remotecontrol.utils.Global;

/**
 * Created by 595056078 on 2017/4/19.
 */

public class TestActivity extends Activity {

    Button addDb,readDb,delDb,btn_send,btn_clear;
    EditText sendContent;
    TextView logOut;
    String log = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        RealmDb.initRealm(getApplicationContext());

        addDb = (Button) findViewById(R.id.addDB);
        readDb = (Button) findViewById(R.id.readDB);
        delDb = (Button) findViewById(R.id.deleteDB);
        sendContent = (EditText) findViewById(R.id.testContent);
        btn_send = (Button) findViewById(R.id.sendTest);
        btn_clear = (Button) findViewById(R.id.clearLog);
        logOut = (TextView) findViewById(R.id.logOut);

        logOut.setMovementMethod(new ScrollingMovementMethod());

        addDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Widget widget = new Widget();
                widget.setPanelId("1");
                widget.setHeight((int)(Math.random()*50));
                widget.setWidth((int)(Math.random()*50));
                widget.setX((int)(Math.random()*50));
                widget.setY((int)(Math.random()*50));

                List<Widget> widgets = new ArrayList<>();
                widgets.add(widget);
                RealmDb.addWidgets(widgets);

                log += "写入一条Widget数据！\n" +
                        "height="+widget.getHeight()+"\n"+
                        "width="+widget.getWidth()+"\n" +
                        "X="+widget.getX()+"\n" +
                        "Y="+widget.getY()+"\n";

                logOut.setText(log);
            }
        });

        readDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Widget> widgets = RealmDb.getWidgetsByPanelId("1");

                JSONArray jsonArray = new JSONArray();
                for(Widget widget:widgets){
                    jsonArray.put(widget.toJson());
                }

                log += jsonArray.toString()+"\n";
                logOut.setText(log);
            }
        });

        delDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RealmDb.deleteWidgetsByPanelId("1");

                log += "已经删除panelId为1的所有控件\n";
                logOut.setText(log);
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = sendContent.getText().toString();

                Log.i("send",s);

                if(Global.out != null) {
                    Connect.SendMessage(s);
                }else {
                    Toast.makeText(getApplicationContext(),"没有连接！",Toast.LENGTH_SHORT).show();
                }

                log += "发出信息："+s+"\n";
                logOut.setText(log);
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log = "";
                logOut.setText(log);
            }
        });
    }
}
