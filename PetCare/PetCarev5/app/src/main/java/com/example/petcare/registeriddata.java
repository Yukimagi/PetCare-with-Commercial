/*package com.example.petcare;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class registeriddata extends SQLiteOpenHelper {
    private static final String DataBaseName = "Noteregisterid"; // table名稱
    private static final int DataBaseVersion = 1; // 版本2

    // 下面建立建構子
    public registeriddata(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, String TableName) {
        super(context, DataBaseName, null, DataBaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 記帳資料庫
        String SqlTable1 = "CREATE TABLE IF NOT EXISTS registerid1 (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title text not null," +
                "content TEXT not null," +
                "species TEXT not null," +
                "phone TEXT not null" +
                ")";
        db.execSQL(SqlTable1);

        // Set the starting value for the _id column
        String setStartingValue = "INSERT INTO registerid1 (_id) VALUES(10000);";
        db.execSQL(setStartingValue);
    }

    // Add this method to return the inserted ID
    public long insertData(String title, String content, String species, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("content", content);
        values.put("species", species);
        values.put("phone", phone);

        // Insert the data
        long insertedId = db.insert("registerid1", null, values);
        db.close();  // Close the database connection

        return insertedId;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String SQL1 = "DROP TABLE registerid1";
        db.execSQL(SQL1);
    }
}*/
package com.example.petcare;
//***以下複製
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Button;

import androidx.annotation.Nullable;

public class registeriddata extends SQLiteOpenHelper {
    private static final String DataBaseName = "Noteregisterid";
    private static final int DataBaseVersion = 1;//版本2

    //下面建立建構子
    public registeriddata(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version,String TableName) {
        super(context, DataBaseName, null, DataBaseVersion);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        //記帳資料庫
        String SqlTable1 = "CREATE TABLE IF NOT EXISTS registerid1 (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title text not null," +
                "content TEXT not null," +
                "species TEXT not null," +
                "phone TEXT not null" +
                ")";
        db.execSQL(SqlTable1);
        // Set the starting value for the _id column
        /*String setStartingValue = "INSERT INTO registerid1 (_id) VALUES(10000);";
        db.execSQL(setStartingValue);*/

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String SQL1 = "DROP TABLE registerid1";
        db.execSQL(SQL1);
        /*final String SQL1 = "DROP TABLE IF EXISTS registerid1;";
        db.execSQL(SQL1);
        onCreate(db);*/
    }

}


