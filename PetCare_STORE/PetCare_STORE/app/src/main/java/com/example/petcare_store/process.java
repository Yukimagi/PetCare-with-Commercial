package com.example.petcare_store;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class process extends AppCompatActivity {
    private EditText editTextStoreName, editTextAddress, editTextImageAd, editTextImagePromo, editTextProductName, editTextProductDetail;
    private Button addButton, updateButton;

    private static final String DataBaseName = "AD";
    private static final int DataBaseVersion = 1;
    private static String DataBaseTable = "AD";
    private static SQLiteDatabase db;
    private AdDataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        //----------資料庫開啟(一定要再create裡)--------
        dbHelper = new AdDataBaseHelper(getApplicationContext(),DataBaseName,null,DataBaseVersion,DataBaseTable);

        editTextStoreName = findViewById(R.id.editTextTextMultiLine_StoreName);
        editTextAddress = findViewById(R.id.editTextTextMultiLine_Address);
        editTextImageAd = findViewById(R.id.editTextTextMultiLine_ad);
        editTextImagePromo = findViewById(R.id.editTextTextMultiLine_promo);
        editTextProductName = findViewById(R.id.editTextTextMultiLine_productName);
        editTextProductDetail = findViewById(R.id.editTextTextMultiLine7);

        addButton = findViewById(R.id.button_add);
        updateButton = findViewById(R.id.button_change);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
            }
        });
    }

    private void addData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();// 開啟資料庫
        ContentValues values = new ContentValues();

        String storeName = editTextStoreName.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String imageAd = editTextImageAd.getText().toString().trim();
        String imagePromo = editTextImagePromo.getText().toString().trim();
        String productName = editTextProductName.getText().toString().trim();
        String productDetail = editTextProductDetail.getText().toString().trim();

        if (storeName.isEmpty() || address.isEmpty() || imageAd.isEmpty()) {
            Toast.makeText(this, "店家名稱、店家網址、宣傳圖片連結不得為空", Toast.LENGTH_SHORT).show();
            return;
        }

        values.put("STORE_NAME", storeName);
        values.put("ADDRESS", address);
        values.put("IMAGE_ad", imageAd);
        values.put("IMAGE_promo", imagePromo);
        values.put("PRODUCT_NAME", productName);
        values.put("PRODUCT_DETAIL", productDetail);

        // 在這裡你需要處理自動增加的 _id
        long newRowId = db.insert("AD", null, values);

        if (newRowId != -1) {
            Toast.makeText(this, "資料新增成功", Toast.LENGTH_SHORT).show();
            // 清空輸入框
            clearEditTextFields();
        } else {
            Toast.makeText(this, "資料新增失敗", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    private void updateData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        String storeName = editTextStoreName.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String imageAd = editTextImageAd.getText().toString().trim();
        String imagePromo = editTextImagePromo.getText().toString().trim();
        String productName = editTextProductName.getText().toString().trim();
        String productDetail = editTextProductDetail.getText().toString().trim();

        if (storeName.isEmpty()) {
            Toast.makeText(this, "店家名稱不得為空", Toast.LENGTH_SHORT).show();
            return;
        }

        values.put("ADDRESS", address);
        values.put("IMAGE_ad", imageAd);
        values.put("IMAGE_promo", imagePromo);
        values.put("PRODUCT_NAME", productName);
        values.put("PRODUCT_DETAIL", productDetail);

        int rowsAffected = db.update("AD", values, "STORE_NAME=?", new String[]{storeName});

        if (rowsAffected > 0) {
            Toast.makeText(this, "資料更新成功", Toast.LENGTH_SHORT).show();
            // 清空輸入框
            clearEditTextFields();
        } else {
            Toast.makeText(this, "資料更新失敗，店家名稱不存在", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    private void clearEditTextFields() {
        editTextStoreName.setText("");
        editTextAddress.setText("");
        editTextImageAd.setText("");
        editTextImagePromo.setText("");
        editTextProductName.setText("");
        editTextProductDetail.setText("");
    }
}