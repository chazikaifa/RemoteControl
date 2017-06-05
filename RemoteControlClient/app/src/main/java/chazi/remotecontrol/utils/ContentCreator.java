package chazi.remotecontrol.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chazi.remotecontrol.entity.Order;

/**
 * Created by 595056078 on 2017/4/12.
 * 这个类用于快速生成按键内容以及组合键的拼接
 * 其中包含字符串的转义
 */

public class ContentCreator {

    public static final int ORDER_TYPE_MOUSE_MOVE = 0;
    public static final int ORDER_TYPE_MOUSE_MOVE_ABS = 1;
    public static final int ORDER_TYPE_MOUSE_CLICK = 2;
    public static final int ORDER_TYPE_MOUSE_HOLD = 3;
    public static final int ORDER_TYPE_MOUSE_RELEASE = 4;
    public static final int ORDER_TYPE_KEY = 5;
    public static final int ORDER_TYPE_KEY_DOWN = 6;
    public static final int ORDER_TYPE_KEY_UP = 7;
    public static final int ORDER_TYPE_WHEEL = 8;
    public static final int ORDER_TYPE_STRING = 9;
    public static final int ORDER_TYPE_DELAY = 10;

    public static final int MOUSE_CLICK_LEFT = 1;
    public static final int MOUSE_PRESS_LEFT = 2;
    public static final int MOUSE_RELEASE_LEFT = 3;
    public static final int MOUSE_DOUBLE_CLICK_LEFT = 4;
    public static final int MOUSE_CLICK_RIGHT = -1;
    public static final int MOUSE_PRESS_RIGHT = -2;
    public static final int MOUSE_RELEASE_RIGHT = -3;
    public static final int MOUSE_DOUBLE_CLICK_RIGHT = -4;

    public static final int KEY_CLICK = 0;
    public static final int KEY_PRESS = 1;
    public static final int KEY_RELEASE = -1;

    public static final String KEY_A = "a";
    public static final String KEY_B = "b";
    public static final String KEY_C = "c";
    public static final String KEY_D = "d";
    public static final String KEY_E = "e";
    public static final String KEY_F = "f";
    public static final String KEY_G = "g";
    public static final String KEY_H = "h";
    public static final String KEY_I = "i";
    public static final String KEY_J = "j";
    public static final String KEY_K = "k";
    public static final String KEY_L = "l";
    public static final String KEY_M = "m";
    public static final String KEY_N = "n";
    public static final String KEY_O = "o";
    public static final String KEY_P = "p";
    public static final String KEY_Q = "q";
    public static final String KEY_R = "r";
    public static final String KEY_S = "s";
    public static final String KEY_T = "t";
    public static final String KEY_U = "u";
    public static final String KEY_V = "v";
    public static final String KEY_W = "w";
    public static final String KEY_X = "x";
    public static final String KEY_Y = "y";
    public static final String KEY_Z = "z";

    public static final String[] letters = {
            KEY_A, KEY_B, KEY_C, KEY_D, KEY_E, KEY_F, KEY_G,
            KEY_H, KEY_I, KEY_J, KEY_K, KEY_L, KEY_M, KEY_N,
            KEY_O, KEY_P, KEY_Q, KEY_R, KEY_S, KEY_T,
            KEY_U, KEY_V, KEY_W, KEY_X, KEY_Y, KEY_Z
    };

    public static final String KEY_NUM0 = "0";
    public static final String KEY_NUM1 = "1";
    public static final String KEY_NUM2 = "2";
    public static final String KEY_NUM3 = "3";
    public static final String KEY_NUM4 = "4";
    public static final String KEY_NUM5 = "5";
    public static final String KEY_NUM6 = "6";
    public static final String KEY_NUM7 = "7";
    public static final String KEY_NUM8 = "8";
    public static final String KEY_NUM9 = "9";

    public static final String[] numbers = {
            KEY_NUM0, KEY_NUM1, KEY_NUM2,
            KEY_NUM3, KEY_NUM4, KEY_NUM5,
            KEY_NUM6, KEY_NUM7, KEY_NUM8,
            KEY_NUM9
    };

    public static final String KEY_F1 = "f1";
    public static final String KEY_F2 = "f2";
    public static final String KEY_F3 = "f3";
    public static final String KEY_F4 = "f4";
    public static final String KEY_F5 = "f5";
    public static final String KEY_F6 = "f6";
    public static final String KEY_F7 = "f7";
    public static final String KEY_F8 = "f8";
    public static final String KEY_F9 = "f9";
    public static final String KEY_F10 = "f10";
    public static final String KEY_F11 = "f11";
    public static final String KEY_F12 = "f12";

