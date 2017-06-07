package chazi.remotecontrol.db;

import java.util.ArrayList;
import java.util.List;

import chazi.remotecontrol.entity.IP;
import chazi.remotecontrol.entity.Panel;
import chazi.remotecontrol.entity.Widget;
import chazi.remotecontrol.utils.ContentCreator;
import io.realm.Realm;
import io.realm.Sort;

import android.content.Context;
import android.util.Log;

import java.util.Collections;

import static chazi.remotecontrol.utils.Global.config;

/**
 * Created by 595056078 on 2017/4/6.
 */

public class RealmDb {
    public static Realm realm;

    //=======================================================Widget================================================================================

    public static void saveWidgets(List<Widget> widgets,String panelId){
        Log.i("Realm","save widgets in "+panelId);

        deleteWidgetsByPanelId(panelId);

        realm.beginTransaction();
        realm.copyToRealm(widgets);
        realm.commitTransaction();
    }

    public static void addWidgets(List<Widget> widgets){
        Log.i("Realm","add widgets");

        realm.beginTransaction();
        realm.copyToRealm(widgets);
        realm.commitTransaction();
    }

    public static void deleteWidgetsByPanelId(String panelId){
        Log.i("Realm","delete widgets in "+panelId);

        realm.beginTransaction();
        realm.where(Widget.class).equalTo("panelId",panelId).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public static List<Widget> getWidgetsByPanelId(String id){
        return realm.where(Widget.class).equalTo("panelId",id).findAll();
    }

    public static void  copyWidgetToPanel(Widget widget,String id){
        Log.i("Realm","copy widgets to "+id);

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
                widget.setContent(ContentCreator.key(ContentCreator.KEY_CLICK,key));
                widget.setType(1);

                defaultKeys.add(widget);
            }
        }

        realm.beginTransaction();
        realm.copyToRealm(defaultKeys);
        realm.commitTransaction();
    }

    //========================================================Panel==============================================================================

    public static boolean isPanelExist(String id){
        List<Panel> panels = realm.where(Panel.class).equalTo("id",id).findAll();
        return panels.size() > 0;
    }

    public static void savePanel(Panel panel){
        Log.i("Realm","savePanel");

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(panel);
        realm.commitTransaction();
    }

    public static void savePanels(List<Panel> panels){
        Log.i("Realm","savePanels");

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(panels);
        realm.commitTransaction();
    }

    public static void deletePanelById(String id){
        Log.i("Realm","deletePanels"+id);

        deleteWidgetsByPanelId(id);

        realm.beginTransaction();
        //虽说应该至多只有一个Panel对应
        //但用findFirst可能会产生空指针异常，即找不到会返回null
        //所以用findAll会更稳定
        realm.where(Panel.class).equalTo("id",id).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public static List<Panel> getAllPanels(){
        return realm.where(Panel.class).findAll().sort("order", Sort.ASCENDING);
    }

    //获取测试数据
    public static void getTestPanels(){

        List<Panel> panels = realm.where(Panel.class).contains("id","test").findAll();

        if(panels.isEmpty()) {

            Log.i("getTest","newTest");

            panels = new ArrayList<>();

            realm.beginTransaction();

            Panel panel1 = new Panel("test1", "按键", 1);
            Panel panel2 = new Panel("test2", "状态按键", 2);
            Panel panel3 = new Panel("test3", "滚轮", 3);
            Panel panel4 = new Panel("test4", "触摸板", 4);
            Panel panel5 = new Panel("test5", "输入框", 5);
            Panel panel6 = new Panel("test6", "组合键", 6);
            Panel panel7 = new Panel("test7", "摇杆", 7);

            panels.add(panel1);
            panels.add(panel2);
            panels.add(panel3);
            panels.add(panel4);
            panels.add(panel5);
            panels.add(panel6);
            panels.add(panel7);

            realm.copyToRealmOrUpdate(panels);

            //按键
            Widget widget = new Widget(panel1.getId(),1);
            widget.setX(300);
            widget.setY(300);
            widget.setName("按键A");
            widget.setContent(ContentCreator.key(ContentCreator.KEY_CLICK,ContentCreator.KEY_A));

            realm.copyToRealm(widget);

            //状态按键
            widget = new Widget(panel2.getId(),2);
            widget.setX(300);
            widget.setY(300);
            widget.setName("状态按键A");
            widget.setContent(ContentCreator.key(ContentCreator.KEY_CLICK,ContentCreator.KEY_A));

            realm.copyToRealm(widget);

            //滚轮
            widget = new Widget(panel3.getId(),3);
            widget.setX(300);
            widget.setY(300);
            widget.setContent("50");

            realm.copyToRealm(widget);

            //触摸板
            widget = new Widget(panel4.getId(),4);
            widget.setX(10);
            widget.setY(50);
            widget.setContent("1000");

            realm.copyToRealm(widget);

            //输入框
            widget = new Widget(panel5.getId(),5);
            widget.setX(5);
            widget.setY(10);

            realm.copyToRealm(widget);

            //自定义按键
            widget = new Widget(panel6.getId(),6);
            widget.setX(300);
            widget.setY(300);
            widget.setName("下一首");

            String content = "";
            content = ContentCreator.key(ContentCreator.KEY_PRESS,ContentCreator.KEY_CTRL,content);
            content = ContentCreator.key(ContentCreator.KEY_PRESS,ContentCreator.KEY_RIGHT,content);

            content = ContentCreator.key(ContentCreator.KEY_RELEASE,ContentCreator.KEY_RIGHT,content);
            content = ContentCreator.key(ContentCreator.KEY_RELEASE,ContentCreator.KEY_CTRL,content);

            widget.setContent(content);

            realm.copyToRealm(widget);

            //摇杆
            widget = new Widget(panel7.getId(),7);
            widget.setX(300);
            widget.setY(300);
            widget.setContent("1");

            realm.copyToRealm(widget);

            realm.commitTransaction();
        }
    }
    //========================================================IP==================================================================================

    //倒序输出，排在前面的是新数据
    public static List<IP> getAllIp(){
        List<IP> ipList = new ArrayList<>();
        ipList.addAll(realm.where(IP.class).findAll());
        Collections.reverse(ipList);
        return ipList;
    }

    public static void saveIP(IP ip){
        //先找出ip和port相同的IP,(讲道理应该最多一个)
        List<IP> sameIP = realm.where(IP.class).equalTo("ipAdr",ip.getIpAdr()).equalTo("port",ip.getPort()).findAll();

        for (IP sIP:sameIP){
            deleteIP(sIP);
        }

        realm.beginTransaction();
        //如果长度大于0，则表示已经有相同的了,将该IP提前显示，其他顺序不变
        //为了做到这个，把原来的IP删掉，重新存入一条
        //否则直接存入
        //在下次查询之后将List<IP>倒序显示即可
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

        //Widget表中panelId为0的数据条数为0说明为新数据库，写入默认按键数据
        if(getWidgetsByPanelId("0").size() == 0){
            refreshDefaultKeys();
        }

        getTestPanels();
    }

    /**
     * 清除所有数据
     */
    public static void deleteAllData(){
        // Start with a clean slate every time
        Realm.deleteRealm(config);
    }
}
