package chazi.remotecontrol.entity;

import io.realm.RealmObject;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 595056078 on 2017/4/3.
 */

public class Widget extends RealmObject {
    private String panelId;
    private int X;
    private int Y;
    private int width;
    private int height;
    private int type;
    //type表示该控件为以下类型:
    //1     按键
    //2     按键状态
    //3     滚轮
    //4     触摸屏
    //5     输入框
    //6     自定义按键
    //7     摇杆

    private String content;
    //该字段用于保存该控件的属性，不同类型的控件以不同方式解释
    //type =
    //1     按键,直接发送该字段, 字段应为"key-"+按键名
    //2     按键状态,保存按键名，按键功能在"keyUp"和"keyDown"间切换
    //3     滚轮,灵敏度
    //4     触摸屏，灵敏度
    //5     输入框,暂无
    //6     组合键,直接发送该字段
    //7     摇杆,摇杆类型

    private String name;

    //这个构造函数生成的控件如果panelId不进行修改即只存为自定义控件
    public Widget(){
        new Widget("-1");
    }

    public Widget(String panelId){
        new Widget(panelId,0,0,0,0,0," "," ");
    }

    public Widget(String panelId,int x,int y,int width,int height){
        new Widget(panelId,x,y,width,height,0," "," ");
    }

    public Widget(String panelId,int x,int y,int width,int height,int type){
        new Widget(panelId,x,y,width,height,type," "," ");
    }

    public Widget(String panelId, int x, int y, int width, int height, int type, String content, String name){
        this.panelId = panelId;
        X = x;
        Y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.content = content;
        this.name = name;
    }

    public Widget(Widget widget){
        this.panelId = widget.getPanelId();
        X = widget.getX();
        Y = widget.getY();
        this.width = widget.getWidth();
        this.height = widget.getHeight();
        this.type = widget.getType();
        this.content = widget.getContent();
        this.name = widget.getName();
    }

    public Widget(JSONObject jsonObject) throws JSONException {
        this.panelId = jsonObject.getString("panelId");
        X = jsonObject.getInt("X");
        Y = jsonObject.getInt("Y");
        this.width = jsonObject.getInt("width");
        this.height = jsonObject.getInt("height");
        this.type = jsonObject.getInt("type");
        this.content = jsonObject.getString("content");
        this.name = jsonObject.getString("name");
    }

    public String getPanelId() {
        return panelId;
    }

    public void setPanelId(String pannelId) {
        this.panelId = pannelId;
    }

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JSONObject toJson(){

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("panelId",panelId);
            jsonObject.put("X",X);
            jsonObject.put("Y",Y);
            jsonObject.put("width",width);
            jsonObject.put("height",height);
            jsonObject.put("type",type);
            jsonObject.put("content",content);
            jsonObject.put("name",name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}