    public static final String[] KEY_Fs = {
            KEY_F1, KEY_F2, KEY_F3,
            KEY_F4, KEY_F5, KEY_F6,
            KEY_F7, KEY_F8, KEY_F9,
            KEY_F10, KEY_F11, KEY_F12
    };

    public static final String KEY_HYPHEN = "/s";
    public static final String KEY_DIFFERENT_NOTES = "`";
    public static final String KEY_EXCLAMATION_MARK = "!";
    public static final String KEY_AT_SIGN = "@";
    public static final String KEY_WELL_NO = "#";
    public static final String KEY_DOLLAR = "$";
    public static final String KEY_PER_CENT = "%";
    public static final String KEY_CARET = "^";
    public static final String KEY_AND = "&";
    public static final String KEY_ASTERISK = "*";
    public static final String KEY_LEFT_BRACKET = "(";
    public static final String KEY_RIGHT_BRACKET = ")";
    public static final String KEY_MINUS = "-";
    public static final String KEY_UNDERLINE = "_";
    public static final String KEY_EQUAL = "=";
    public static final String KEY_PLUS = "+";

    public static final String[] symbol_group_1 = {
            KEY_HYPHEN, KEY_DIFFERENT_NOTES,
            KEY_EXCLAMATION_MARK, KEY_AT_SIGN, KEY_WELL_NO,
            KEY_DOLLAR, KEY_PER_CENT, KEY_CARET,
            KEY_AND, KEY_ASTERISK, KEY_LEFT_BRACKET,
            KEY_RIGHT_BRACKET, KEY_MINUS, KEY_UNDERLINE,
            KEY_EQUAL, KEY_PLUS
    };

    public static final String KEY_LEFT_SQUARE_BRACKETS = "[";
    public static final String KEY_RIGHT_SQUARE_BRACKETS = "]";
    public static final String KEY_LEFT_BRACE = "{";
    public static final String KEY_RIGHT_BRACE = "}";
    public static final String KEY_RIGHT_SLASH = "\\";
    public static final String KEY_SEPARATOR = "|";
    public static final String KEY_COLON = ":";
    public static final String KEY_SEMICOLON = ";";
    public static final String KEY_QUOTATION_MARKS = "'";
    public static final String KEY_DOUBLE_QUOTATION_MARKS = "\"";
    public static final String KEY_COMMA = ",";
    public static final String KEY_PERIOD = ".";
    public static final String KEY_LEFT_ANGLE_BRACKET = "<";
    public static final String KEY_RIGHT_ANGLE_BRACKET = ">";
    public static final String KEY_LEFT_SLASH = "/";
    public static final String KEY_QUESTION_MARK = "?";

    public static final String[] symbol_group_2 = {
            KEY_LEFT_SQUARE_BRACKETS, KEY_RIGHT_SQUARE_BRACKETS,
            KEY_LEFT_BRACE, KEY_RIGHT_BRACE,
            KEY_RIGHT_SLASH, KEY_SEPARATOR,
            KEY_COLON, KEY_SEMICOLON,
            KEY_QUOTATION_MARKS, KEY_DOUBLE_QUOTATION_MARKS,
            KEY_COMMA, KEY_PERIOD,
            KEY_LEFT_ANGLE_BRACKET, KEY_RIGHT_ANGLE_BRACKET,
            KEY_LEFT_SLASH, KEY_QUESTION_MARK
    };

    public static final String KEY_ALT = "alt";
    public static final String KEY_ATL_LEFT = "altleft";
    public static final String KEY_ALT_RIGHT = "altright";
    public static final String KEY_BACKSPACE = "backspace";
    public static final String KEY_CAPS_LOCK = "capslock";
    public static final String KEY_CTRL = "ctrl";
    public static final String KEY_DELETE = "delete";
    public static final String KEY_DOWN = "down";
    public static final String KEY_END = "end";
    public static final String KEY_ENTER = "enter";
    public static final String KEY_ESC = "esc";
    public static final String KEY_FN = "fn";
    public static final String KEY_HOME = "home";
    public static final String KEY_INSERT = "insert";
    public static final String KEY_LEFT = "left";
    public static final String KEY_NUM_LOCK = "numlock";
    public static final String KEY_PAGE_DOWN = "pagedown";
    public static final String KEY_PAGE_UP = "page_up";
    public static final String KEY_PLAY_PAUSE = "playpause";
    public static final String KEY_PRT_SC = "prtsc";
    public static final String KEY_RIGHT = "right";
    public static final String KEY_SCROLL_LOCK = "scrolllock";
    public static final String KEY_SHIFT = "shift";
    public static final String KEY_SHIFT_LEFT = "shiftleft";
    public static final String KEY_SHIFT_RIGHT = "shiftright";
    public static final String KEY_SPACE = "space";
    public static final String KEY_TAB = "tab";
    public static final String KEY_UP = "up";
    public static final String KEY_VOLUME_DOWN = "volumedown";
    public static final String KEY_VOLUME_UP = "volumeup";
    public static final String KEY_WIN = "win";
    public static final String KEY_WIN_LEFT = "winleft";
    public static final String KEY_WIN_RIGHT = "winright";

