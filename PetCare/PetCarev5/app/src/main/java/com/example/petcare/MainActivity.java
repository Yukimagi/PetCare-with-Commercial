package com.example.petcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;//***換頁很重要
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;//***
import android.widget.Button;//***
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    //TCP
    private static final String SERVER_IP = "192.168.229.47"; // 請替換為伺服器的 IP 地址
    private static final int SERVER_PORT = 8888; // 請確保使用與伺服器相同的端口號
    static Socket socket;
    private EditText editTextMessage;
    private DataOutputStream outputStream;
    //TCP

    private static class TCPClientA extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String message = params[0];

            try {
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF(message);
//                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error sending message", e);
            }

            return null;
        }
    }

    //////////////////////////////
    private static class TCPClient extends AsyncTask<String, Void, String> {
        private static final int TIMEOUT = 5000; // 10 seconds timeout
        private Context context;

        // 建立建構子接收Context物件
        public TCPClient(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            String message = params[0];
            String responseMessage = null;

            try {
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF(message);

                // Set the timeout for reading from the InputStream
                socket.setSoTimeout(TIMEOUT);

                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                responseMessage = inputStream.readUTF();
                socket.close();
            } catch (SocketTimeoutException e) {
                // 在這裡處理超時的情況
                Log.e(TAG, "Timeout while waiting for server response", e);
                responseMessage = "Timeout";
            } catch (IOException e) {
                // 其他IO例外的處理
                Log.e(TAG, "Error sending/receiving message", e);
                responseMessage = "IOException";
            }

            return responseMessage;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Server response received, show it in a Toast
                showToast(result);
            } else {
                // Error or timeout occurred, show an error message in a Toast
                showToast("Error");
            }
        }

        private void showToast(final String message) {
            // Run on the UI thread to show Toast
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        String message = "hello";
//        new TCPClientA().execute(message);

        //寵物醫院頁面
        Button button_hospital = (Button) findViewById(R.id.button_hospital);
        button_hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent hospital = new Intent(MainActivity.this, common.class);
                startActivity(hospital);

            }
        });

        //寵物記帳
        Button button_account = (Button) findViewById(R.id.button_account);
        button_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent account = new Intent(MainActivity.this, Account.class);
                startActivity(account);

            }
        });

        //代辦事項
        Button button_item = (Button) findViewById(R.id.button_item);
        button_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent item = new Intent(MainActivity.this, Item.class);
                startActivity(item);

            }
        });

        //提醒功能
        Button button_remind = (Button) findViewById(R.id.button_remind);
        button_remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent remind = new Intent(MainActivity.this, Remind.class);
                startActivity(remind);

            }
        });

        //寵物日記
        Button button_diary = (Button) findViewById(R.id.button_diary);
        button_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent diary = new Intent(MainActivity.this, Diary.class);
                startActivity(diary);

            }
        });

        //主人相性測驗
        Button button_test = (Button) findViewById(R.id.button_test);
        button_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent test = new Intent(MainActivity.this, Test.class);
                startActivity(test);

            }
        });

        //寵物保健
        Button btnHealth = (Button) findViewById(R.id.btnHealth);
        btnHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent health = new Intent(MainActivity.this, Health.class);
                startActivity(health);

            }
        });

        //預約掛號
        Button button_appointment = (Button) findViewById(R.id.button_appointment);
        button_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent appointment = new Intent(MainActivity.this, Appointment.class);
                startActivity(appointment);
            }
        });

        //看診紀錄
        Button button_record = (Button) findViewById(R.id.button_record);
        button_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent record = new Intent(MainActivity.this, checkRecords.class);
                startActivity(record);
            }
        });

//        Button button_checkRecords = (Button) findViewById(R.id.button_checkRecords);
//        button_checkRecords.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent checkRecords = new Intent(MainActivity.this,checkRecords.class);
//                startActivity(checkRecords);
//
//            }
//        });

        //使用者介面
        Button button_user = (Button) findViewById(R.id.button_user);
        button_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent user = new Intent(MainActivity.this, UserInterface.class);
                startActivity(user);
            }
        });
    }
}