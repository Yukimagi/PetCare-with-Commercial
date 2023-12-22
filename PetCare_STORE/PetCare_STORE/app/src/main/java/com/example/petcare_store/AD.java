package com.example.petcare_store;



import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.example.petcare_store.TcpClient;
public class AD extends AppCompatActivity implements TcpClient.OnTcpActionListener{
    String ret="";


    private Handler handler = new Handler(Looper.getMainLooper());
    //TCP------------------------------
    private TcpClient tcpClient;
    ///////////////////////////////////////////////////////////////////////////////
    private void connectToServer() {
        tcpClient.connect(this);
    }
    // 實現OnTcpActionListener接口的方法
    @Override
    public void onConnectSuccess() {
        // 連接成功的處理
        Log.d("YourActivity", "連接成功");
    }
    @Override
    public void onConnectFailure(String error) {
        // 連接失敗的處理
        Log.e("YourActivity", "連接失敗: " + error);
    }
    @Override
    public void onSendMessageSuccess(String response) {
        // 資訊傳送成功的處理，可以使用 response 參數來處理伺服器的回應
        Log.d("YourActivity", "資訊傳送成功，伺服器回應：" + response);
        ret = response;
    }

    @Override
    public void onSendMessageFailure(String error) {
        // 資訊傳送失敗的處理
        Log.e("YourActivity", "資訊傳送失敗: " + error);
    }
    @Override
    public void onDisconnect() {
        // 斷開連線的處理
        Log.d("YourActivity", "已斷開連線");
    }
    // 在需要傳送資訊時呼叫
    private void sendMessageToServer(String message) {
        tcpClient.sendMessage(message, this);
    }

    // 在需要斷開連線時呼叫
    private void disconnectFromServer() {
        tcpClient.disconnect(this);
    }
    ///////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);

        // 在onCreate中，創建Glide實例
        Glide.with(this);
        tcpClient = new TcpClient();
        connectToServer();

        Spinner storeSpinner = findViewById(R.id.storeSpinner);
        Button submitButton = findViewById(R.id.submitButton);

        AdDataBaseHelper dbHelper = new AdDataBaseHelper(this, "AD", null, 1, "AD");
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 獲取所有店家名稱
        ArrayList<String> storeNames = dbHelper.getAllStoreNames();

        // 設置下拉選單的適配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, storeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storeSpinner.setAdapter(adapter);

        // 設置按鈕點擊事件
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 在這裡處理按鈕點擊事件，可以獲取選中的店家名稱
                String selectedStore = storeSpinner.getSelectedItem().toString();
                // 使用 AdDataBaseHelper 中的 getStoreInfo 方法獲取店家相關資訊
                StoreInfo storeInfo = dbHelper.getStoreInfo(selectedStore);




                // 將資訊轉換為逗號分隔的字串
                String storeData = storeInfo.storeName + "," +
                        storeInfo.address + "," +
                        storeInfo.productName + "," +
                        storeInfo.productDetail + "," +
                        storeInfo.imageAd + "," +
                        storeInfo.imagePromo;

                // 需要你實現的邏輯
                sendMessageToServer(storeData);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("YourActivity", ret);
                    }

                }, 100);
            }
        });
    }
}