    public static final String[] controls = {
            KEY_ALT, KEY_ATL_LEFT, KEY_ALT_RIGHT,
            KEY_BACKSPACE, KEY_CAPS_LOCK, KEY_CTRL,
            KEY_DELETE, KEY_DOWN, KEY_END,
            KEY_ENTER, KEY_ESC, KEY_FN,
            KEY_HOME, KEY_INSERT, KEY_LEFT,
            KEY_NUM_LOCK, KEY_PAGE_DOWN, KEY_PAGE_UP,
            KEY_PLAY_PAUSE, KEY_PRT_SC, KEY_RIGHT,
            KEY_SCROLL_LOCK, KEY_SHIFT, KEY_SHIFT_LEFT,
            KEY_SHIFT_RIGHT, KEY_SPACE, KEY_TAB,
            KEY_UP, KEY_VOLUME_DOWN, KEY_VOLUME_UP,
            KEY_WIN, KEY_WIN_LEFT, KEY_WIN_RIGHT
    };

    public static final String[][] defaultKeys = {
            letters, numbers, KEY_Fs,symbol_group_1, symbol_group_2, controls
    };

    public static String generateOrderListString(List<Order> orderList){
        String s = "";

        for(Order order:orderList){
            int type = order.getOrderType();
            String param = order.getOrderParam();
            String[] params = param.split(",");
            switch (type){
                case ORDER_TYPE_MOUSE_MOVE:
                    s = move(Float.parseFloat(params[0]),Float.parseFloat(params[1]),s);
                    break;
                case ORDER_TYPE_MOUSE_MOVE_ABS:
                    s = absMove(Float.parseFloat(params[0]),Float.parseFloat(params[1]),s);
                    break;
                case ORDER_TYPE_MOUSE_CLICK:
                    if(param.equals("right")){
                        s = Click(MOUSE_CLICK_RIGHT,s);
                    }else {
                        s = Click(MOUSE_CLICK_LEFT,s);
                    }
                    break;
                case ORDER_TYPE_MOUSE_HOLD:
                    if(param.equals("right")){
                        s = Click(MOUSE_PRESS_RIGHT,s);
                    }else {
                        s = Click(MOUSE_PRESS_LEFT,s);
                    }
                    break;
                case ORDER_TYPE_MOUSE_RELEASE:
                    if(param.equals("right")){
                        s = Click(MOUSE_RELEASE_RIGHT,s);
                    }else {
                        s = Click(MOUSE_RELEASE_LEFT,s);
                    }
                    break;
                case ORDER_TYPE_KEY:
                    s = key(KEY_CLICK,param,s);
                    break;
                case ORDER_TYPE_KEY_DOWN:
                    s = key(KEY_PRESS,param,s);
                    break;
                case ORDER_TYPE_KEY_UP:
                    s = key(KEY_RELEASE,param,s);
                    break;
                case ORDER_TYPE_WHEEL:
                    s = wheel(Integer.parseInt(param),s);
                    break;
                case ORDER_TYPE_STRING:
                    s = sendString(param,s);
                    break;
                case ORDER_TYPE_DELAY:
                    s = delay(Integer.parseInt(param),s);
                    break;
            }
        }
        return s;
    }

//    public static String getOrderString(Order order){
//        int type = order.getOrderType();
//        String param = order.getOrderParam();
//        String s = "";
//        switch (type){
//            case ORDER_TYPE_MOUSE_CLICK:
//                if(param.equals("right")){
//                    s = Click(MOUSE_CLICK_RIGHT);
//                }else {
//                    s = Click(MOUSE_CLICK_LEFT);
//                }
//                break;
//            case ORDER_TYPE_MOUSE_HOLD:
//                if(param.equals("right")){
//                    s = Click(MOUSE_PRESS_RIGHT);
//                }else {
//                    s = Click(MOUSE_PRESS_LEFT);
//                }
//                break;
//            case ORDER_TYPE_MOUSE_RELEASE:
//                if(param.equals("right")){
//                    s = Click(MOUSE_RELEASE_RIGHT);
//                }else {
//                    s = Click(MOUSE_RELEASE_LEFT);
//                }
//                break;
//            case ORDER_TYPE_KEY:
//                s = key(KEY_CLICK,param);
//                break;
//            case ORDER_TYPE_KEY_DOWN:
//                s = key(KEY_PRESS,param);
//                break;
//            case ORDER_TYPE_KEY_UP:
//                s = key(KEY_RELEASE,param);
//                break;
//            case ORDER_TYPE_WHEEL:
//                s = wheel(Integer.parseInt(param));
//                break;
//            case ORDER_TYPE_STRING:
//                s = sendString(param);
//                break;
//            case ORDER_TYPE_DELAY:
//                s = delay(Integer.parseInt(param));
//                break;
//        }
//        return s;
//    }

