package com.example.mymap.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build;
import android.util.TypedValue;

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

    public static float dp2px(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

}
