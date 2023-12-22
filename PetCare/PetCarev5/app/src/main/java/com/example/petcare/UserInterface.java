package com.example.petcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
public class UserInterface extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interface);

        // 返回上一頁的按鈕設定
        Button User_return = (Button) findViewById(R.id.user_return);
        User_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //收藏頁面
        Button toCollect=(Button) findViewById(R.id.user_collect);
        toCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(UserInterface.this, Collection.class);
                startActivity(intent);
            }
        });

        //推薦頁面
        Button toAdvertise=(Button) findViewById(R.id.user_ads);
        toAdvertise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(UserInterface.this, Advertise.class);
                startActivity(intent);
            }
        });
    }
}