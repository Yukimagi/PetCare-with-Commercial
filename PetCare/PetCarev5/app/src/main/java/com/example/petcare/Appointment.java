package com.example.petcare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import android.util.Log;


public class Appointment extends AppCompatActivity {

    private static final String DataBaseName = "Noteappointment";//database名稱
    private static final int DataBaseVersion = 1;//版本1
    private static String DataBaseTable = "appointment1";
    private static SQLiteDatabase db;
    private appointmentdata sqlDataBaseHelper;
    String PETID,PETNAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        //Log.d("Appointment", "onCreate method called");
        show();

        //預約按鈕
        Button button= findViewById(R.id.addbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Appointment.this, addappointment.class);
                startActivity(intent);
            }
        });
        //註冊ID按鈕
        Button register_ID= findViewById(R.id.register_ID);
        register_ID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Appointment.this, register_ID.class);
                startActivity(intent);
            }
        });


        // 假設您的 ListView 的 ID 是 listViewNotes
        ListView listViewNotes = findViewById(R.id.listViewNotes);
        listViewNotes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 獲取長按的項目的標題

                String s = listViewNotes.getItemAtPosition(position).toString();
                String[] title=s.split("\n");

                // 提取價錢部分->提取ID部分
                String PETID = title[0].trim();
                String PETNAME = title[1].trim();
                String TIME_PERIOD = title[3].trim();


                //----------資料庫開啟(一定要再create裡)--------
                sqlDataBaseHelper = new appointmentdata(getApplicationContext(),DataBaseName,null,DataBaseVersion,DataBaseTable);
                db = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫

                // 準備 WHERE 條件，根據標題找到對應的記事項目

                String whereClause = "title" + " = ?";
                String[] whereArgs = {title[0]};

                AlertDialog.Builder builder = new AlertDialog.Builder(Appointment.this);
                builder.setTitle("編輯")
                        .setMessage("修改還是刪除")
                        .setPositiveButton("刪除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 刪除數據庫中的記事項目
                                int rowsDeleted = db.delete(DataBaseTable, whereClause, whereArgs);
                                // System.out.println(whereClause+whereArgs);

                                // 檢查是否成功刪除資料
                                if (rowsDeleted > 0) {
                                    Toast.makeText(getApplicationContext(), "記事刪除成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "記事刪除失敗", Toast.LENGTH_SHORT).show();
                                }


                                show();
                            }
                        })

                        .setNegativeButton("修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setClass(Appointment.this, modifyappointment.class);

                                //傳值到下一頁
                                intent.putExtra("title",PETID.toString());  //寵物ID
                                intent.putExtra("content",PETNAME.toString());  //寵物名
                                intent.putExtra("DATE",title[2].toString());    //日期
                                intent.putExtra("TIME_PERIOD",TIME_PERIOD.toString()); //時段(下拉式)
                                intent.putExtra("OTHER",title[4].toString());   //備註
                                startActivity(intent);
                            }
                        });



                builder.show();

                return false;


            }

        });
    }
    public void show() {

        //----------資料庫開啟(一定要再create裡)--------
        sqlDataBaseHelper = new appointmentdata(getApplicationContext(), DataBaseName, null, DataBaseVersion, DataBaseTable);
        db = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫

        Cursor c = db.rawQuery("SELECT * FROM " + DataBaseTable, null);

        // 建立適配器，使用 ArrayAdapter 作為範例
        ArrayList<String> notesList = new ArrayList<>();

        if (c.moveToFirst()) {
            do {

                String title = c.getString(1);
                String content = c.getString(2);
                String DATE = c.getString(3);
                String TIME_PERIOD = c.getString(4);
                String OTHER = c.getString(5);

                //String note1 =  date + "\n" + title + " " + other + " $"+ content;
                //String note1 =  date + "\n" + title + "\n" + other + "\n"+ "$" + content;
                //String note1 = PETID + "\n" + PETNAME + "\n" + DATE + "\n" + TIME_PERIOD + "\n" + OTHER;
                String note1 = title + "\n" + content + "\n" + DATE + "\n" + TIME_PERIOD + "\n" + OTHER;

                notesList.add(note1);
            } while (c.moveToNext());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_list_item_1,
                notesList
        );

        // 設定適配器給 ListView
        ListView listViewNotes = findViewById(R.id.listViewNotes);
        listViewNotes.setAdapter(adapter);

    }
}