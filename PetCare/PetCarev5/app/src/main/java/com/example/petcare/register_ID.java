package com.example.petcare;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Handler;
import android.widget.RadioButton;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

public class register_ID extends AppCompatActivity {

    private static final String DataBaseName="Noteregisterid";//資料庫名稱
    private static final int DataBaseVersion=1;//版本
    private static String DataBaseTable="registerid1";//資料表名稱
    private static SQLiteDatabase db;
    private registeriddata sqlDataBaseHelper;

    // Method to display the _id on the interface
    private void displayId(long insertedId) {
        TextView tv_result = findViewById(R.id.tv_result);
        tv_result.setText("ID:" + insertedId);

    }
    //-------------------------------------------------------------------------------
    String pet;
    boolean click=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_id);

        //下面是透過轉換radio button轉換照片
        RadioButton radioButtonCat = findViewById(R.id.radioButton_cat);
        RadioButton radioButtonDog = findViewById(R.id.radioButton_dog);

        radioButtonCat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ImageView imageView = findViewById(R.id.imageView);

                if (isChecked) {
                    imageView.setImageResource(R.drawable.cat);
                    pet="貓咪";
                    click=true;
                }
            }
        });

        radioButtonDog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ImageView imageView = findViewById(R.id.imageView);

                if (isChecked) {
                    imageView.setImageResource(R.drawable.dog);
                    pet="狗狗";
                    click=true;
                }
            }
        });

        //新增完成按鈕
        Button button= findViewById(R.id.finish);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 獲取標題和內容的值

                EditText editTextPETNAME = findViewById(R.id.editTextTextMultiLine1);//寵物名
                EditText editTextOWNERNAME = findViewById(R.id.editTextTextMultiLine2);//主人名
                EditText editTextPHONE = findViewById(R.id.editTextTextMultiLine3);//主人電話

                String PETNAME = editTextPETNAME.getText().toString();
                String OWNERNAME = editTextOWNERNAME.getText().toString();
                String PHONE = editTextPHONE.getText().toString();

                //----------資料庫開啟(一定要再create裡)--------
                sqlDataBaseHelper = new registeriddata(getApplicationContext(),DataBaseName,null,DataBaseVersion,DataBaseTable);
                db = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
                //新增資料庫
                ContentValues values = new ContentValues();

                values.put("title", PETNAME);   //PETNAME
                values.put("content", OWNERNAME);   //OWNERNAME
                values.put("species", pet);   //PETNAME
                values.put("phone", PHONE); //PHONE

                // 插入資料到記事表
                long insertedId = db.insert(DataBaseTable,null,values);
                // 檢查是否成功插入資料
                if (insertedId != -1) {
                    insertedId=insertedId+10000;
                    // Successfully inserted, now display the _id

                    Toast.makeText(getApplicationContext(), "記事新增成功"+insertedId, Toast.LENGTH_SHORT).show();
                    /*Intent intent = new Intent();
                    intent.setClass(register_ID.this, Appointment.class);
                    startActivity(intent);*/
                    displayId(insertedId);
                } else {
                    Toast.makeText(getApplicationContext(), "記事新增失敗"+insertedId, Toast.LENGTH_SHORT).show();
                }

            }


        });

        /*// Method to display the _id on the interface
        private void displayId(long insertedId) {
            TextView tv_result = findViewById(R.id.tv_result);
            tv_result.setText("ID:" + insertedId);

        }*/

        Button button2 = findViewById(R.id.back);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 點擊返回後，跳至預約掛號介面
                Intent intent = new Intent(register_ID.this, Appointment.class);
                startActivity(intent);
            }
        });

    }
}