    public static String Click(int flag) {
        return Click(flag, "");
    }

    public static String Click(int flag, String content) {

        //若content不为空，则增加'~'连接符
        if (!content.equals("")) {
            content += "~";
        }

        //鼠标点击，根据flag增加不同的点击事件
        //1表示左键单击
        //2表示左键按下
        //3表示左键松开
        //4表示左键双击
        //以上加负号表示右键
        switch (Math.abs(flag)) {
            case 1:
                content += "mouse";
                break;
            case 2:
                content += "mouseHold";
                break;
            case 3:
                content += "mouseRelease";
                break;
            case 4:
                content += "mouseDoubleClick";
                break;
            //为了稳定，接收到其他参数一律认为是点击
            default:
                content += "mouse";
        }
        //大于零则为左键，否则右键
        if (flag >= 0) {
            content += "~left";
        } else {
            content += "~right";
        }


        return content;
    }

    //鼠标相对移动
    public static String move(float x,float y) {
        return move(x, y, "");
    }

    public static String move(float x, float y, String content) {
        //若content不为空，则增加'~'连接符
        if (!content.equals("")) {
            content += "~";
        }

        content += "mouse@~" + x + "," + y;

        return content;
    }

    //鼠标绝对移动量
    public static String absMove(float x, float y) {
        return absMove(x, y, "");
    }

    public static String absMove(float x, float y, String content) {
        if (!content.equals("")) {
            content += "~";
        }

        content += "mouse~" + x + "," + y;

        return content;
    }

    //滚轮
    //flag即为滚动的距离
    public static String wheel(int flag) {
        return wheel(flag, "");
    }

    public static String wheel(int flag, String content) {
        if (!content.equals("")) {
            content += "~";
        }

        content += "wheel~"+flag;

        return content;
    }

    public static String delay(int num) {
        return delay(num, "");
    }

    public static String delay(int num, String content) {
        if (!content.equals("")) {
            content += "~";
        }

        content += "delay~" + num;

        return content;
    }

    //按键，flag表示具体操作,keyName即为传输的按键名
    public static String key(int flag, String keyName) {
        return key(flag, keyName, "");
    }

    public static String key(int flag, String keyName, String content) {
        if (!content.equals("")) {
            content += "~";
        }

        //0表示按下并松开
        //1表示按下
        //-1表示松开
        switch (flag) {
            case 0:
                content += "key~" + keyName;
                break;
            case 1:
                content += "keyDown~" + keyName;
                break;
            case -1:
                content += "keyUp~" + keyName;
                break;
            default:
                content += "key~" + keyName;
                break;
        }

        return content;
    }

    public static String sendString(String s) {
        return sendString(s, "");
    }

    public static String sendString(String s, String content) {
        if (!content.equals("")) {
            content += "~";
        }

        //左斜杠转义为双斜杠
        Pattern pattern = Pattern.compile("/");
        Matcher matcher = pattern.matcher(s);
        s = matcher.replaceAll("//");

        //~转义为/s
        pattern = Pattern.compile("~");
        matcher = pattern.matcher(s);
        s = matcher.replaceAll("/s");

        String[] strings = s.split("\n");

        //换行符转义成按键回车
        if (strings.length > 1) {
            //转义时如果遇到连续的换行符，会分割出空字符串，则会出现通信出错，所以必须进行处理
            //即当出现空字符串时，不添加"string~"直接跳过
            //
            //具体分三部分处理
            //
            //数组第一项
            //  若第一项为空，添加"key~enter"
            //  否则添加"string~"+第一串字符串,和"~key~enter"
            //  即第一条指令前不加"~"
            //数组最后一项
            //  若最后一项不为空则添加"string~"+最后一串字符串
            //其他
            //  类似第一项的处理，不过所有指令前面都需添加"~"
            if (strings[0].equals("")) {
                content += "key~" + KEY_ENTER;
            } else {
                content += "string~" + strings[0];
                content += "~key~" + KEY_ENTER;
            }
            for (int i = 1; i < (strings.length - 1); i++) {
                if (strings[i].equals("")) {
                    content += "~key~" + KEY_ENTER;
                } else {
                    content += "~string~" + strings[i];
                    content += "~key~" + KEY_ENTER;
                }
            }
            if (!strings[strings.length - 1].equals("")) {
                content += "~string~" + strings[strings.length - 1];
            }

        } else {
            //若没有换行符，直接添加
            content += "string~" + s;
        }

        return content;
    }
}
