package com.example.petcare_store;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class check extends AppCompatActivity {

    private SQLiteDatabase db;
    private Cursor cursor;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        // 初始化資料庫
        AdDataBaseHelper dbHelper = new AdDataBaseHelper(this, "AD", null, 1, "AD");
        db = dbHelper.getReadableDatabase();

        // 查詢資料
        String[] columns = {"_id", "STORE_NAME", "ADDRESS", "PRODUCT_NAME", "PRODUCT_DETAIL", "IMAGE_ad", "IMAGE_promo"};
        cursor = db.query("AD", columns, null, null, null, null, null);

        // 顯示資料
        String[] fromColumns = {"STORE_NAME", "ADDRESS", "PRODUCT_NAME", "PRODUCT_DETAIL", "IMAGE_ad", "IMAGE_promo"};
        int[] toViews = {R.id.storeNameTextView, R.id.addressTextView, R.id.productNameTextView, R.id.productDetailTextView, R.id.imageViewAd, R.id.imageViewPromo};
        // 在onCreate中，創建Glide實例
        Glide.with(this);

        adapter = new SimpleCursorAdapter(this, R.layout.list_item, cursor, fromColumns, toViews, 0) {
            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                super.bindView(view, context, cursor);

                // 獲取文字
                String storeName = cursor.getString(cursor.getColumnIndexOrThrow("STORE_NAME"));
                String address = cursor.getString(cursor.getColumnIndexOrThrow("ADDRESS"));
                String productName = cursor.getString(cursor.getColumnIndexOrThrow("PRODUCT_NAME"));
                String productDetail = cursor.getString(cursor.getColumnIndexOrThrow("PRODUCT_DETAIL"));

                // 獲取圖片的URL
                String imageUrlAd = cursor.getString(cursor.getColumnIndexOrThrow("IMAGE_ad"));
                String imageUrlPromo = cursor.getString(cursor.getColumnIndexOrThrow("IMAGE_promo"));

                // 獲取ImageView的引用
                ImageView imageViewAd = view.findViewById(R.id.imageViewAd);
                ImageView imageViewPromo = view.findViewById(R.id.imageViewPromo);

                // 使用Glide載入網路圖片
                Glide.with(context)
                        .load(imageUrlAd)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .override(411, 220)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.e("Glide", "Load failed", e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d("Glide", "Resource is ready");
                                return false;
                            }
                        })
                        .into(imageViewAd);

                Glide.with(context)
                        .load(imageUrlPromo)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .override(396, 295)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.e("Glide", "Load failed", e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d("Glide", "Resource is ready");
                                return false;
                            }
                        })
                        .into(imageViewPromo);
                ((TextView) view.findViewById(R.id.storeNameTextView)).setText("店家名稱: " + storeName);
                ((TextView) view.findViewById(R.id.addressTextView)).setText("網址: " + address);
                ((TextView) view.findViewById(R.id.productNameTextView)).setText("產品名稱: " + productName);
                ((TextView) view.findViewById(R.id.productDetailTextView)).setText("產品描述: " + productDetail);
            }
        };

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        // 長按刪除
        registerForContextMenu(listView);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // 刪除選中項目
        if (item.getItemId() == R.id.deleteItem) {
            int deletedRows = db.delete("AD", "_id=?", new String[]{String.valueOf(info.id)});
            if (deletedRows > 0) {
                Toast.makeText(this, "資料已刪除", Toast.LENGTH_SHORT).show();
                // 重新查詢資料
                cursor = db.query("AD", null, null, null, null, null, null);
                adapter.changeCursor(cursor);
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 釋放資源
        cursor.close();
        db.close();
    }
}