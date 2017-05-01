package chazi.remotecontrol;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import chazi.remotecontrol.db.RealmDb;
import chazi.remotecontrol.entity.Panel;
import chazi.remotecontrol.utils.Connect;
import chazi.remotecontrol.utils.ContentCreator;
import chazi.remotecontrol.utils.Global;
import chazi.remotecontrol.utils.RsSharedUtil;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, KeyEvent.Callback{

    Button playPauseButton;
    Button testButton,btn_left,btn_right,view_test;
    TextView mousePad;

    private boolean mouseMoved=false;
    public RelativeLayout relativeLayout;

    private float initX =0;
    private float initY =0;
    private float disX =0;
    private float disY =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RealmDb.initRealm(getApplicationContext());

        Global.context = getApplicationContext();
        playPauseButton = (Button)findViewById(R.id.playPauseButton);

        view_test = (Button) findViewById(R.id.view_test);
        view_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Panel panel = new Panel("1","面板1",0,true);

                Bundle bundle = new Bundle();
                bundle.putParcelable("panel",panel);

                Intent intent = new Intent();
                intent.setClass(MainActivity.this,PanelListActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        testButton = (Button) findViewById(R.id.toTest);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,TestActivity.class);
                startActivity(intent);
            }
        });

        btn_left = (Button) findViewById(R.id.left_click);
        btn_right = (Button) findViewById(R.id.right_click);

        btn_left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();

                switch (action){
                    case MotionEvent.ACTION_BUTTON_PRESS:
                    case MotionEvent.ACTION_DOWN:
                        Connect.SendMessage(ContentCreator.Click(ContentCreator.MOUSE_PRESS_LEFT));
                        break;
                    case MotionEvent.ACTION_BUTTON_RELEASE:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_HOVER_EXIT:
                        Connect.SendMessage(ContentCreator.Click(ContentCreator.MOUSE_RELEASE_LEFT));
                        break;
                }

                return false;
            }
        });

        btn_right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();

                switch (action){
                    case MotionEvent.ACTION_BUTTON_PRESS:
                    case MotionEvent.ACTION_DOWN:
                        Connect.SendMessage(ContentCreator.Click(ContentCreator.MOUSE_PRESS_RIGHT));
                        break;
                    case MotionEvent.ACTION_BUTTON_RELEASE:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_HOVER_EXIT:
                        Connect.SendMessage(ContentCreator.Click(ContentCreator.MOUSE_RELEASE_RIGHT));
                        break;
                }

                return false;
            }
        });

        relativeLayout=(RelativeLayout)findViewById(R.id.activity_main);

        playPauseButton.setOnClickListener(this);

        mousePad = (TextView)findViewById(R.id.mousePad);
        mousePad.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(Global.isConnected && Global.out!=null){
                    switch(event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            //save X and Y positions when user touches the TextView
                            initX =event.getX();
                            initY =event.getY();
                            mouseMoved=false;
                            Log.i("Mouse","Down");
                            break;
                        case MotionEvent.ACTION_MOVE:
                            disX = event.getX()- initX;
                            disY = event.getY()- initY;
                            initX = event.getX();
                            initY = event.getY();
                            if(disX !=0|| disY !=0){
                                Connect.SendMessage(ContentCreator.move(disX,disY));
                            }

                            //当移动范围小，当做点击
                            if(abs(disX)>2 && abs(disY)>2) {
                                mouseMoved = true;
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            //consider a tap only if usr did not move mouse after ACTION_DOWN
                            if(!mouseMoved){
                                Connect.SendMessage(ContentCreator.Click(ContentCreator.MOUSE_CLICK_LEFT));
                                Log.i("Mouse","Click");
                            }
                    }
                }
                return true;
            }
        });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        String s = String.valueOf((char)event.getUnicodeChar());

        if(event.getUnicodeChar()!=0)
            Connect.SendMessage(ContentCreator.key(ContentCreator.KEY_CLICK,s));
//            Connect.SendMessage("key~"+event.getUnicodeChar());

        return super.onKeyUp(keyCode, event);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playPauseButton:
                InputMethodManager inputMethodManager =
                        (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(
                        relativeLayout.getApplicationWindowToken(),
                        InputMethodManager.SHOW_FORCED, 0);
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_connect) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Enter Server IP Address");
            //alertDialog.setMessage("Enter Server IP address");

            final EditText input = new EditText(MainActivity.this);
            String serverIP= RsSharedUtil.getString(getApplicationContext(),"server_ip");
            input.setText(serverIP);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            lp.setMargins(100,100,100,100);
            alertDialog.setView(input);
            alertDialog.setIcon(R.drawable.ic_cast_white_24dp);

            alertDialog.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String server_ip = input.getText().toString();
                            Connect.ConnectToServer(server_ip);
                        }
                    });

            alertDialog.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    public class SendMessage extends AsyncTask<String,Void,Void>{
//        @Override
//        protected Void doInBackground(String... params) {
//            if(out != null) {
//                out.println(params[0]);
//            }else {
//                Toast.makeText(getApplicationContext(),"没有连接！",Toast.LENGTH_SHORT).show();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            Log.d("SendMessage","message sent");
//        }
//    }
//
//    public class ConnectPhoneTask extends AsyncTask<String,Void,Boolean> {
//
//        private final String TAG = this.getClass().getSimpleName();
//
//        @Override
//        protected Boolean doInBackground(String... params) {
//            boolean result = true;
//            try {
//                InetAddress serverAddr = InetAddress.getByName(params[0]);
//
//                socket = new Socket(serverAddr, Global.SERVER_PORT);//Open socket on server IP and port
//                //socket = new Socket(Global.SERVER_IP, Global.SERVER_PORT);//Open socket on server IP and port
//            } catch (IOException e) {
//                Log.e(TAG, "Error while connecting", e);
//                result = false;
//            }
//            if(result){
//                SharedPreferences.Editor editor=getSharedPreferences("remote",MODE_PRIVATE).edit();
//                editor.putString("server_ip",params[0]);
//                editor.apply();
//            }
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result)
//        {
//            isConnected = result;
//            Toast.makeText(context,isConnected?"Connected to server!":"Error while connecting", Toast.LENGTH_LONG).show();
//            try {
//                if(isConnected) {
//                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
//                            .getOutputStream())), true); //create output stream to send data to server
//                }
//            }catch (IOException e){
//                Log.e(TAG, "Error while creating OutWriter", e);
//                Toast.makeText(context,"Error while connecting",Toast.LENGTH_LONG).show();
//            }
//        }
//    }
}
