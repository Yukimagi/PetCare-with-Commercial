package com.example.petcare_store;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //合作店家資訊
        Button button_process = (Button) findViewById(R.id.button_process);
        button_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent process = new Intent(MainActivity.this, process.class);
                startActivity(process);

            }
        });

        //合作店家總覽
        Button button_check = (Button) findViewById(R.id.button_check);
        button_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent check = new Intent(MainActivity.this, check.class);
                startActivity(check);

            }
        });

        //合作店家廣告推播
        Button button_AD = (Button) findViewById(R.id.button_AD);
        button_AD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AD = new Intent(MainActivity.this, AD.class);
                startActivity(AD);

            }
        });
    }
}