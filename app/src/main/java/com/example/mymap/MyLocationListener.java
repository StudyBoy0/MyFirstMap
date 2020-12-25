package com.example.mymap;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;

public class MyLocationListener implements AMapLocationListener {
    private final String TAG = "MyLocationListener";
    private Context mContext;
    private DBOpenHelper openHelper;

    public MyLocationListener(Context context){
        mContext = context;
    }
    AMap aMap;
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        //定位到当前位置
        if(aMapLocation!=null){
            if(aMapLocation.getErrorCode() == 0){ //ErrorCode等于0代表定位成功，否则就定位失败
                navigateTo(aMapLocation);
            }
        }

    }
    private void navigateTo(AMapLocation aMapLocation){
        //获取经纬度
        LatLng ll = new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
        Log.i(TAG,ll.toString());

        String lng = String.valueOf(aMapLocation.getLongitude());
        String lat = String.valueOf(aMapLocation.getLatitude());


        //存储获取的经纬度数据
        openHelper = new DBOpenHelper(mContext,"myData.db",null,1);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Lng",lng);
        values.put("Lat",lat);
        db.insert("LocationData",null,values);
        values.clear();

    }


}
