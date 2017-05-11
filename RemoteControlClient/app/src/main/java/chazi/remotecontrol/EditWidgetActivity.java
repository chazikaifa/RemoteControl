package chazi.remotecontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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

    private int index;
    private EditText name_et,content_et;
    private ListView searchListView;
    private List<Widget> defaultWidgetList = new ArrayList<>();
    private List<Widget> searchList = new ArrayList<>();
    private SearchAdapter adapter;
    private String word,name,content;
    private Button btn_confirm,btn_cancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RealmDb.initRealm(getApplicationContext());

        index = getIntent().getExtras().getInt("index");

        setContentView(R.layout.activity_edit_widget);

        defaultWidgetList = RealmDb.getWidgetsByPanelId("0");

        name_et = (EditText) findViewById(R.id.widget_name);
        content_et = (EditText) findViewById(R.id.btn_content);

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
//                            adapter = new SearchAdapter(getApplicationContext(),R.layout.item_widget_search,searchList);
//                            searchListView.setAdapter(adapter);
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

        btn_confirm = (Button) findViewById(R.id.btn_confirm);
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
                setResult(0,intent);
                finish();
            }
        });

        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("index",index);
                setResult(1,intent);
                finish();
            }
        });
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
        setResult(1,intent);
        finish();
    }
}
