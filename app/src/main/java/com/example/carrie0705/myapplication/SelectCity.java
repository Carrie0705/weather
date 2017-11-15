package com.example.carrie0705.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carrie0705.app.MyApplication;
import com.example.carrie0705.bean.City;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Carrie0705 on 2017/10/18.
 */

public class SelectCity extends Activity implements View.OnClickListener{
    private ImageView mBackBtn;
    private ListView mlistView;
    private String[] data={"第1组","第2组","第3组","第4组","第5组","第6组",
            "第7组","第8组","第9组","第10组","第11组","第12组","第13组",
            "第14组","第15组","第16组","第17组","第18组","第19组","第20组",
            "第21组","第22组"};

    private List<City> mCityList;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        initViews();
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        Intent intent = this.getIntent();
        String citycode = intent.getStringExtra("cityCode");

        mTextView.setText("当前城市:北京");
    }

//单击返回按钮
    public void onClick(View v){
        switch(v.getId()){
            case R.id.title_back:
                Intent i = new Intent();
                i.putExtra("cityCode","101070901");//101020100 上海  101070901 阜新
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
        String[] data= new String[5000];
        int i=0;
        for(City city : mCityList){
            data[i] = city.getCity();
            i++;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,data);
        mlistView.setAdapter(adapter);

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

}
