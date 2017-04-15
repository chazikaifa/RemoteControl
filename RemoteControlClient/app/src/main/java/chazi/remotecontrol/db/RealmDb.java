package chazi.remotecontrol.db;

import java.util.ArrayList;
import java.util.List;

import chazi.remotecontrol.entity.IP;
import chazi.remotecontrol.entity.Panel;
import chazi.remotecontrol.entity.Widget;
import chazi.remotecontrol.utils.ContentCreator;
import io.realm.Realm;
import android.content.Context;

import java.util.Collections;

import static chazi.remotecontrol.utils.Global.config;

/**
 * Created by 595056078 on 2017/4/6.
 */

public class RealmDb {
    public static Realm realm = null;

    //=======================================================Widget================================================================================

    public static void saveWidgets(List<Widget> widgets,String panelId){
        deleteWidgetsByPanelId(panelId);

        realm.beginTransaction();
        realm.copyToRealm(widgets);
        realm.commitTransaction();
    }

    public static void addWidgets(List<Widget> widgets){
        realm.beginTransaction();
        realm.copyToRealm(widgets);
        realm.commitTransaction();
    }

    public static void deleteWidgetsByPanelId(String panelId){
        realm.beginTransaction();
        realm.where(Widget.class).equalTo("panelId",panelId).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public static List<Widget> getWidgetsByPanelId(String id){
        return realm.where(Widget.class).equalTo("panelId",id).findAll();
    }

    public static void  copyWidgetToPanel(Widget widget,String id){
        Widget newWidget = new Widget(widget);
        newWidget.setPanelId(id);

        realm.beginTransaction();
        realm.copyToRealm(newWidget);
        realm.commitTransaction();
    }

    //刷新默认控件
    public static void refreshDefaultKeys(){
        //为防止重复先将原有的默认控件删除
        deleteWidgetsByPanelId("0");

        List<Widget> defaultKeys = new ArrayList<>();

        for(String[] stringGroup: ContentCreator.defaultKeys){
            for (String key:stringGroup){
                Widget widget = new Widget();
                widget.setPanelId("0");
                widget.setName(key);
                widget.setContent("key-"+key);
                widget.setType(1);

                defaultKeys.add(widget);
            }
        }

        realm.beginTransaction();
        realm.copyToRealm(defaultKeys);
        realm.commitTransaction();
    }

    //========================================================Panel==============================================================================

    public static void savePanel(Panel panel){
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(panel);
        realm.commitTransaction();
    }

    public static void savePanels(List<Panel> panels){
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(panels);
        realm.commitTransaction();
    }

    public static void deletePanelById(String id){
        deleteWidgetsByPanelId(id);

        realm.beginTransaction();
        //虽说应该至多只有一个Panel对应
        //但用findFirst可能会产生空指针异常，即找不到会返回null
        //所以用findAll会更稳定
        realm.where(Panel.class).equalTo("id",id).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public static List<Panel> getAllPanel(){
        return realm.where(Panel.class).findAll();
    }
    //========================================================IP==================================================================================

    //倒序输出，排在前面的是新数据
    public static List<IP> getAllIp(){
        List<IP> ipList = realm.where(IP.class).findAll();
        Collections.reverse(ipList);
        return ipList;
    }

    public static void saveIP(IP ip){
        //先找出ip和port相同的IP,(讲道理应该最多一个)
        List<IP> sameIP = realm.where(IP.class).equalTo("ipAdr",ip.getIpAdr()).equalTo("port",ip.getPort()).findAll();

        realm.beginTransaction();
        //如果长度大于0，则表示已经有相同的了,将该IP提前显示，其他顺序不变
        //为了做到这个，把原来的IP删掉，重新存入一条
        //否则直接存入
        //在下次查询之后将List<IP>倒序显示即可
        if(sameIP.size() > 0){
            realm.where(IP.class).equalTo("ipAdr",ip.getIpAdr()).equalTo("port",ip.getPort()).findAll().deleteAllFromRealm();
        }
        realm.copyToRealm(ip);
        realm.commitTransaction();
    }

    public static void deleteIP(IP ip){
        realm.beginTransaction();
        realm.where(IP.class).equalTo("ipAdr",ip.getIpAdr()).equalTo("port",ip.getPort()).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    //========================================================All=================================================================================

    public static void initRealm(Context context){
        //初始化数据库,若不存在，创建数据库
        Realm.init(context);
        realm = Realm.getDefaultInstance();
        //保存数据库的config
        config = RealmDb.realm.getConfiguration();

        //Widget表中panelId为-1的数据条数为0说明为新数据库，写入默认按键数据
        if(getWidgetsByPanelId("0").size() == 0){
            refreshDefaultKeys();
        }
    }

    /**
     * 清除所有数据
     */
    public static void deleteAllData(){
        // Start with a clean slate every time
        Realm.deleteRealm(config);
    }
}
