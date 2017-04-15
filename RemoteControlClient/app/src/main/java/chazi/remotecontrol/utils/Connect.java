package chazi.remotecontrol.utils;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import android.content.Context;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


/**
 * Created by 595056078 on 2017/3/31.
 */

public class Connect {

    public Connect(){
        new Connect(Global.SERVER_IP);
    }

    public Connect(String address){
        new Connect(address,Global.SERVER_PORT);
    }

    public Connect(String address,int port){
        ConnectPhoneTask connectPhoneTask = new ConnectPhoneTask();
        connectPhoneTask.execute(address+":"+port);
    }

    public void SendMessage(String message){
        SendMessageTask sendMessageTask = new SendMessageTask();
        sendMessageTask.execute(message);
    }

    private class SendMessageTask extends AsyncTask<String,Void,Void> {
        @Override
        protected Void doInBackground(String... params) {
            if(Global.out != null) {
                Global.out.println(params[0]);
            }else {
                Toast.makeText(Global.context,"没有连接！",Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("SendMessage","message sent");
        }
    }

    private class ConnectPhoneTask extends AsyncTask<String,Void,Boolean> {

        private final String TAG = this.getClass().getSimpleName();

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = true;
            try {
                String addr_port = params[0];
                String ip = addr_port.split(":")[0];
                int port = Integer.parseInt(addr_port.split(":")[1]);

                InetAddress serverAddr = InetAddress.getByName(ip);

                Global.socket = new Socket(serverAddr, port);//Open socket on server IP and port
                //socket = new Socket(Global.SERVER_IP, Global.SERVER_PORT);//Open socket on server IP and port
            } catch (IOException e) {
                Log.e(TAG, "Error while connecting", e);
                result = false;
            }
            if(result){
                SharedPreferences.Editor editor= Global.context.getSharedPreferences("remote",Context.MODE_PRIVATE).edit();
                editor.putString("server_ip",params[0].split(":")[0]);
                editor.apply();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            Global.isConnected = result;
            Toast.makeText(Global.context,Global.isConnected?"Connected to server!":"Error while connecting", Toast.LENGTH_LONG).show();
            try {
                if(Global.isConnected) {
                    Global.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(Global.socket
                            .getOutputStream())), true); //create output stream to send data to server
                }
            }catch (IOException e){
                Log.e(TAG, "Error while creating OutWriter", e);
                Toast.makeText(Global.context,"Error while connecting",Toast.LENGTH_LONG).show();
            }
        }
    }
}
