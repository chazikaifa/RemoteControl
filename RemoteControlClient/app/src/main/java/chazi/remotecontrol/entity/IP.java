package chazi.remotecontrol.entity;

import io.realm.RealmObject;

/**
 * Created by 595056078 on 2017/4/6.
 */

public class IP extends RealmObject {
    private String ipAdr;
    private int port;

    public IP(){
        this("192.168.199.125",9999);
    }

    public IP(String ipAdr){
        this(ipAdr,9999);
    }

    public IP(String ipAdr, int port) {
        this.ipAdr = ipAdr;
        this.port = port;
    }

    public String getIpAdr() {
        return ipAdr;
    }

    public void setIpAddr(String ipAdr) {
        this.ipAdr = ipAdr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
