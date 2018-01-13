package com.example.carrie0705.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener,ViewPager.OnPageChangeListener{
    private static final int UPDATE_TODAY_WEATHER = 1;
    private ImageView mUpdateBtn;
    private ImageView mCitySelect;

    private TextView cityTv,timeTv,humidityTv,weekTv,pmDataTv,pmQualityTv,
            temperatureTv,climateTv,windTv,city_name_Tv,wendu_Tv;
    private TextView date1_Tv,date2_Tv,date3_Tv,date4_Tv,fengli1_Tv,fengli2_Tv,fengli3_Tv,
            fengli4_Tv,wendu1_Tv,wendu2_Tv,wendu3_Tv,wendu4_Tv,
            type1_Tv,type2_Tv,type3_Tv,type4_Tv;
    private ImageView weatherImp,pmImg;
    private ImageView weather1Imp,weather2Imp,weather3Imp,weather4Imp;

    private ViewPagerAdapter vpAdapter;
    private ViewPager vp;
    private List<View> views = new ArrayList<View>();

    private ImageView[] dots;
    private int[] ids={R.id.iv1,R.id.iv2};

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

        LayoutInflater inflater = LayoutInflater.from(this);
        views.add(inflater.inflate(R.layout.page1,null));
        views.add(inflater.inflate(R.layout.page2,null));
        vpAdapter = new ViewPagerAdapter(views,this);
        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);
        vp.addOnPageChangeListener(this);

        initView();
        initDots();
    }
    void initDots(){
        dots = new ImageView[views.size()];
        for(int i=0;i<views.size();i++){
            dots[i]=(ImageView)findViewById(ids[i]);
        }
    }
