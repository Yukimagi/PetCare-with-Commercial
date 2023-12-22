package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.commons.logging.Log;

public class ShowDbChanges implements Runnable {
    DatabaseReference ref;

    public void run() {
        FireBaseService fbs = null;
        try {
            fbs = new FireBaseService();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ref = fbs.getDb().getReference("/");
        ref.addValueEventListener(new ValueEventListener() {

            public void onDataChange(DataSnapshot dataSnapshot) {
                Object document = dataSnapshot.getValue();
                saveDataToJsonFile(dataSnapshot);
                // System.out.println(document);
            }

            public void onCancelled(DatabaseError error) {
                System.out.print("Error: " + error.getMessage());
            }
        });
    }
    
    private static void saveDataToJsonFile(DataSnapshot dataSnapshot) {
        // Get the data
        Object document = dataSnapshot.getValue();

        // Convert the data to a JSON string
        String jsonData = convertToJson(document);

        // Write the JSON data to a file
        try {
            Path outputPath = Paths.get("output.json");
            Files.write(outputPath, jsonData.getBytes(StandardCharsets.UTF_8));
            System.out.println("Data saved to JSON file successfully.");
        } catch (IOException e) {
            System.out.println("Error writing to JSON file: " + e.getMessage());
        }
    }

    private static String convertToJson(Object data) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(data);
        } catch (IOException e) {
            throw new RuntimeException("Error converting to JSON: " + e.getMessage());
        }
    }

    public void deleteChild(String key,String key2,String key3,String key4) {
        // 获取子集的 Reference
        DatabaseReference referenceToDelete = ref.child(key).child(key2).child(key3).child(key4);

        // 删除子集
        referenceToDelete.removeValue((error, ref) -> {
            if (error == null) {
                System.out.println("子集删除成功！");
            } else {
                System.err.println("子集删除失败：" + error.getMessage());
            }
        });
    }
    
    //多型
    public void setting(String key,String value){
        ref.child(key).setValueAsync(value);
    }
    public void setting(String key,String key2,String value){
        ref.child(key).child(key2).setValueAsync(value);
    }
    public void setting(String key,String key2,String key3,String value){
        ref.child(key).child(key2).child(key3).setValueAsync(value);
    }
    public void setting(String key,String key2,String key3,String key4,String value){
        ref.child(key).child(key2).child(key3).child(key4).setValueAsync(value);
    }
    public void setting(String key0,String key,String key2,String key3,String key4,String value){
        ref.child(key0).child(key).child(key2).child(key3).child(key4).setValueAsync(value);
    }

    public interface  ValueEventListenerCallback {
        void onDataChange(String value);
        void onCancelled(String errorMessage);
    }

    public void reading(String key, final ValueEventListenerCallback callback) {
        ref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                callback.onDataChange(value);
            }
            public void onCancelled(DatabaseError error) {
                callback.onCancelled("Error: " + error.getMessage());
            }
        });
    }
    public void reading4rsv(String key, final ValueEventListenerCallback callback) {
        ref.child("rsv").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                callback.onDataChange(value);
            }
            public void onCancelled(DatabaseError error) {
                callback.onCancelled("Error: " + error.getMessage());
            }
        });
    }
    public void check(String key0,String key,String key2,final String targetKey,final ValueEventListenerCallback callback) {
        ref.child(key0).child(key).child(key2).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(targetKey)) {
                    System.out.println("Key exists in the database.");
                    callback.onDataChange("y");
                }else if(snapshot.getChildrenCount() >= 20){
                    System.out.println("Too many rsv");
                    callback.onDataChange("yf");
                } else {
                    callback.onDataChange("n");
                    System.out.println("Key does not exist in the database.");
                }
            }
            public void onCancelled(DatabaseError error) {
                callback.onCancelled("Error: " + error.getMessage());
            }
        });
    }
    public void check2(String key0,final String targetKey,final ValueEventListenerCallback callback) {
        ref.child(key0).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                callback.onDataChange("n");
            }
            public void onCancelled(DatabaseError error) {
                callback.onCancelled("Error: " + error.getMessage());
            }
        });
    }
}
