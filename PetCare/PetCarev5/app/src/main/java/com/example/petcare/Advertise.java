package com.example.petcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petcare.Utils.TcpClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
// Glide imports
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class Advertise extends AppCompatActivity implements TcpClient.OnTcpActionListener{
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MyAdapter myAdapter;
    private LinkedList<HashMap<String,String>> AdvertiseList;
    private List<HashMap<String, String>> collectionList ;

    String ret="";
    private Handler handler = new Handler(Looper.getMainLooper());
    /////////////////////////////////////////////////////////////////////
    private TcpClient tcpClient;
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

        try {
            // 嘗試從server 獲取數據
            List<HashMap<String, String>> serverData = handleReceivedJson(ret);

            // 只有在server請求成功時，更新RecyclerView數據
            updateRecyclerViewData(serverData);

            // Save received data to SharedPreferences
            saveDataToSharedPreferences(serverData);
        } catch (Exception e) {
            // 處理錯誤、顯示toast
            e.printStackTrace();
        }
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

    ///////////////////////////////TCP//////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise);

        tcpClient = new TcpClient();
        connectToServer();

        //返回上一頁
        Button ads_return = (Button) findViewById(R.id.ads_return);
        ads_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 新建一個空的廣告接收列表
        AdvertiseList = new LinkedList<>();
        // 新建一個空的商家收藏列表//更新收藏
        collectionList  = loadCollectionListFromSharedPreferences();

        // 設定 RecyclerView，並加載廣告數據
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        myAdapter = new MyAdapter();
        // 從server拿取資料
        recyclerView.setAdapter(myAdapter);

        sendMessageToServer("client");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("YourActivity", ret);
            }

        }, 100);

        // Try to load data from SharedPreferences
        loadDataFromSharedPreferences();
    }


    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
        class MyViewHolder extends RecyclerView.ViewHolder{
            public View adsitemView;
            public TextView title,address,productName,productDetail;
            public ImageView storeIMG,couponIMG;
            public Button btCollect;

            // ViewHolder 中的元素初始化
            public MyViewHolder(View v){
                super(v);
                adsitemView = v;
                title = adsitemView.findViewById(R.id.store_title);
                address = adsitemView.findViewById(R.id.store_address);
                productName = adsitemView.findViewById(R.id.product_name);
                productDetail = adsitemView.findViewById(R.id.product_detail);
                storeIMG = adsitemView.findViewById(R.id.storeIMG);
                couponIMG= adsitemView.findViewById(R.id.couponIMG);
                btCollect = itemView.findViewById(R.id.button_Collect);
            }
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 創建一個新的 view
            View adsitemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adsitem, parent, false);    //載入layout
            MyViewHolder vh = new MyViewHolder(adsitemView);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {

            // 綁定 ViewHolder 中的元素和廣告數據
            holder.title.setText(AdvertiseList.get(position).get("title"));
            holder.address.setText(AdvertiseList.get(position).get("address"));
            holder.productName.setText(AdvertiseList.get(position).get("productName"));
            holder.productDetail.setText(AdvertiseList.get(position).get("productDetail"));

            // 使用Glide載入商家照片
            Glide.with(Advertise.this)
                    .load(AdvertiseList.get(position).get("storeImg_url"))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.storeIMG);

            // 使用Glide載入優惠券照片
            Glide.with(Advertise.this)
                    .load(AdvertiseList.get(position).get("couponImg_url"))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.couponIMG);

            // 設定點擊事件，用於收藏商家
            holder.btCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 獲取點擊的商家數據
                    HashMap<String, String> clickedItem = AdvertiseList.get(holder.getAdapterPosition());

                    // 檢查該商家是否已經在收藏列表中
                    if (!isAlreadyCollected(clickedItem)) {
                        // 將商家添加到收藏列表
                        collectionList.add(clickedItem);
                        // Log the value of collectionList
                        Log.d("Collection", "Collection list: " + collectionList);
                        // 儲存更新後的收藏列表
                        saveCollectionListToSharedPreferences(collectionList);
                        // 顯示收藏成功的 Toast
                        Toast.makeText(Advertise.this, "收藏成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 商家已經存在於收藏列表中，顯示已收藏的 Toast
                        Toast.makeText(Advertise.this, "已收藏此店家", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return AdvertiseList.size();
        }

        // 檢查商家是否已經在收藏列表中
        private boolean isAlreadyCollected(HashMap<String, String> item) {
            for (HashMap<String, String> collectionItem : collectionList) {
                Log.d("Collection", "Checking item: " + collectionItem.get("title"));
                if (collectionItem.get("title").equals(item.get("title"))) {
                    Log.d("Collection", "Item already in collection");
                    return true; // 商家已經存在於收藏列表中
                }
            }
            Log.d("Collection", "Item not in collection");
            return false; // 商家尚未被收藏
        }

        // 將收藏列表保存到SharedPreferences的方法
        private void saveCollectionListToSharedPreferences(List<HashMap<String, String>> collectionList) {
            // 獲取SharedPreferences對象
            SharedPreferences preferences = getSharedPreferences("CollectionPrefs", MODE_PRIVATE);

            // 獲取SharedPreferences編輯器
            SharedPreferences.Editor editor = preferences.edit();

            // 將collectionList轉換為JSON字符串
            Gson gson = new Gson();
            String json = gson.toJson(collectionList);

            // 將JSON字符串保存到SharedPreferences
            editor.putString("collectionList", json);

            // 提交更改
            editor.apply();
        }


    }

    private List<HashMap<String, String>> handleReceivedJson(String ret) {
        // 創建一個列表來存儲HashMap
        List<HashMap<String, String>> serverDataList = new ArrayList<>();

        try {
            // 使用逗號作為分隔符拆分ret字符串
            String[] dataItems = ret.split(",");

            // 檢查拆分的數據是否至少有六個項目（以創建一個HashMap）
            if (dataItems.length >= 6) {

                // 遍歷數據項目，為每六個項目創建一個新的HashMap
                for (int i = 0; i < dataItems.length; i += 6) {
                    // 為每組六個數據項目創建一個新的HashMap
                    HashMap<String, String> dataMap = new HashMap<>();

                    // 將拆分的數據分配給HashMap中相應的鍵
                    dataMap.put("title", dataItems[i].trim());
                    dataMap.put("address", dataItems[i + 1].trim());
                    dataMap.put("productName", dataItems[i + 2].trim());
                    dataMap.put("productDetail", dataItems[i + 3].trim());
                    dataMap.put("storeImg_url", dataItems[i + 4].trim());
                    dataMap.put("couponImg_url", dataItems[i + 5].trim());

                    // 將HashMap添加到列表中
                    serverDataList.add(dataMap);
                }
            } else {
                Log.e("YourActivity", "從伺服器接收到的數據格式無效");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 返回HashMap列表
        return serverDataList;
    }

    // 更新 updateRecyclerViewData 方法
    private void updateRecyclerViewData(List<HashMap<String, String>> serverData) {
        // 清空廣告列表
        AdvertiseList.clear();

        // 將伺服器數據的所有元素添加到廣告列表中
        AdvertiseList.addAll(serverData);

        // 通知 Adapter 重新繪製列表中的所有項
        myAdapter.notifyDataSetChanged();
    }

    // 將數據保存到 SharedPreferences
    private void saveDataToSharedPreferences(List<HashMap<String, String>> data) {
        // 將數據轉換為 JSON 字符串
        Gson gson = new Gson();
        String jsonData = gson.toJson(data);

        // 將 JSON 字符串保存到 SharedPreferences
        SharedPreferences preferences = getSharedPreferences("AdvertisePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("serverData", jsonData);
        editor.apply();
    }

    // 從 SharedPreferences 中加載數據
    private void loadDataFromSharedPreferences() {
        // 獲取 SharedPreferences
        SharedPreferences preferences = getSharedPreferences("AdvertisePrefs", MODE_PRIVATE);

        // 從 SharedPreferences 中獲取 JSON 字符串
        String jsonData = preferences.getString("serverData", null);

        if (jsonData != null) {
            // 將 JSON 字符串轉換回 List<HashMap<String, String>>
            List<HashMap<String, String>> serverData = new Gson().fromJson(
                    jsonData,
                    new TypeToken<List<HashMap<String, String>>>() {}.getType()
            );

            // 更新 RecyclerView 數據
            updateRecyclerViewData(serverData);
        }
    }

    // 從SharedPreferences中加載收藏清單
    private List<HashMap<String, String>> loadCollectionListFromSharedPreferences(){
        // 使用 SharedPreferences
        SharedPreferences preferences = getSharedPreferences("CollectionPrefs", MODE_PRIVATE);
        // 從 SharedPreferences 中獲取 JSON 字符串
        String json = preferences.getString("collectionList", "");
        // 將 JSON 字符串轉換回 List<HashMap<String, String>>
        Gson gson = new Gson();
        Type type = new TypeToken<List<HashMap<String, String>>>() {}.getType();
        return gson.fromJson(json, type);
    }

}
