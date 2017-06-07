package chazi.remotecontrol.utils;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import chazi.remotecontrol.db.RealmDb;
import chazi.remotecontrol.entity.IP;


/**
 * Created by 595056078 on 2017/3/31.
 */

public class Connect {


    public static final int CONNECT_SUCCESS = 0;
    public static final int CONNECT_FAIL = 1;
    public static final int SEND_SUCCESS = 2;
    public static final int NO_CONNECT = 3;
    public static final int ERROR_CREATING_IO = 4;
    public static final int SAVE_IP = 5;

    public static class ConnectHandler extends Handler{
        private MCallback mCallback;

        public ConnectHandler(MCallback callback) {
            mCallback = callback;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CONNECT_SUCCESS:
                    mCallback.onConnectSuccess();
                    break;
                case CONNECT_FAIL:
                    mCallback.onConnectFail();
                    break;
                case SEND_SUCCESS:
                    mCallback.onSendSuccess();
                    break;
                case NO_CONNECT:
                    mCallback.onNoConnect();
                    break;
                case ERROR_CREATING_IO:
                    mCallback.onErrorCreatingIO();
                    break;
                case SAVE_IP:
                    Bundle bundle = msg.getData();
                    String ip = bundle.getString("ip");
                    int port = bundle.getInt("port");
                    IP nIP = new IP(ip,port);

                    mCallback.onSaveIP(nIP);
                    break;
            }
            mCallback.onFinish();

        }

        public void setmCallback(MCallback callback){
            mCallback = callback;
        }

        public MCallback getmCallback(){
            return mCallback;
        }
    }

    public interface MCallback{
        void onConnectSuccess();
        void onConnectFail();
        void onSendSuccess();
        void onNoConnect();
        void onErrorCreatingIO();
        void onSaveIP(IP ip);
        void onFinish();
    }

    public Connect(){}


    public static void ConnectToServer(String address){
        ConnectToServer(address,Global.SERVER_PORT);
    }

    public static void ConnectToServer(String address,int port){
        ConnectToServerWithCallback(address,port,null);
    }

    public static ConnectPhoneTask ConnectToServerWithCallback(String address, int port, Handler handler){
        ConnectPhoneTask connectPhoneTask = new ConnectPhoneTask();
        connectPhoneTask.setHandler(handler);
        connectPhoneTask.execute(address+":"+port);
        return connectPhoneTask;
    }

    public static void SendMessage(String message){
        SendMessageTask sendMessageTask = new SendMessageTask();
        sendMessageTask.execute(message);
    }

    public static class SendMessageTask extends AsyncTask<String,Void,Void> {

        private Handler handler;

        @Override
        protected Void doInBackground(String... params) {
            Log.i("[Send Message]",params[0]);
            if(Global.out != null) {
                Global.out.println(params[0]);
            }else {
                if(handler!=null){
                    Message message = new Message();
                    message.what = 0;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("SendMessage","message sent");
        }

        public Handler getHandler() {
            return handler;
        }

        public void setHandler(Handler handler) {
            this.handler = handler;
        }
    }

    public static class ConnectPhoneTask extends AsyncTask<String,Void,Boolean> {

        private final String TAG = this.getClass().getSimpleName();
        private Handler handler = null;

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = true;
            String ip = "";
            int port = 4444;
            try {
                String addr_port = params[0];
                ip = addr_port.split(":")[0];
                port = Integer.parseInt(addr_port.split(":")[1]);

                InetAddress serverAddr = InetAddress.getByName(ip);

                Global.socket = new Socket(serverAddr, port);//Open socket on server IP and port
            } catch (IOException e) {
                Log.e(TAG, "Error while connecting", e);
                result = false;

                if(handler != null) {
                    Message message = new Message();
                    message.what = CONNECT_FAIL;
                    handler.sendMessage(message);
                }
            }
            if(result){
                if(handler != null) {
                    Message message = new Message();
                    message.what = SAVE_IP;

                    Bundle bundle = new Bundle();
                    bundle.putString("ip",ip);
                    bundle.putInt("port",port);

                    message.setData(bundle);

                    handler.sendMessage(message);
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            Global.isConnected = result;

            if(handler != null) {
                Message message = new Message();
                message.what = Global.isConnected ? CONNECT_SUCCESS : CONNECT_FAIL;
                handler.sendMessage(message);
            }

            try {
                if(Global.isConnected) {
                    Global.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(Global.socket
                            .getOutputStream())), true); //create output stream to send data to server
                    Global.in = new BufferedReader(new InputStreamReader(Global.socket.getInputStream()));
                }
            }catch (IOException e){
                Log.e(TAG, "Error while creating OutWriter or InReader", e);
            }
        }

        public Handler getHandler() {
            return handler;
        }

        public void setHandler(Handler handler) {
            this.handler = handler;
        }
    }
}
