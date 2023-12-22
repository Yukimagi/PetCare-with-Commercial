package com.example.petcare;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class appointmentdata extends SQLiteOpenHelper {
    private static final String DataBaseName = "Noteappointment";
    private static final int DataBaseVersion = 1;

    //下面建立建構子
    public appointmentdata(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, String TableName) {
        super(context, DataBaseName, null, DataBaseVersion);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //記帳資料庫
        String SqlTable1 = "CREATE TABLE IF NOT EXISTS appointment1 (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                //"PETID text not null," +
                //"PETNAME TEXT not null," +
                "title text not null," +
                "content TEXT not null," +
                "DATE TEXT not null," +
                "TIME_PERIOD TEXT not null," +
                "OTHER TEXT not null" +
                ")";
        // 將資料整理成指定格式
        //String formattedData = "rsvreq:" + title + "," + DATE + "," + TIME_PERIOD + "," + OTHER;

        /*
        PETID 10001
        PETNAME
        DATE 2023/11/30
        TIME_PERIOD TEXT A(下午)
        備註 null
        */
        db.execSQL(SqlTable1);
    }
    /*傳給server的資料格式: rsvreq:10001,2023/11/30,A,null */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String SQL1 = "DROP TABLE appointment1";
        db.execSQL(SQL1);
    }


}