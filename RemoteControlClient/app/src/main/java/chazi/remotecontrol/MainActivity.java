package chazi.remotecontrol;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chazi.remotecontrol.db.RealmDb;
import chazi.remotecontrol.entity.IP;
import chazi.remotecontrol.utils.Connect;
import chazi.remotecontrol.utils.Global;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity implements KeyEvent.Callback,AdapterView.OnItemClickListener{

    private RelativeLayout container;
    private EditText ip_input,port_input;
    private Button btn_link;
    private ImageView btn_show_hide;
    private TextView btn_offline;
    private ListView ipListView;
    private IPListAdapter adapter;
    private boolean isConnecting = false;

    private List<IP> IPList,matchList = new ArrayList<>();

    private boolean jumpWait = true;
    private Connect.ConnectPhoneTask connectPhoneTask;
    private Connect.MCallback mCallback = new Connect.MCallback() {
        @Override
        public void onConnectSuccess() {
            Toast.makeText(getApplicationContext(),"连接成功！",Toast.LENGTH_SHORT).show();

            final Intent intent = new Intent();
            intent.setClass(MainActivity.this, PanelListActivity.class);

            //若等待时间不足一秒则一秒后再跳转
            if(jumpWait) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                },1000);
            }else {
                startActivity(intent);
            }
        }

        @Override
        public void onConnectFail() {
            Toast.makeText(getApplicationContext(),"连接失败，请检查ip与端口号,并确认处于同一局域网！",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onSendSuccess() {

        }

        @Override
        public void onNoConnect() {

        }

        @Override
        public void onErrorCreatingIO() {
            Toast.makeText(getApplicationContext(),"创建输入输出流失败！",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onSaveIP(IP nIP) {
            RealmDb.saveIP(nIP);
        }

        @Override
        public void onFinish() {
            ip_input.setEnabled(true);
            port_input.setEnabled(true);

            btn_link.setText("连接");
            isConnecting = false;

            connectPhoneTask = null;
        }
    };
    private Connect.ConnectHandler handler = new Connect.ConnectHandler(mCallback);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RealmDb.initRealm(getApplicationContext());

        Global.context = getApplicationContext();

        container=(RelativeLayout)findViewById(R.id.container);

        ip_input = (EditText) findViewById(R.id.ip_input);
        port_input = (EditText) findViewById(R.id.port_input);
        btn_show_hide = (ImageView) findViewById(R.id.show_hide_list);
        btn_link = (Button) findViewById(R.id.btn_link);
        btn_offline = (TextView) findViewById(R.id.offline_mode);
        ipListView = (ListView) findViewById(R.id.history_list);

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ipListView.getVisibility() == View.VISIBLE){
                    hideList();
                }
            }
        });

        btn_show_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnecting) {
                    if (ipListView.getVisibility() == View.VISIBLE) {
                        hideList();
                    } else {
                        showList();
                    }
                }
            }
        });

        btn_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnecting) {
                    String ip, sPort;
                    int port = 4444;
                    ip = ip_input.getText().toString();
                    sPort = port_input.getText().toString();

                    Log.i("link", "ip is " + ip + " and port is " + port);

                    String[] ips = ip.split("\\.");
                    boolean ipFlag = true;
                    if (!(ips.length == 4)) {
                        ipFlag = false;
                        Log.i("judge ip", "length is " + ips.length);
                    } else {
                        for (String s : ips) {
                            try {
                                int group = Integer.valueOf(s);
                                if (group < 0 || group > 255) {
                                    ipFlag = false;
                                    break;
                                }
                            } catch (Exception e) {
                                Log.i("judge ip", e.toString());
                                ipFlag = false;
                                break;
                            }
                        }
                    }
                    if (!ipFlag) {
                        Toast.makeText(getApplicationContext(), "不合法的ip地址！", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean portFlag = true;
                        try {
                            port = Integer.valueOf(sPort);
                            if (port < 1024 || port > 65535) {
                                portFlag = false;
                            }
                        } catch (Exception e) {
                            Log.i("judge port", e.toString());
                            portFlag = false;
                        }
                        if (!portFlag) {
                            Toast.makeText(getApplicationContext(), "不合法的端口号！", Toast.LENGTH_SHORT).show();
                        } else {
                            connectPhoneTask = Connect.ConnectToServerWithCallback(ip, port, handler);
                            btn_link.setText("取消连接");
                            isConnecting = true;
                            jumpWait = true;
                            ip_input.setEnabled(false);
                            port_input.setEnabled(false);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    jumpWait = false;
                                }
                            }, 1000);
                        }
                    }
                }else {
                    if(connectPhoneTask != null){
                        connectPhoneTask.cancel(true);
                    }
                    btn_link.setText("连接");
                    isConnecting = false;
                    ip_input.setEnabled(true);
                    port_input.setEnabled(true);
                }
            }
        });

        btn_offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,PanelListActivity.class);
                startActivity(intent);
            }
        });

        IPList = RealmDb.getAllIp();
        matchList.addAll(IPList);
        adapter = new IPListAdapter(getApplicationContext(),R.layout.item_ip,matchList);
        ipListView.setAdapter(adapter);
        ipListView.setOnItemClickListener(this);

        ip_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String word = s.toString();

                Pattern pattern = Pattern.compile(".*" + word + ".*");
                Matcher matcher;

                adapter.clear();
                for (IP ip : IPList) {
                    matcher = pattern.matcher(ip.getIpAdr());
                    if (matcher.matches()) {
                        matchList.add(ip);
                    }
                }
                if(matchList.size() == 0){
                    hideList();
                }else {
                    showList();
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        IP ip = adapter.getItem(position);
        ip_input.setText(ip.getIpAdr());
        port_input.setText(ip.getPort()+"");
        hideList();
    }

    private void showList(){
        btn_show_hide.setImageResource(R.drawable.ic_arrow_up);
        ipListView.setVisibility(View.VISIBLE);
    }

    private void hideList(){
        btn_show_hide.setImageResource(R.drawable.ic_arrow_down);
        ipListView.setVisibility(View.GONE);
    }
}
