package com.example.carrie0705.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * Created by Carrie0705 on 2017/9/20.
 */

public class MyActivity extends Activity implements View.OnClickListener{
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        imageView = (ImageView) findViewById(R.id.refresh);
        imageView.setOnClickListener(this);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.refresh:
                Toast.makeText(MyActivity.this,"This is refresh!",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
