package com.example.mymap;

import android.app.Activity;
import android.os.Build;

public class SystemUtils {
    /**
    * 判断Activity是否销毁
    */
    public static boolean isHostValidate(Activity activity){
        if(activity == null){
            return true;
        }

        if(activity.isFinishing()){
            return true;
        }

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && activity.isDestroyed();
    }
}
