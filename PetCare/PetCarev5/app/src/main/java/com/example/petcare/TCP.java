package com.example.petcare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.view.View;
import android.widget.Button;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCP extends AppCompatActivity {
/*
    private static final String TAG = "TCP";
    //private static final String SERVER_IP = "192.168.229.47"; // 請替換為伺服器的 IP 地址
    private static final String SERVER_IP = "192.168.0.101";
    private static final int SERVER_PORT = 8888; // 請確保使用與伺服器相同的端口號
    Socket socket;
    private EditText editTextMessage;
    private DataOutputStream outputStream;
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp);

    }
/*
    private static class TCPClient extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String message = params[0];

            try {
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF(message);
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error sending message", e);
            }

            return null;
        }
    }*/
}