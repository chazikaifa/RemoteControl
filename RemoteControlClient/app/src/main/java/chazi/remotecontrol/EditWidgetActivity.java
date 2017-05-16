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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chazi.remotecontrol.db.RealmDb;
import chazi.remotecontrol.entity.Widget;

/**
 * Created by 595056078 on 2017/5/3.
 */

public class EditWidgetActivity extends Activity implements AdapterView.OnItemClickListener{

    public static final int STATUS_OK = 0;
    public static final int STATUS_CANCEL = 1;
    public static final int STATUS_DELETE = 2;

    private int index;
    private EditText name_et,content_et;
    private ListView searchListView;
    private List<Widget> defaultWidgetList = new ArrayList<>();
    private List<Widget> searchList = new ArrayList<>();
    private SearchAdapter adapter;
    private String word,name,content;
    private TextView btn_confirm;
    private ImageView btn_back,btn_delete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RealmDb.initRealm(getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        index = bundle.getInt("index");
        name = bundle.getString("name");
        content = bundle.getString("content");

        setContentView(R.layout.activity_edit_widget);

        defaultWidgetList = RealmDb.getWidgetsByPanelId("0");

        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("index",index);
                setResult(STATUS_CANCEL,intent);
                finish();
            }
        });
        btn_back.setOnTouchListener(new MyOnTouchListener(EditWidgetActivity.this,R.drawable.back_normal,R.drawable.back_selected));

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
                                intent.putExtra("index",index);
                                setResult(STATUS_DELETE,intent);
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
        btn_delete.setOnTouchListener(new MyOnTouchListener(EditWidgetActivity.this,R.drawable.delete_normal,R.drawable.delete_selected));

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

                    if (word.equals("")) {
                        searchListView.setVisibility(View.GONE);
                        searchList.clear();
                    } else {
                        Pattern pattern = Pattern.compile(".*" + word + ".*");
                        Matcher matcher;

                        searchList.clear();
                        for (Widget widget : defaultWidgetList) {
                            matcher = pattern.matcher(widget.getName());
                            if (matcher.matches()) {
                                searchList.add(widget);
                            }
                        }

                        Log.i("TextChange",searchList.size()+"");

                        if (searchList.size() <= 0) {
                            searchListView.setVisibility(View.GONE);
                        } else {
                            searchListView.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }catch (Exception e){
                    Log.i("search",e.toString());
                }
            }
        });

        searchListView = (ListView) findViewById(R.id.search_list);

        adapter = new SearchAdapter(getApplicationContext(),R.layout.item_widget_search,searchList);
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
                bundle.putString("name",name);
                bundle.putString("content",content);

                Log.i("onEdit","name = "+name + " content = "+content);

                bundle.putInt("index",index);
                intent.putExtras(bundle);
                setResult(STATUS_OK,intent);
                finish();
            }
        });
        btn_confirm.setOnTouchListener(new MyOnTouchListener(EditWidgetActivity.this));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        content_et.setText(searchList.get(position).getContent());
        searchListView.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("index",index);
        setResult(STATUS_CANCEL,intent);
        finish();
    }
}
