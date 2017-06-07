package chazi.remotecontrol;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chazi.remotecontrol.db.RealmDb;
import chazi.remotecontrol.entity.Widget;
import chazi.remotecontrol.utils.DensityUtil;

/**
 * Created by 595056078 on 2017/5/3.
 */

public class EditWidgetActivity extends Activity implements AdapterView.OnItemClickListener {

    public static final int STATUS_OK = 0;
    public static final int STATUS_CANCEL = 1;
    public static final int STATUS_DELETE = 2;

    private int index;

    private RelativeLayout button_rl, sen_rl, rocker_rl, input_rl;

    private EditText name_et, content_et, sen_et;
    private RadioButton[] keyType_rb = new RadioButton[3];
    private ListView searchListView;
    private List<Widget> defaultWidgetList = new ArrayList<>();
    private List<Widget> searchList = new ArrayList<>();
    private List<String> orderList = new ArrayList<>();
    private SearchAdapter adapter;
    private OrderListAdapter orderAdapter;
    private String word, name, content;
    private int type;
    private TextView btn_confirm;
    private ImageView btn_back, btn_delete, btn_add_order;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RealmDb.initRealm(getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        index = bundle.getInt("index");
        name = bundle.getString("name");
        content = bundle.getString("content");
        type = bundle.getInt("type");

        setContentView(R.layout.activity_edit_widget);

        button_rl = (RelativeLayout) findViewById(R.id.button_rl);
        sen_rl = (RelativeLayout) findViewById(R.id.sen_rl);
        rocker_rl = (RelativeLayout) findViewById(R.id.rocker_rl);
        input_rl = (RelativeLayout) findViewById(R.id.input_rl);

        defaultWidgetList = RealmDb.getWidgetsByPanelId("0");

        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_back.setOnTouchListener(new MyOnTouchListener(EditWidgetActivity.this, R.drawable.back_normal, R.drawable.back_selected));

        btn_delete = (ImageView) findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(EditWidgetActivity.this)
                        .setTitle("删除该控件")
                        .setMessage("确定要删除该控件吗？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.putExtra("index", index);
                                setResult(STATUS_DELETE, intent);
                                EditWidgetActivity.this.finish();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

            }
        });
        btn_delete.setOnTouchListener(new MyOnTouchListener(EditWidgetActivity.this, R.drawable.delete_normal, R.drawable.delete_selected));

        //根据控件不同展示不同的界面
        switch (type) {
            case 1:
            case 2:
                SetButton();
                break;
            case 6:
                SetGroupButton();
                break;
            case 3:
            case 4:
                SetSen();
                break;
            case 5:
                SetInput();
                break;
            case 7:
                SetRocker();
                break;
        }
    }

    private void SetGroupButton() {
        button_rl.setVisibility(View.VISIBLE);

        btn_add_order = (ImageView) findViewById(R.id.btn_add_order);
        btn_add_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = content_et.getText().toString();
                orderAdapter.add(s);
                orderAdapter.notifyDataSetChanged();
            }
        });
        btn_add_order.setOnTouchListener(new MyOnTouchListener(getApplicationContext(), R.drawable.plus, R.drawable.plus_press, R.color.transparent, R.color.gray));

        name_et = (EditText) findViewById(R.id.widget_name);
        name_et.setText(name);
        content_et = (EditText) findViewById(R.id.btn_content);
        content_et.setHint("新增命令");
        content_et.setPadding(DensityUtil.dip2px(getApplicationContext(), 10), DensityUtil.dip2px(getApplicationContext(), 10), DensityUtil.dip2px(getApplicationContext(), 60), DensityUtil.dip2px(getApplicationContext(), 10));

        final String[] orders = content.split("~");
        int n;
        if (orders.length % 2 == 0) {
            n = orders.length / 2;
            for (int i = 0; i < n; i++) {
                orderList.add(orders[2 * i] + "~" + orders[2 * i + 1]);
            }
        } else {
            n = (orders.length - 1) / 2;
            for (int i = 0; i < n; i++) {
                orderList.add(orders[2 * i] + "~" + orders[2 * i + 1]);
            }
            if (!orders[2 * n].equals("")) {
                orderList.add(orders[2 * n]);
            }
        }

        searchListView = (ListView) findViewById(R.id.search_list);
        orderAdapter = new OrderListAdapter(getApplicationContext(), R.layout.item_order, orderList);
        searchListView.setAdapter(orderAdapter);

        btn_confirm = (TextView) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = name_et.getText().toString();

                if(!orderList.isEmpty()) {
                    content = orderList.get(0);
                    for (int i = 1; i < orderList.size(); i++) {
                        content += "~"+orderList.get(i);
                    }
                }else {
                    content = "";
                }

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("content", content);

                Log.i("onEdit", "name = " + name + " content = " + content);

                bundle.putInt("index", index);
                intent.putExtras(bundle);
                setResult(STATUS_OK, intent);
                finish();
            }
        });
        btn_confirm.setOnTouchListener(new MyOnTouchListener(EditWidgetActivity.this));
    }

    private void SetButton() {
        button_rl.setVisibility(View.VISIBLE);

        btn_add_order = (ImageView) findViewById(R.id.btn_add_order);
        btn_add_order.setVisibility(View.GONE);

        name_et = (EditText) findViewById(R.id.widget_name);
        name_et.setText(name);
        content_et = (EditText) findViewById(R.id.btn_content);
        content_et.setText(content);

        content_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                word = s.toString();
                adapter.setWord(word);

                try {

//                    if (word.equals("")) {
//                        searchListView.setVisibility(View.INVISIBLE);
//                        searchList.clear();
//                    } else {
                    Pattern pattern = Pattern.compile(".*" + word + ".*");
                    Matcher matcher;

                    searchList.clear();
                    for (Widget widget : defaultWidgetList) {
                        matcher = pattern.matcher(widget.getName());
                        if (matcher.matches()) {
                            searchList.add(widget);
                        }
                    }

                    Log.i("TextChange", searchList.size() + "");

                    searchListView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
//                    }
                } catch (Exception e) {
                    Log.i("search", e.toString());
                }
            }
        });

        searchListView = (ListView) findViewById(R.id.search_list);

        adapter = new SearchAdapter(getApplicationContext(), R.layout.item_widget_search, searchList);
        searchListView.setAdapter(adapter);
        searchListView.setOnItemClickListener(this);

        btn_confirm = (TextView) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = name_et.getText().toString();
                content = content_et.getText().toString();

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("content", content);

                Log.i("onEdit", "name = " + name + " content = " + content);

                bundle.putInt("index", index);
                intent.putExtras(bundle);
                setResult(STATUS_OK, intent);
                finish();
            }
        });
        btn_confirm.setOnTouchListener(new MyOnTouchListener(EditWidgetActivity.this));

    }

    private void SetSen() {
        sen_rl.setVisibility(View.VISIBLE);

        sen_et = (EditText) findViewById(R.id.sen_et);

        sen_et.setHint("请输入大于0的整数");
        sen_et.setText(content);

        btn_confirm = (TextView) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content = sen_et.getText().toString();

                try {
                    int sen = Integer.valueOf(content);

                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("name", name);
                    bundle.putString("content", content);

                    Log.i("onEdit", "name = " + name + " content = " + content);

                    bundle.putInt("index", index);
                    intent.putExtras(bundle);
                    setResult(STATUS_OK, intent);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(EditWidgetActivity.this, "格式错误！", Toast.LENGTH_SHORT).show();
                }

            }
        });
        btn_confirm.setOnTouchListener(new MyOnTouchListener(EditWidgetActivity.this));
    }

    private void SetInput() {
        input_rl.setVisibility(View.VISIBLE);
    }

    private void SetRocker() {
        rocker_rl.setVisibility(View.VISIBLE);

        keyType_rb[0] = (RadioButton) findViewById(R.id.rocker_type_1);
        keyType_rb[1] = (RadioButton) findViewById(R.id.rocker_type_2);
        keyType_rb[2] = (RadioButton) findViewById(R.id.rocker_type_3);

        int keyType = Integer.parseInt(content);
        if (keyType < 0 || keyType > 2) {
            keyType = 0;
        }
        keyType_rb[keyType].setChecked(true);

        btn_confirm = (TextView) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 3; i++) {
                    if (keyType_rb[i].isChecked()) {
                        content = i + "";
                        break;
                    }
                }

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("content", content);

                Log.i("onEdit", "name = " + name + " content = " + content);

                bundle.putInt("index", index);
                intent.putExtras(bundle);
                setResult(STATUS_OK, intent);
                finish();

            }
        });
        btn_confirm.setOnTouchListener(new MyOnTouchListener(EditWidgetActivity.this));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        content_et.setText(searchList.get(position).getContent());
        searchListView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("index", index);
        setResult(STATUS_CANCEL, intent);
        finish();
    }
}
