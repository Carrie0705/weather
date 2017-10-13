package com.example.carrie0705.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Carrie0705 on 2017/10/11.
 */

public class NetUtil {
    public static final int NETWORN_NONE = 0;
    public static final int NETWORN_WIFI = 1;
    public static final int NETWORN_MOBILE = 2;

    public static int getNetworkState(Context context){
        ConnectivityManager connManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if(networkInfo == null){
            return NETWORN_NONE;
        }

        int nType = networkInfo.getType();
        if(nType == connManager.TYPE_MOBILE){
            return NETWORN_MOBILE;
        }else if(nType == connManager.TYPE_WIFI){
            return  NETWORN_WIFI;
        }
        return NETWORN_NONE;
    }
}
