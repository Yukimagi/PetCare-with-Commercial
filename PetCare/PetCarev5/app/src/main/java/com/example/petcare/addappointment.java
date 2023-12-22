package com.example.petcare;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioButton;
import android.widget.CompoundButton;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petcare.Utils.TcpClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class addappointment extends AppCompatActivity implements TcpClient.OnTcpActionListener{

    private static final String DataBaseName = "Noteappointment";
    private static final int DataBaseVersion = 1;
    private static String DataBaseTable = "appointment1";
    private static SQLiteDatabase db;
    private appointmentdata sqlDataBaseHelper;
    TextView text;
    Button button;
    DatePickerDialog.OnDateSetListener datePicker;
    Calendar calendar = Calendar.getInstance();
    String dateString;
    String pet;
    boolean click=false;
    String formattedData;
    String ret="";
    private Handler handler = new Handler(Looper.getMainLooper());
    //------------------------------TCP
//    private static final String TAG = "TCP_1";
//    //private static final String SERVER_IP = "192.168.229.47"; // 請替換為伺服器的 IP 地址
//    //private static final String SERVER_IP = "192.168.0.101";
//    private static final String SERVER_IP = "172.20.10.8";
//    private static final int SERVER_PORT = 8888; // 請確保使用與伺服器相同的端口號
//    Socket socket;
//    //private EditText editTextMessage;
//    private DataOutputStream outputStream;
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
        setContentView(R.layout.activity_addappointment);
        //日期
        button = findViewById(R.id.button);
        text = findViewById(R.id.text);

        tcpClient = new TcpClient();
        connectToServer();

        datePicker = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy/MM/dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.TAIWAN);
                text.setText("日期：" + sdf.format(calendar.getTime()));
                dateString = sdf.format(calendar.getTime());
            }
        };

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(addappointment.this,
                        datePicker,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        //下面是透過轉換radio button轉換照片
        RadioButton radioButtonM = findViewById(R.id.radioButton_M);
        RadioButton radioButtonA = findViewById(R.id.radioButton_A);
        RadioButton radioButtonN = findViewById(R.id.radioButton_N);

        radioButtonM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //ImageView imageView = findViewById(R.id.imageView);

                if (isChecked) {
                    //imageView.setImageResource(R.drawable.cat);
                    pet="M";
                    click=true;
                }
            }
        });

        radioButtonA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //ImageView imageView = findViewById(R.id.imageView);

                if (isChecked) {
                    //imageView.setImageResource(R.drawable.dog);
                    pet="A";
                    click=true;
                }
            }
        });

        radioButtonN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //ImageView imageView = findViewById(R.id.imageView);

                if (isChecked) {
                    //imageView.setImageResource(R.drawable.cat);
                    pet="N";
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
                EditText editTextPETID = findViewById(R.id.editTextPETID);//
                EditText editTextPETNAME = findViewById(R.id.editTextPETNAME);//
                EditText editTextOther = findViewById(R.id.editTextTextMultiLine3);//備註

                String PETID = editTextPETID.getText().toString();
                String PETNAME = editTextPETNAME.getText().toString();
                String OTHER = editTextOther.getText().toString();
                String req = "rsvreq:"+PETID+";"+dateString.replace("/", "-")+";"+pet+";"+PETNAME+";"+OTHER;

                sendMessageToServer(req);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (PETID.isEmpty() || PETNAME.isEmpty() || dateString.isEmpty() || pet.isEmpty() || OTHER.isEmpty()) {
                            // 顯示提示訊息
                            Toast.makeText(getApplicationContext(), "尚有欄位未填寫", Toast.LENGTH_SHORT).show();
                        }else if(ret.equals("ERROR")){
                            Toast.makeText(getApplicationContext(), "連線錯誤", Toast.LENGTH_SHORT).show();
                        }else if(ret.equals("FAIL;full")){
                            Toast.makeText(getApplicationContext(), "人數已滿", Toast.LENGTH_SHORT).show();
                        }else if(ret.equals("FAIL;duplicate")){
                            Toast.makeText(getApplicationContext(), "重複預約，請確認預約紀錄1", Toast.LENGTH_SHORT).show();
                        }else{
                            //----------資料庫開啟(一定要再create裡)--------
                            sqlDataBaseHelper = new appointmentdata(getApplicationContext(),DataBaseName,null,DataBaseVersion,DataBaseTable);
                            db = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫
                            //新增資料庫
                            ContentValues values = new ContentValues();

                            //values.put("PETID", PETID);   //PETID
                            //values.put("PETNAME", PETNAME);   //PETNAME
                            values.put("title", PETID);   //PETID
                            values.put("content", PETNAME);   //PETNAME
                            values.put("DATE", dateString); //DATE
                            values.put("TIME_PERIOD", pet);//TIME_PERIOD
                            values.put("OTHER", OTHER); //備註

                            // 插入資料到記事表
                            long insertedId = db.insert(DataBaseTable,null,values);
                            // 檢查是否成功插入資料
                            if (insertedId != -1) {
                                Toast.makeText(getApplicationContext(), "記事新增成功第"+insertedId+"筆掛號", Toast.LENGTH_SHORT).show();
                                // 將資料整理成傳送給server的指定格式
                                formattedData = "revers:" + PETID + "," + dateString + "," + pet + "," + OTHER;
//                        new TCPClient().execute(formattedData);
                                Intent intent = new Intent();
                                intent.setClass(addappointment.this, Appointment.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "記事新增失敗"+insertedId, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, 100);


            }
        });

    }
    /////
//    private static class TCPClient extends AsyncTask<String, Void, String> {
//        private static final int TIMEOUT = 10000; // 10 seconds timeout
//        private Context context;
//
//        // 建立建構子接收Context物件
//        public TCPClient(Context context) {
//            this.context = context;
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            String message = params[0];
//            String responseMessage = null;
//
//            try {
//                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
//                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
//                outputStream.writeUTF(message);
//
//                // Set the timeout for reading from the InputStream
//                socket.setSoTimeout(TIMEOUT);
//
//                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
//                responseMessage = inputStream.readUTF();
//            } catch (IOException e) {
//                Log.e(TAG, "Error sending/receiving message", e);
//            }
//
//            return responseMessage;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            if (result != null) {
//                // Server response received, show it in a Toast
//                showToast(result);
//            } else {
//                // Error or timeout occurred, show an error message in a Toast
//                showToast("Error or Timeout");
//            }
//
//        }
//
//        private void showToast(final String message) {
//            // Run on the UI thread to show Toast
//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }

}