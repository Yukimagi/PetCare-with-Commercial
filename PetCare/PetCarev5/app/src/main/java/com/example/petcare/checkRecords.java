package com.example.petcare;

import static com.example.petcare.AddNewTask.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class checkRecords extends AppCompatActivity {

    String UserID = "10001";
    private static final String DataBaseName = "Noteappointment";//database名稱
    private static final int DataBaseVersion = 1;//版本1
    private static String DataBaseTable = "appointment1";
    private static SQLiteDatabase db;
    private appointmentdata sqlDataBaseHelper;
    String PETID,PETNAME;

    ArrayList<String> FBdata = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkrecords);


        FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mdatabase.getReference();

//        //Log.d("Appointment", "onCreate method called");
//        show();


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

                AlertDialog.Builder builder = new AlertDialog.Builder(checkRecords.this);
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
                                intent.setClass(checkRecords.this, modifyappointment.class);

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

        myRef.child("rec").child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot typeSnapshot : snapshot.getChildren()) {
                        String date = typeSnapshot.getKey(); // 獲取類型（A、M）

                        String comment = typeSnapshot.child("comment").getValue(String.class);
                        String medicine = typeSnapshot.child("medicine").getValue(String.class);
                        String return1 = typeSnapshot.child("return").getValue(String.class);
                        FBdata.add("Date: " + date + "\n注意事項: " + comment +
                                "\n用藥: " + medicine + "\n下次回診時間: " + return1);
                    }
                }
//                String value = snapshot.getValue(String.class);
//                Log.d(TAG, "Value is: " + value);
//                FBdata.add(value);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                show();
            }
        }, 2000);

    }
    public void show() {

        //----------資料庫開啟(一定要再create裡)--------
//        sqlDataBaseHelper = new appointmentdata(getApplicationContext(), DataBaseName, null, DataBaseVersion, DataBaseTable);
//        db = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
//
//        Cursor c = db.rawQuery("SELECT * FROM " + DataBaseTable, null);

        // 建立適配器，使用 ArrayAdapter 作為範例
        ArrayList<String> notesList = new ArrayList<>();

//        for(int a = 0; a < 5;a++){
//            notesList.add("meow");
//        }
        for(String element: FBdata){
            notesList.add(element);
        }
//
//        if (c.moveToFirst()) {
//            do {
//
//                String title = c.getString(1);
//                String content = c.getString(2);
//                String DATE = c.getString(3);
//                String TIME_PERIOD = c.getString(4);
//                String OTHER = c.getString(5);
//
//                //String note1 =  date + "\n" + title + " " + other + " $"+ content;
//                //String note1 =  date + "\n" + title + "\n" + other + "\n"+ "$" + content;
//                //String note1 = PETID + "\n" + PETNAME + "\n" + DATE + "\n" + TIME_PERIOD + "\n" + OTHER;
//                String note1 = title + "\n" + content + "\n" + DATE + "\n" + TIME_PERIOD + "\n" + OTHER;
//
//                notesList.add(note1);
//            } while (c.moveToNext());
//        }

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