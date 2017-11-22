package com.example.carrie0705.myapplication;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;


/**
 * Created by Carrie0705 on 2017/11/15.
 */

public class ClearEditText extends android.support.v7.widget.AppCompatEditText implements View.OnFocusChangeListener,TextWatcher{

    private Drawable mClearDrawable;
    //public ClearEditText(Context context){
    //    this(context,null);
    //}
    //public ClearEditText(Context context,AttributeSet atters){
       // this(context,atters,android.R.editTextStyle);
    //}
    public ClearEditText(Context context,AttributeSet atters,int defStyle){
        super(context,atters,defStyle);
        init();
    }
    public void init(){
        mClearDrawable = getCompoundDrawables()[2];
        //if(mClearDrawable == null){
        //    mClearDrawable = getResources().getDrawable(R.drawable.emotionstore_progresscancelbtn);
        //}
        mClearDrawable.setBounds(0,0,mClearDrawable.getIntrinsicWidth(),
                mClearDrawable.getIntrinsicHeight());
        setClearIconVisible(false);
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }
    public void setClearIconVisible(boolean visible){
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1],right,getCompoundDrawables()[3]);

    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setClearIconVisible(s.length()>0);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            setClearIconVisible(getText().length()>0);
        }else{
            setClearIconVisible(false);
        }
    }

}
