package com.example.mymap.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DBOpenHelper extends SQLiteOpenHelper {


    private final String CREATE_POSITION_SQL = "create table Location ("
            + "id integer primary key autoincrement, "
            + "tag real, "
            + "lat real, "
            + "lng real) ";

    public DBOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_POSITION_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(CREATE_POSITION_SQL);
    }
}
