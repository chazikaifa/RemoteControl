package chazi.remotecontrol.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

import chazi.remotecontrol.db.RealmDb;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.internal.Collection;

/**
 * Created by 595056078 on 2017/4/5.
 */

public class Panel extends RealmObject implements Parcelable{

    //id采用UUID作主键，为-1时表示为自定义按键，0为默认按键
    @PrimaryKey
    private String id;
    private String name;
    private int order;
    private boolean vertical;

    public Panel(){

    }

    public Panel(String name){
        String uid = UUID.randomUUID().toString();
        new Panel(uid,name,100);

    }

    public Panel(String name,String id){
        new Panel(id,name,100);
    }

    public Panel(String name, int order) {
        String uid = UUID.randomUUID().toString();
        new Panel(uid,name,order,true);
    }


    public Panel(String id, String name, int order) {
        new Panel(id,name,order,true);
    }

    public Panel(String id, String name, int order,boolean isVertical) {
        this.id = id;
        this.name = name;
        this.order = order;
        vertical = isVertical;
    }

    //由于JSON格式包含控件信息，直接对数据库进行操作
    //为避免JSON解析出错，数据库操作放在最后
    public Panel(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("id");
        name = jsonObject.getString("name");
        order = jsonObject.getInt("order");
        vertical = jsonObject.getBoolean("vertical");

        JSONArray jsonArray = jsonObject.getJSONArray("widgets");
        int size = jsonArray.length();
        List<Widget> widgetList = new ArrayList<>();
        for(int i = 0;i < size;i++){
            Widget widget = new Widget(jsonArray.getJSONObject(i));
            widgetList.add(widget);
        }

        RealmDb.saveWidgets(widgetList,id);
        RealmDb.savePanel(this);
    }

    protected Panel(Parcel in) {
        id = in.readString();
        name = in.readString();
        order = in.readInt();
        vertical = in.readByte() != 0;
    }

    public static final Creator<Panel> CREATOR = new Creator<Panel>() {
        @Override
        public Panel createFromParcel(Parcel in) {
            return new Panel(in);
        }

        @Override
        public Panel[] newArray(int size) {
            return new Panel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    public JSONObject toJson(){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("id",id);
            jsonObject.put("name",name);
            jsonObject.put("order",order);
            jsonObject.put("vertical",vertical);

            List<Widget> widgetList = RealmDb.getWidgetsByPanelId(id);
            JSONArray jsonArray = new JSONArray();
            for(Widget widget:widgetList){
                jsonArray.put(widget.toJson());
            }

            jsonObject.put("widgets",jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeInt(order);
        parcel.writeByte((byte) (vertical ? 1 : 0));
    }
}
