package com.example.carrie0705.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carrie0705.app.MyApplication;
import com.example.carrie0705.bean.City;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carrie0705 on 2017/10/18.
 */

public class SelectCity extends Activity implements View.OnClickListener{
    private static final String TAG = "Filter";
    private ImageView mBackBtn;
    private ListView mlistView;
    private EditText mClearEditText;

    private List<City> mCityList;
    private TextView mTextView;
    private String[] data= new String[5000];
    private String[] data1= new String[5000];

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        mClearEditText = (EditText) findViewById(R.id.search_edit);

        initViews();
        initSearch();
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);


        //Intent intent = this.getIntent();
        //String citycode = intent.getStringExtra("cityCode");

        //mTextView.setText("当前城市:北京");
    }

//单击返回按钮
    public void onClick(View v){
        switch(v.getId()){
            case R.id.title_back:
                SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
                String cityCode = sharedPreferences.getString("main_city_code","101010100");
                Intent i = new Intent();
                i.putExtra("cityCode",cityCode);//101020100 上海  101070901 阜新
                setResult(RESULT_OK,i);
                finish();
                break;
            default:
                break;
        }
    }
//listview列表显示以及单击返回
    public void initViews(){
        mlistView = (ListView)findViewById(R.id.select_city);

        MyApplication myApplication = (MyApplication) getApplication();
        mCityList = myApplication.getCityList();
        int i=0;
        for(City city : mCityList){
            data[i] = city.getCity();
            i++;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,data);
        mlistView.setAdapter(adapter);
        Log.d("myWeather","adapter");
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?>adapterView,View view,int i,long I){
                Intent intent = new Intent();
                City city = mCityList.get(i);
                Log.d("myWeather",city.getNumber());
                intent.putExtra("cityCode",city.getNumber());
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    private void filterData(String filterStr){
        boolean flag = false;
        Log.d("Filter",filterStr);
        int i=0;
        if(TextUtils.isEmpty(filterStr)){
            Log.d("Filter","empty");
            for(City city : mCityList){
                data1[i] = city.getCity();
                i++;
            }
        }else{
            i=0;
            for(City city : mCityList){
                if(city.getCity().indexOf(filterStr.toString()) != -1){
                    Log.d("Filter","not empty");
                    data1[i] = city.getCity();
                    Log.d("Filter",data1[i]);
                    flag = true;
                    i++;
                }
            }
        }
        if(flag == false){
            data1=data;
            Toast.makeText(SelectCity.this, "未查询到所输城市", Toast.LENGTH_LONG).show();
        }else{
            for(int j=i;j<data1.length;j++) {
                data1[j] = " ";
            }
        }
    }
    public void initSearch(){
        mClearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(s.toString());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,data1);
                mlistView.setAdapter(adapter);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long I) {
                    Intent intent = new Intent();
                    for (int j = 0; j < data.length; j++) {
                        if (data[j] == data1[i]) {
                            i = j;
                            break;
                        }
                    }
                    City city = mCityList.get(i);
                    Log.d("myWeather", city.getNumber());
                    intent.putExtra("cityCode", city.getNumber());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
    }
}
