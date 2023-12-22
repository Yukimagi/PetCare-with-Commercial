package com.example.petcare_store;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpClient {

    public interface OnTcpActionListener {
        void onConnectSuccess();
        void onConnectFailure(String error);
        void onSendMessageSuccess(String response);
        void onSendMessageFailure(String error);
        void onDisconnect();
    }

    private Socket socket;
    private OnTcpActionListener listener;
    private boolean isSocketConnected = false;

    public void connect(OnTcpActionListener listener) {
        this.listener = listener;
        if (!isSocketConnected) {
            new ConnectTask().execute("172.20.10.3", "8888");
        } else {
            // Socket 已經連接成功，呼叫成功回呼
            if (listener != null) {
                listener.onConnectSuccess();
            }
        }
    }

    public void sendMessage(String message, OnTcpActionListener listener) {
        if (isSocketConnected) {
            new SendMessageTask().execute(message);
        } else {
            // Socket 未連接，呼叫失敗回呼
            if (listener != null) {
                listener.onSendMessageFailure("Socket 未連接");
            }
        }
    }

    public void disconnect(OnTcpActionListener listener) {
        new DisconnectTask().execute();
    }

    private class ConnectTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = null;

            try {
                String serverIP = params[0];
                int serverPort = Integer.parseInt(params[1]);

                socket = new Socket(serverIP, serverPort);
                isSocketConnected = true;

                result = "連線成功";
            } catch (UnknownHostException e) {
                result = "無法識別的主機";
            } catch (IOException e) {
                result = "連線失敗：" + e.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (listener != null) {
                if (result.equals("連線成功")) {
                    listener.onConnectSuccess();
                } else {
                    listener.onConnectFailure(result);
                }
            }
        }
    }

    private class SendMessageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = null;

            try {
                String message = params[0];
                if (socket != null && socket.isConnected()) {
                    // 傳送訊息
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    DataInputStream instream = new DataInputStream(socket.getInputStream());
                    outputStream.writeUTF(message);

                    // 讀取伺服器回傳的訊息
                    DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                    result = inputStream.readUTF();
                } else {
                    result = "Socket未連線";
                }
            } catch (IOException e) {
                result = "資訊傳送失敗：" + e.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (listener != null) {
                if (result.equals("Socket未連線")) {
                    listener.onSendMessageFailure(result);
                } else {
                    listener.onSendMessageSuccess(result);
                }
            }
        }
    }

    private class DisconnectTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (socket != null && socket.isConnected()) {
                    socket.close();
                }
            } catch (IOException e) {
                Log.e("TcpClient", "Error closing socket: " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (listener != null) {
                listener.onDisconnect();
            }
            isSocketConnected = false;
        }
    }
}

