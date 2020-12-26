package com.example.mymap.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.amap.api.maps.model.LatLng;
import com.example.mymap.entity.LineDataEntity;
import com.example.mymap.callback.CallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by TennyQ on 12/26/20
 */
public class LocationDBHelper {

    private static final String TAG = "LocationDBHelper";
    private static final String THREAD = "Thread";

    private static final int MSG_LINE_LIST = 0x01;

    private Context context;
    private DBOpenHelper dbHelper;
    private long timeTag;
    private CallBack<List<LineDataEntity>> mCallBack;

    private ExecutorService executorService;

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_LINE_LIST:
                    if (mCallBack != null) {
                        List<LineDataEntity> lineEntityList = (List<LineDataEntity>) msg.obj;
                        if (lineEntityList == null || lineEntityList.isEmpty()) {
                            mCallBack.failed("还没有历史数据");
                        } else {
                            mCallBack.success(lineEntityList);
                        }
                    }
                    break;
            }
        }
    };

    public LocationDBHelper(Context context, CallBack<List<LineDataEntity>> callBack) {
        this.context = context;
        this.mCallBack = callBack;
        timeTag = System.currentTimeMillis();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void init() {
        dbHelper = new DBOpenHelper(context, "LatLng.db", null, 1);
    }

    public void saveLatLng(final double lat, final double lng) {
        Log.d(THREAD, "saveLatLng current Thread: " + Thread.currentThread().getId());
        execute(new Runnable() {
            @Override
            public void run() {
                realSaveLatLng(timeTag, lat, lng);
            }
        });
    }

    private void realSaveLatLng(long tag, double lat, double lng) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("insert into Location (tag, lat, lng) values(?, ?, ?)", new Object[]{tag, lat, lng});
    }

    public void searchLocation() {
        execute(new Runnable() {
            @Override
            public void run() {
                realSearchLocation(timeTag);
            }
        });
    }

    private void realSearchLocation(long exceptTimeTag) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from Location where tag IS NOT " + exceptTimeTag, null);

        List<LineDataEntity> lineEntityList = new ArrayList<>();

        LineDataEntity lineDataEntity = null;
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                long tag = cursor.getLong(cursor.getColumnIndex("tag"));
                double lat = cursor.getDouble(cursor.getColumnIndex("lat"));
                double lng = cursor.getDouble(cursor.getColumnIndex("lng"));

                if (lineDataEntity == null || lineDataEntity.getTimeTag() != tag) {
                    lineDataEntity = new LineDataEntity(tag);
                    lineDataEntity.addLatLng(new LatLng(lat, lng));
                    lineEntityList.add(lineDataEntity);
                } else {
                    lineDataEntity.addLatLng(new LatLng(lat, lng));
                }

            } while (cursor.moveToNext()) ;
        }
        cursor.close();

        Message message = Message.obtain(handler);
        message.what = MSG_LINE_LIST;
        message.obj = lineEntityList;

        handler.sendMessage(message);

        Log.d(TAG, lineEntityList.toString());
    }


    private void execute(Runnable runnable) {
        executorService.submit(runnable);
    }

    private void release() {
        mCallBack = null;
    }

}
