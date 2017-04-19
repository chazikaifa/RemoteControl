package chazi.remotecontrol.utils;

import android.content.Context;
import java.net.Socket;
import java.io.PrintWriter;

import io.realm.RealmConfiguration;

/**
 * Created by lenovo on 15/02/2017.
 */

public class Global {

    public static final String SERVER_IP = "192.168.199.131";
    public static final int SERVER_PORT = 4444;

    public static RealmConfiguration config;

    public static Socket socket;
    public static PrintWriter out;
    public static Context context;
    public static boolean isConnected = false;

    public static final String PLAY="play";
    public static final String MOUSE_LEFT_CLICK="left_click";
}