//刚进入应用程序的时候,对页面中的元素进行初始化
    private void initView(){
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

        date1_Tv = (TextView) views.get(0).findViewById(R.id.day1);
        fengli1_Tv = (TextView) views.get(0).findViewById(R.id.day1_feng);
        wendu1_Tv  = (TextView) views.get(0).findViewById(R.id.day1_wendu);
        type1_Tv = (TextView) views.get(0).findViewById(R.id.day1_weather);
        weather1Imp = (ImageView) views.get(0).findViewById(R.id.day1_image);

        date2_Tv = (TextView) views.get(0).findViewById(R.id.day2);
        fengli2_Tv = (TextView) views.get(0).findViewById(R.id.day2_feng);
        wendu2_Tv  = (TextView) views.get(0).findViewById(R.id.day2_wendu);
        type2_Tv = (TextView) views.get(0).findViewById(R.id.day2_weather);
        weather2Imp = (ImageView) views.get(0).findViewById(R.id.day2_image);

        date3_Tv = (TextView) views.get(1).findViewById(R.id.day3);
        fengli3_Tv = (TextView) views.get(1).findViewById(R.id.day3_feng);
        wendu3_Tv  = (TextView) views.get(1).findViewById(R.id.day3_wendu);
        type3_Tv = (TextView) views.get(1).findViewById(R.id.day3_weather);
        weather3Imp = (ImageView) views.get(1).findViewById(R.id.day3_image);

        date4_Tv = (TextView) views.get(1).findViewById(R.id.day4);
        fengli4_Tv = (TextView) views.get(1).findViewById(R.id.day4_feng);
        wendu4_Tv  = (TextView) views.get(1).findViewById(R.id.day4_wendu);
        type4_Tv = (TextView) views.get(1).findViewById(R.id.day4_weather);
        weather4Imp = (ImageView) views.get(1).findViewById(R.id.day4_image);


        SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
        String cityCode = sharedPreferences.getString("main_city_code","101010100");
        queryWeatherCode(cityCode);
        /*city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
        wendu_Tv.setText("N/A");*/
    }

    public void onClick(View view){
        SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
        String cityCode = sharedPreferences.getString("main_city_code","101010100");
        String cityName = sharedPreferences.getString("cityName","当前无城市");
        //单击选择城市
        if(view.getId() == R.id.titlecity){
            Intent i = new Intent(this,SelectCity.class);
            i.putExtra("cityName",cityName);
            //startActivity(i);
            startActivityForResult(i,1);
        }
        //单击刷新键
        if(view.getId() == R.id.refresh){
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
        if(view.getId() == R.id.share){
            Log.d("share","share");
        }
        if(view.getId() == R.id.address){
            Log.d("share","address");
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
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli1(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli2(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli3(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 4) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli4(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if(xmlPullParser.getName().equals("date") && dateCount == 1){
                                eventType = xmlPullParser.next();
                                todayWeather.setNextDay1(xmlPullParser.getText());
                                dateCount++;
                            } else if(xmlPullParser.getName().equals("date") && dateCount == 2){
                                eventType = xmlPullParser.next();
                                todayWeather.setNextDay2(xmlPullParser.getText());
                                dateCount++;
                            } else if(xmlPullParser.getName().equals("date") && dateCount == 3){
                                eventType = xmlPullParser.next();
                                todayWeather.setNextDay3(xmlPullParser.getText());
                                dateCount++;
                            } else if(xmlPullParser.getName().equals("date") && dateCount == 4){
                                eventType = xmlPullParser.next();
                                todayWeather.setNextDay4(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh1(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh2(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh3(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 4) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh4(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow1(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow2(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow3(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 4) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow4(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType1(xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType2(xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType3(xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 4) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType4(xmlPullParser.getText());
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

        SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cityName",todayWeather.getCity());
        editor.commit();

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


        date1_Tv.setText(todayWeather.getNextDay1());
        date2_Tv.setText(todayWeather.getNextDay2());
        date3_Tv.setText(todayWeather.getNextDay3());
        date4_Tv.setText(todayWeather.getNextDay4());

        fengli1_Tv.setText(todayWeather.getFengli1());
        fengli2_Tv.setText(todayWeather.getFengli2());
        fengli3_Tv.setText(todayWeather.getFengli3());
        fengli4_Tv.setText(todayWeather.getFengli4());

        wendu1_Tv.setText(todayWeather.getHigh1()+"~"+todayWeather.getLow1());
        wendu2_Tv.setText(todayWeather.getHigh1()+"~"+todayWeather.getLow2());
        wendu3_Tv.setText(todayWeather.getHigh1()+"~"+todayWeather.getLow3());
        wendu4_Tv.setText(todayWeather.getHigh1()+"~"+todayWeather.getLow4());

        type1_Tv.setText(todayWeather.getType1());
        type2_Tv.setText(todayWeather.getType2());
        type3_Tv.setText(todayWeather.getType3());
        type4_Tv.setText(todayWeather.getType4());

        getPic(todayWeather.getType(),weatherImp);
        getPic(todayWeather.getType1(),weather1Imp);
        getPic(todayWeather.getType2(),weather2Imp);
        getPic(todayWeather.getType3(),weather3Imp);
        getPic(todayWeather.getType4(),weather4Imp);

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



        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
    }
    public void getPic(String weather,ImageView Imp){
        if(Imp == weatherImp) {
            switch (weather) {
                case "晴":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_qing);
                    break;
                case "暴雪":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                    break;
                case "暴雨":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                    break;
                case "大暴雨":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                    break;
                case "大雪":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_daxue);
                    break;
                case "大雨":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_dayu);
                    break;
                case "多云":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                    break;
                case "雷阵雨":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                    break;
                case "雷阵雨冰雹":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                    break;
                case "沙尘暴":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                    break;
                case "特大暴雨":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                    break;
                case "雾":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_wu);
                    break;
                case "小雪":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                    break;
                case "小雨":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                    break;
                case "阴":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_yin);
                    break;
                case "雨夹雪":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                    break;
                case "阵雪":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                    break;
                case "阵雨":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                    break;
                case "中雪":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                    break;
                case "中雨":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                    break;
            }
        }else{
            switch (weather) {
                case "晴":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_qingx);
                    break;
                case "暴雪":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_baoxuex);
                    break;
                case "暴雨":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_baoyux);
                    break;
                case "大暴雨":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_dabaoyux);
                    break;
                case "大雪":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_daxuex);
                    break;
                case "大雨":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_dayux);
                    break;
                case "多云":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_duoyunx);
                    break;
                case "雷阵雨":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_leizhenyuxx);
                    break;
                case "雷阵雨冰雹":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbaox);
                    break;
                case "沙尘暴":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_shachenbaox);
                    break;
                case "特大暴雨":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_tedabaoyux);
                    break;
                case "雾":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_wux);
                    break;
                case "小雪":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_xiaoxuex);
                    break;
                case "小雨":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_xiaoyux);
                    break;
                case "阴":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_yinx);
                    break;
                case "雨夹雪":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_yujiaxuex);
                    break;
                case "阵雪":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_zhenxuex);
                    break;
                case "阵雨":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_zhenyux);
                    break;
                case "中雪":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_zhongxuex);
                    break;
                case "中雨":
                    Imp.setImageResource(R.drawable.biz_plugin_weather_zhongyux);
                    break;
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for(int i=0;i<ids.length;i++){
            if(i == position){
                dots[i].setImageResource(R.drawable.page_indicator_focused);
            }else{
                dots[i].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
