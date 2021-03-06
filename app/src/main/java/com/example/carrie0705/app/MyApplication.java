package com.example.carrie0705.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.example.carrie0705.bean.City;
import com.example.carrie0705.db.CityDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carrie0705 on 2017/10/25.
 */

public class MyApplication extends Application {
    private static final String TAG = "MyApp";

    private static MyApplication mApplication;
    private CityDB mcityDB;

    private List<City> mCityList;

    public void onCreate(){
        super.onCreate();
        Log.d(TAG,"MyApplication->onCreate");

        mApplication = this;
        mcityDB = openCityDB();
        initCityList();
    }

    private void initCityList(){
        mCityList = new ArrayList<City>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                prepareCityList();
            }
        }).start();
    }
//把数据库中的内容复制到mCityList中
    private boolean prepareCityList(){
        mCityList = mcityDB.getAllCity();
        int i=0;
        for(City city : mCityList){
            i++;
            String cityName = city.getCity();
            String cityCode = city.getNumber();
            Log.d(TAG,cityCode+":"+cityName+" "+i);
        }
        Log.d(TAG,"i="+i);
        return true;
    }

    public List<City> getCityList(){
        return mCityList;
    }

    public static MyApplication getInstance(){
        return mApplication;
    }
//第一次进来时,会将数据库复制进去
    private CityDB openCityDB(){
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases"
                + File.separator
                + CityDB.CITY_DB_NAME;
        File db = new File(path);
        Log.d(TAG,path);
        if(!db.exists()){

            String pathfolder = "/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + getPackageName()
                    + File.separator + "databases"
                    + File.separator;
            File dirFirstFolder = new File(pathfolder);
            if(!dirFirstFolder.exists()){
                dirFirstFolder.mkdirs();
                Log.i("MyApp","mkdirs");
            }
            Log.i("MyApp","db is not exists");
            try{
                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while((len = is.read(buffer)) != -1){
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            }catch (IOException e){
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this,path);
    }
}
