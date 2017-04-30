package chazi.remotecontrol.entity;

import android.content.Context;

import chazi.remotecontrol.utils.DensityUtil;
import io.realm.RealmObject;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 595056078 on 2017/4/3.
 */

public class Widget extends RealmObject {
    private String panelId;
    private float X;
    private float Y;
    private float width;//以dp为单位，方便适配不同机型
    private float height;
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

    public Widget(String panelId,float x,float y,float width,float height){
        new Widget(panelId,x,y,width,height,0," "," ");
    }

    public Widget(String panelId,float x,float y,float width,float height,int type){
        new Widget(panelId,x,y,width,height,type," "," ");
    }

    public Widget(String panelId, float x, float y, float width, float height, int type, String content, String name){
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
        X = (float) jsonObject.getDouble("X");
        Y = (float) jsonObject.getDouble("Y");
        this.width = (float) jsonObject.getDouble("width");
        this.height = (float) jsonObject.getDouble("height");
        this.type = jsonObject.getInt("type");
        this.content = jsonObject.getString("content");
        this.name = jsonObject.getString("name");
    }

    public String getPanelId() {
        return panelId;
    }

    public void setPanelId(String panelId) {
        this.panelId = panelId;
    }

    public float getX() {
        return X;
    }

    public void setX(float x) {
        X = x;
    }

    public float getY() {
        return Y;
    }

    public void setY(float y) {
        Y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setWidthInPx(float pxw, Context context){
        width = DensityUtil.px2dip(context,pxw);
    }

    public int getWidthInPx(Context context){
        return DensityUtil.dip2px(context, width);
    }

    public void setHeightInPx(float pxh, Context context){
        height = DensityUtil.px2dip(context,pxh);
    }

    public int getHeightInPx(Context context){
        return DensityUtil.dip2px(context, height);
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


