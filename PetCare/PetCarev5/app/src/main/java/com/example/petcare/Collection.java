package com.example.petcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class Collection extends AppCompatActivity {
    private RecyclerView collectRecyclerView;  // RecyclerView 用於顯示收藏清單的視圖
    private RecyclerView.LayoutManager collectlayoutManager;  // RecyclerView 的佈局管理器
    private CollectionAdapter collectionAdapter;  // 用於填充 RecyclerView 的適配器
    private List<HashMap<String, String>> collectionList;  // 存儲收藏清單的資料結構

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        // 返回上一頁
        Button collect_return = (Button) findViewById(R.id.collect_return);
        collect_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 從存儲中加載收藏清單
        collectionList = loadCollectionListFromSharedPreferences();

        // 初始化 RecyclerView
        collectRecyclerView = findViewById(R.id.recyclerViewCollect);
        collectlayoutManager = new LinearLayoutManager(this);
        collectRecyclerView.setLayoutManager(collectlayoutManager);

        // 使用收藏清單設置適配器
        collectionAdapter = new CollectionAdapter(collectionList);
        collectRecyclerView.setAdapter(collectionAdapter);
    }

    private class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {
        private List<HashMap<String, String>> collectionData;

        public CollectionAdapter(List<HashMap<String, String>> collectionData) {
            this.collectionData = collectionData;
        }
        // 收藏項目的 ViewHolder 類別
        class CollectionViewHolder extends RecyclerView.ViewHolder {
            public View itemView;           // 項目的視圖
            public TextView title, address; // 商家標題和地址的 TextView
            public ImageView storeIMG, couponIMG; // 商家和優惠券的 ImageView
            public Button deleteButton;      // 刪除按鈕

            // CollectionViewHolder 的構造函數，初始化視圖中的元素
            public CollectionViewHolder(View v) {
                super(v);
                itemView = v;

                // 初始化元素
                title = itemView.findViewById(R.id.collection_title);
                address = itemView.findViewById(R.id.collection_address);
                storeIMG = itemView.findViewById(R.id.storeIMG);
                couponIMG = itemView.findViewById(R.id.couponIMG);
                deleteButton = itemView.findViewById(R.id.deleteButton);
            }
        }


        @NonNull
        @Override
        public CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 創建一個新的 view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.collectitem, parent, false);
            return new CollectionViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull CollectionViewHolder holder, int position) {
            // 使用收藏清單中的資料填充
            holder.title.setText(collectionData.get(position).get("title"));
            holder.address.setText(collectionData.get(position).get("address"));

            // 使用Glide載入商家照片
            Glide.with(Collection.this)
                    .load(collectionData.get(position).get("storeImg_url"))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.storeIMG);

            // 使用Glide載入商家照片
            Glide.with(Collection.this)
                    .load(collectionData.get(position).get("couponImg_url"))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.couponIMG);

            // 設置刪除按鈕的點擊監聽器
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 使用 holder.getAdapterPosition() 獲取當前位置
                    int clickedPosition = holder.getAdapterPosition();
                    // 檢查位置是否有效
                    if (clickedPosition != RecyclerView.NO_POSITION) {
                        // 使用檢索到的位置處理刪除按鈕的點擊
                        deleteItem(clickedPosition);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return collectionData.size();
        }

        // 從收藏清單中刪除項目的方法
        private void deleteItem(int position) {
            // 從收藏清單中移除項目
            collectionData.remove(position);

            // 儲存更新後的收藏清單
            saveCollectionList();

            // 通知適配器數據集已更改
            notifyDataSetChanged();
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

    //  將收藏清單保存到SharedPreferences
    private void saveCollectionList() {
        SharedPreferences preferences = getSharedPreferences("CollectionPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // 將收藏清單轉換為JSON字符串
        Gson gson = new Gson();
        String json = gson.toJson(collectionList);

        // 將JSON字符串保存到SharedPreferences
        editor.putString("collectionList", json);
        editor.apply();
    }
}
