package chazi.remotecontrol.entity;

import java.util.ArrayList;
import java.util.List;

import chazi.remotecontrol.utils.ContentCreator;

/**
 * Created by 595056078 on 2017/6/5.
 */

public class Order {
    private int orderType;
    private String orderParam;

    public Order(){

    }

    public Order(int orderType, String orderParam){

        this.orderType = orderType;
        this.orderParam = orderParam;
    }

    public Order(String command){
        this();

        String[] order = command.split("~");
        if(order.length==2){
            setOrderParam(order[1]);
            switch (order[0]){
                case "mouse":
                    if(order[1].split(",").length >1){
                        setOrderType(ContentCreator.ORDER_TYPE_MOUSE_MOVE);
                    }else {
                        setOrderType(ContentCreator.ORDER_TYPE_MOUSE_CLICK);
                    }
                    break;
                case "mouse@":
                    setOrderType(ContentCreator.ORDER_TYPE_MOUSE_MOVE_ABS);
                    break;
                case "mouseHold":
                    setOrderType(ContentCreator.ORDER_TYPE_MOUSE_HOLD);
                    break;
                case "mouseRelease":
                    setOrderType(ContentCreator.ORDER_TYPE_MOUSE_RELEASE);
                    break;
                case "key":
                    setOrderType(ContentCreator.ORDER_TYPE_KEY);
                    break;
                case "keyDown":
                    setOrderType(ContentCreator.ORDER_TYPE_KEY_DOWN);
                    break;
                case "keyUp":
                    setOrderType(ContentCreator.ORDER_TYPE_KEY_UP);
                    break;
                case "string":
                    setOrderType(ContentCreator.ORDER_TYPE_STRING);
                    break;
                case "delay":
                    setOrderType(ContentCreator.ORDER_TYPE_DELAY);
                    break;
            }
        }
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public String getOrderParam() {
        return orderParam;
    }

    public void setOrderParam(String orderParam) {
        this.orderParam = orderParam;
    }

    public static List<Order> getOrderListFromContent(String content){
        List<Order> orderList = new ArrayList<>();

        String[] contents = content.split("~");
        int n;
        if(contents.length % 2 != 0){
            n = (contents.length-1)/2;
        }else {
            n = contents.length/2;
        }

        for(int i=0;i<n;i++){
            String s = contents[i*2]+"~"+contents[i*2+1];
            Order order = new Order(s);
            orderList.add(order);
        }

        return orderList;
    }

}
