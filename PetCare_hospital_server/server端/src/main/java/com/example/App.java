package com.example;

import com.google.firebase.database.*;

import java.util.Hashtable;
import java.net.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class App {
    private static ServerSocket SSocket;
    private static int port;
    private Hashtable ht = new Hashtable();
    Socket socket;

    private static String formatMonth(String monthString) {
        return String.format("%02d", Integer.parseInt(monthString));
    }

    public App(ShowDbChanges showDbChanges) throws IOException {
        try {
            SSocket = new ServerSocket(port);
            System.out.println("Server created.");
            System.out.println("waiting for client to connect...");
        
            while (true) {
                socket = SSocket.accept();
                System.out.println("connected from Client " + socket.getInetAddress().getHostAddress());
                DataOutputStream outstream = new DataOutputStream(socket.getOutputStream());
                ht.put(socket, outstream);
                Thread thread = new Thread(new ServerThread(socket, ht,showDbChanges));
                thread.start();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {       
        ShowDbChanges showDbChanges = new ShowDbChanges();
        
        Thread t=new Thread(showDbChanges);
        t.run();

        //wait 10 second
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ///////////read message
        // showDbChanges.reading4rsv("2023-12-18", new ShowDbChanges.ValueEventListenerCallback() {
        //     @Override
        //     public void onDataChange(String value) {
        //         System.out.println(value);
        //     }
        //     @Override
        //     public void onCancelled(String errorMessage) {
        //         System.out.println(errorMessage);
        //     }
        // });
        int i = 0;
        //ui start
        MainFrame.main(args,showDbChanges);
        

        //start server
        port=8888;
        App ServerStart=new App(showDbChanges);
        
        //main thread stay for 100sec
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        //Over
        System.out.println("Over");
    }
}
