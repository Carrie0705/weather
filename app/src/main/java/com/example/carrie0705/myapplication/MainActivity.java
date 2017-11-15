package com.example.carrie0705.myapplication;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carrie0705.bean.TodayWeather;
import com.example.carrie0705.util.NetUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity implements View.OnClickListener{
    private static final int UPDATE_TODAY_WEATHER = 1;
    private ImageView mUpdateBtn;
    private ImageView mCitySelect;

    private TextView cityTv,timeTv,humidityTv,weekTv,pmDataTv,pmQualityTv,
            temperatureTv,climateTv,windTv,city_name_Tv,wendu_Tv;
    private ImageView weatherImp,pmImg;

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
            switch ((msg.what)){
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mUpdateBtn = (ImageView) findViewById(R.id.refresh);
        mUpdateBtn.setOnClickListener(this);

        if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){
            Log.d("myWeather","网络畅通");
            Toast.makeText(MainActivity.this,"网络畅通！",Toast.LENGTH_LONG).show();

        }else {
            Log.d("myWeather","网络堵塞，请检查网络连接是否完好！");
            Toast.makeText(MainActivity.this,"网络堵塞，请检查网络连接是否完好！",Toast.LENGTH_LONG).show();
        }

        mCitySelect = (ImageView) findViewById(R.id.titlecity);
        mCitySelect.setOnClickListener(this);
        initView();
    }
//刚进入应用程序的时候,对页面中的元素进行初始化,值都赋为N/A
    void initView(){
        city_name_Tv=(TextView) findViewById((R.id.title_city_name));
        cityTv = (TextView) findViewById(R.id.city_name);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.PM_name);
        pmQualityTv = (TextView) findViewById(R.id.PM_quantity);
        pmImg =(ImageView) findViewById(R.id.PM_imag);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImp = (ImageView) findViewById(R.id.weather_image);
        wendu_Tv = (TextView) findViewById(R.id.wendu);

        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
        wendu_Tv.setText("N/A");

    }


    public void onClick(View view){
        //单击返回键
        if(view.getId() == R.id.titlecity){
            Intent i = new Intent(this,SelectCity.class);
            i.putExtra("cityName","北京");
            //startActivity(i);
            startActivityForResult(i,1);
        }
        //单击刷新键
        if(view.getId() == R.id.refresh){
            SharedPreferences sharedPreferences = getSharedPreferences("congig",MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code","101010100");
            //北京 101010100   兰州 101160101
            Log.d("myWeather",cityCode);
            if(NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE){
                Log.d("myWeather","网络畅通");
                queryWeatherCode(cityCode);
            }else{
                Log.d("myWeather","网络堵塞，请检查网络连接是否完好！");
                Toast.makeText(MainActivity.this,"网络堵塞，请检查网络连接是否完好！",Toast.LENGTH_LONG).show();
            }
        }
    }
//判断网络是否连接
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        if(requestCode == 1 && resultCode == RESULT_OK){
            String newCityCode = data.getStringExtra("cityCode");
            Log.d("myWeather","选择的城市代码为"+newCityCode);

            if(NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE){
                Log.d("myWeather","网络畅通");
                queryWeatherCode(newCityCode);
            }else{
                Log.d("myWeather","网络堵塞，请检查网络连接是否完好！");
                Toast.makeText(MainActivity.this,"网络堵塞，请检查网络连接是否完好！",Toast.LENGTH_LONG).show();
            }
        }
    }
    //创建一个新的线程,用于获取接口中的信息并调用函数进行解析
    private void queryWeatherCode(String citycode){
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey="+citycode;
        Log.d("myWeather",address);
        new Thread(new Runnable(){
            public void run(){
                //Toast.makeText(MainActivity.this,"run",Toast.LENGTH_LONG).show();
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str=reader.readLine())!=null){
                        response.append(str);
                        Log.d("myWeather",str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather",responseStr);
                    todayWeather = parseXML(responseStr);
                    if(todayWeather != null){
                        Log.d("myWeather",todayWeather.toString());

                        Message msg =new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj=todayWeather;
                        mHandler.sendMessage(msg);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(con!=null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }
//解析xml
    private TodayWeather parseXML(String xmldata){
        TodayWeather todayWeather = null;
        int fengxiangCount=0;
        int fengliCount =0;
        int dateCount=0;
        int highCount =0;
        int lowCount=0;
        int typeCount =0;
        try{
            XmlPullParserFactory fac =XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather","parseXML");
            while(eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    //判断当前时间是否为文档开始事件
                    case  XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp")){
                            todayWeather = new TodayWeather();
                        }
                        if(todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                        break;
                    //判断当前事件是否为标签元素结束事件
                    case  XmlPullParser.END_TAG:
                        break;
                }
                //进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }
        }catch (XmlPullParserException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return todayWeather;
    }

//将解析到的数据,显示在页面中
    void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+"发布");
        humidityTv.setText("湿度:"+todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:"+todayWeather.getFengli());
        wendu_Tv.setText("温度:"+todayWeather.getWendu()+"℃");

        if(todayWeather.getPm25()== null){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
        }else {
            int pm = Integer.parseInt(todayWeather.getPm25());
            if (pm <= 50 && pm >= 0) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
            } else if (pm > 50 && pm <= 100) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
            } else if (pm > 100 && pm <= 150) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
            } else if (pm > 150 && pm <= 200) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
            } else if (pm > 200 && pm <= 300) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
            } else {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
            }
        }

        switch(todayWeather.getType()){
            case "晴":
                weatherImp.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "暴雪":
                weatherImp.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                weatherImp.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                weatherImp.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                weatherImp.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "大雨":
                weatherImp.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "多云":
                weatherImp.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                weatherImp.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                weatherImp.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "沙尘暴":
                weatherImp.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                weatherImp.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case  "雾":
                weatherImp.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "小雪":
                weatherImp.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "小雨":
                weatherImp.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "阴":
                weatherImp.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "雨夹雪":
                weatherImp.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雪":
                weatherImp.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "阵雨":
                weatherImp.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "中雪":
                weatherImp.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "中雨":
                weatherImp.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
        }

        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
    }



}
