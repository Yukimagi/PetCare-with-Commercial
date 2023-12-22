package com.example.petcare_store;

//***以下複製
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class AdDataBaseHelper extends SQLiteOpenHelper {
    private static final String DataBaseName = "AD";//database名稱
    private static final int DataBaseVersion = 1;//版本1

    //下面建立建構子
    public AdDataBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version,String TableName) {
        super(context, DataBaseName, null, DataBaseVersion);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SqlTable = "CREATE TABLE IF NOT EXISTS AD (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "STORE_NAME text not null," +
                "ADDRESS TEXT not null," +
                "PRODUCT_NAME TEXT ," +
                "PRODUCT_DETAIL TEXT ," +
                "IMAGE_ad TEXT," + // 第一個圖片欄位
                "IMAGE_promo TEXT" +  // 第二個圖片欄位
                ")";
        db.execSQL(SqlTable);
    }
    @SuppressLint("Range")
    public ArrayList<String> getAllStoreNames() {
        ArrayList<String> storeNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT STORE_NAME FROM AD", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                storeNames.add(cursor.getString(cursor.getColumnIndex("STORE_NAME")));
            }
            cursor.close();
        }
        return storeNames;
    }

    @SuppressLint("Range")
    public StoreInfo getStoreInfo(String storeName) {
        StoreInfo storeInfo = new StoreInfo();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM AD WHERE STORE_NAME=?", new String[]{storeName});
        if (cursor != null && cursor.moveToFirst()) {
            storeInfo.storeName = cursor.getString(cursor.getColumnIndex("STORE_NAME"));
            storeInfo.address = cursor.getString(cursor.getColumnIndex("ADDRESS"));
            storeInfo.productName = cursor.getString(cursor.getColumnIndex("PRODUCT_NAME"));
            storeInfo.productDetail = cursor.getString(cursor.getColumnIndex("PRODUCT_DETAIL"));
            storeInfo.imageAd = cursor.getString(cursor.getColumnIndex("IMAGE_ad"));
            storeInfo.imagePromo = cursor.getString(cursor.getColumnIndex("IMAGE_promo"));
            cursor.close();
        }
        return storeInfo;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        final String SQL = "DROP TABLE AD";
        db.execSQL(SQL);
    }
}