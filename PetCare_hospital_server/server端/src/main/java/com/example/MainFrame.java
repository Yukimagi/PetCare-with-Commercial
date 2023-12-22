package com.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.ArrayList;

public class MainFrame {
    private static JFrame frame;
    private static JPanel cardPanel;
    private static CardLayout cardLayout;
    static ShowDbChanges s;

    public static void main(String[] args,ShowDbChanges Object1) {
        s = Object1;
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Main Page");
            frame.setSize(500, 450);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            BufferedImage originalImage = null;
            try {
                originalImage = ImageIO.read(new File("cat.png")); // Replace with your image path
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Resize the image
            int newWidth = 250; // Set the desired width
            int newHeight = 300; // Set the desired height
            Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            // Display the scaled image in a JLabel
            if (originalImage != null) {
                JLabel imageLabel = new JLabel(scaledIcon);
                frame.add(imageLabel, BorderLayout.NORTH);
            }

            // Create card layout panel
            cardPanel = new JPanel();
            cardLayout = new CardLayout();
            cardPanel.setLayout(cardLayout);

            // Create main page panel
            JPanel mainPage = new JPanel(new BorderLayout());
            
            // JButton nextPageButton = new JButton("Next Page");
            JButton leftButton = new JButton("看診回饋系統");
            JButton rightButton = new JButton("預約排程表");
            JButton rightrightButton = new JButton("健康紀錄表");
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(leftButton);
            buttonPanel.add(rightButton);
            buttonPanel.add(rightrightButton);

            JLabel mainContentLabel = new JLabel("醫院端系統");
            mainContentLabel.setFont(new Font("微軟正黑體", Font.BOLD, 30));
            mainContentLabel.setHorizontalAlignment(SwingConstants.CENTER);

            mainPage.add(mainContentLabel, BorderLayout.CENTER);
            mainPage.add(buttonPanel, BorderLayout.SOUTH);

            // Create second page panel
            JPanel secondPage = new JPanel(new BorderLayout());
            JButton backToMainButton = new JButton("回到主頁");
            secondPage.add(new JLabel("預約排程查詢"), BorderLayout.NORTH);
            secondPage.add(backToMainButton, BorderLayout.SOUTH);

            // Create third page panel
            JPanel thirdPage = new JPanel(new BorderLayout());
            JButton backToMainButton2 = new JButton("回到主頁");
            thirdPage.add(new JLabel("回饋"), BorderLayout.NORTH);
            thirdPage.add(backToMainButton2, BorderLayout.SOUTH);

            // Create third page panel
            JPanel fouthPage = new JPanel(new BorderLayout());
            JButton backToMainButton3 = new JButton("回到主頁");
            fouthPage.add(new JLabel("紀錄"), BorderLayout.NORTH);
            fouthPage.add(backToMainButton3, BorderLayout.SOUTH);

            // Add action listeners
            rightButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(cardPanel, "SecondPage");
                    // Call the method to initialize DatePickerApp content
                    initializeDatePickerApp(secondPage,0);
                }
            });

            leftButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(cardPanel, "ThirdPage");
                    initializeDatePickerApp(thirdPage,1);
                }
            });

            rightrightButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(cardPanel, "FouthPage");
                    // Call the method to initialize DatePickerApp content
                    initializeIDPickerApp(fouthPage);
                }
            });

            backToMainButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(cardPanel, "MainPage");
                }
            });

            backToMainButton2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(cardPanel, "MainPage");
                }
            });

            backToMainButton3.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(cardPanel, "MainPage");
                }
            });

            // Add pages to card layout
            cardPanel.add(mainPage, "MainPage");
            cardPanel.add(secondPage, "SecondPage");
            cardPanel.add(thirdPage, "ThirdPage");
            cardPanel.add(fouthPage, "FouthPage");
            // Show the main page initially
            cardLayout.show(cardPanel, "MainPage");

            // Add card panel to the frame
            frame.add(cardPanel);

            frame.setVisible(true);
        });
    }

    private static void put(String date,String id,String comment,String medicine,String retur){
        System.out.println(date);
        System.out.println(id);
        System.out.println(comment);
        System.out.println(medicine);
        System.out.println(retur);
        s.setting("rec",id,date,"comment",comment);
        s.setting("rec",id,date,"medicine",medicine);
        s.setting("rec",id,date,"return",retur);
    }

    
    private static void fetchDataFromFirebase(DefaultTableModel tableModel,String targetDate) {
        tableModel.setRowCount(0);

        try {
            // Read and parse the JSON file
            Path jsonFilePath = Paths.get("output.json");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonFilePath.toFile());

            JsonNode targetNode = rootNode.path("rsv").path(targetDate);

            // Convert to a list structure
            List<List<String>> resultList = convertNodeToLists(targetNode);

            // Print the result
            // System.out.println(resultList);
            for (List<String> rowData : resultList) {
                Vector<Object> rowVector = new Vector<>(rowData);
                rowVector.remove(4);
                rowVector.remove(2);
                tableModel.addRow(rowVector);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> fetchDataFromFirebase4HealthList() {
        ArrayList<String> healthList = new ArrayList<>();
        try {
            // Read and parse the JSON file
            Path jsonFilePath = Paths.get("output.json");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonFilePath.toFile());

            Iterator<Map.Entry<String, JsonNode>> fields = rootNode.path("health").fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                healthList.add(field.getKey());
                // System.out.println("Field Name: " + field.getKey());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return healthList;
    }

    private static void fetchDataFromFirebase2(DefaultTableModel tableModel,DefaultTableModel tableModel2,String targetID) {
        tableModel.setRowCount(0);
        tableModel2.setRowCount(0);

        try {
            // Read and parse the JSON file
            Path jsonFilePath = Paths.get("output.json");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonFilePath.toFile());

            JsonNode targetNode = rootNode.path("health").path(targetID);
            JsonNode targetNode2 = rootNode.path("health").path(targetID);

            // Iterator<Map.Entry<String, JsonNode>> fields = rootNode.path("health").fields();
            // while (fields.hasNext()) {
            //     Map.Entry<String, JsonNode> field = fields.next();
            //     System.out.println("Field Name: " + field.getKey());
            //     System.out.println("Field Value: " + field.getValue());
            // }

            // Convert to a list structure
            List<List<String>> resultList = convertNodeToLists(targetNode);
            List<List<String>> resultList2 = convertNodeToLists(targetNode2);

            // Print the result
            // System.out.println(resultList);
            // System.out.println(resultList2);
            List<String> tempList = new ArrayList<>();

            List<String> rowData = resultList.get(0);
            rowData.remove(0);
            for(int i = 0; i < rowData.size();i+=2){
                String tempString = rowData.get(i).substring(1);
                int number = Integer.parseInt(tempString);
                number += 1;
                tempString = "第"+number+"筆記錄";
                tempList.add(tempString);
                tempList.add(rowData.get(i+1));
                Vector<Object> rowVector = new Vector<>(tempList);
                tableModel.addRow(rowVector);
                tempList.clear();
            }

            rowData = resultList.get(1);
            rowData.remove(0);
            for(int i = 0; i < rowData.size();i+=2){
                tempList.add(rowData.get(i));
                tempList.add(rowData.get(i+1));
                Vector<Object> rowVector = new Vector<>(tempList);
                tableModel2.addRow(rowVector);
                tempList.clear();
            }
                // Vector<Object> rowVector = new Vector<>(rowData);
                // rowVector.remove(4);
                // rowVector.remove(2);
                // tableModel.addRow(rowVector);
            // for (List<String> rowData : resultList2) {
            //     rowData.remove(0);
            //     Vector<Object> rowVector = new Vector<>(rowData);
            //     // rowVector.remove(4);
            //     // rowVector.remove(2);
            //     tableModel2.addRow(rowVector);
            // }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<List<String>> convertNodeToLists(JsonNode node) {
        List<List<String>> resultList = new ArrayList<>();

        if (node != null && node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                List<String> currentList = new ArrayList<>();
                currentList.add(entry.getKey());

                JsonNode nestedNode = entry.getValue();
                processNestedNode(currentList, nestedNode);

                resultList.add(currentList);
            }
        }
        return resultList;
    }

    private static void processNestedNode(List<String> currentList, JsonNode node) {
        if (node != null && node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
    
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                currentList.add(entry.getKey());
    
                JsonNode deeperNode = entry.getValue();
                if (deeperNode.isArray()) {
                    // System.out.print(currentList);
                    processArrayNode(currentList, deeperNode);
                } else if (deeperNode.isObject()) {
                    processNestedNode(currentList, deeperNode);
                } else {
                    currentList.add(deeperNode.asText());
                }
            }
        }
    }
    
    private static void processArrayNode(List<String> currentList, JsonNode arrayNode) {
        if (arrayNode.isArray()) {
            Iterator<JsonNode> elements = arrayNode.elements();
    
            while (elements.hasNext()) {
                JsonNode element = elements.next();
                if (element.isObject()) {
                    processNestedNode(currentList, element);
                } else {
                    currentList.add(element.asText());
                }
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    private static void openFeedbackWindow(Container parentContainer,String date) {
        JFrame feedbackFrame = new JFrame("Feedback Window");
        feedbackFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 創建子窗口內容
        JPanel panel = new JPanel(new GridLayout(6, 2));

        JLabel dateLabel = new JLabel("Date:");
        JTextField dateTextField = new JTextField();
        dateTextField.setEditable(false); // 日期不可編輯

        JLabel IDLabel = new JLabel("ID:");
        JTextField IDTextField = new JTextField();

        JLabel commentLabel = new JLabel("comment:");
        JTextField commentTextField = new JTextField();

        JLabel medicineLabel = new JLabel("medicine:");
        JTextField medicineTextField = new JTextField();

        JLabel returnLabel = new JLabel("return:");
        JTextField returnTextField = new JTextField();

        JButton submitButton = new JButton("Submit");

        panel.add(dateLabel);
        panel.add(dateTextField);
        panel.add(IDLabel);
        panel.add(IDTextField);
        panel.add(commentLabel);
        panel.add(commentTextField);
        panel.add(medicineLabel);
        panel.add(medicineTextField);
        panel.add(returnLabel);
        panel.add(returnTextField);
        panel.add(submitButton);

        feedbackFrame.getContentPane().add(panel);

        // 設定日期初始值
        dateTextField.setText(date);

        // 設定按鈕的事件處理
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 在這裡處理提交按鈕的邏輯
                // 可以從 IDTextField、commentTextField、feedbackTextField 和 feedbackTextField2 中獲取數據
                String date = dateTextField.getText();
                String ID = IDTextField.getText();
                String comment = commentTextField.getText();
                String medicine = medicineTextField.getText();
                String retur = returnTextField.getText();
                System.out.println("Date: " + date);
                System.out.println("ID: " + ID);
                System.out.println("comment: " + comment);
                System.out.println("medicine: " + medicine);
                System.out.println("return: " + retur);
                put(date,ID,comment,medicine,retur);

                // 關閉子窗口
                feedbackFrame.dispose();
            }
        });

        feedbackFrame.setSize(300, 250);
        feedbackFrame.setLocationRelativeTo(parentContainer);
        feedbackFrame.setVisible(true);
    }

    // Method to initialize DatePickerApp content in a panel
    private static void initializeDatePickerApp(Container container,int mode) {
        // DatePickerApp code goes here
        JPanel panel = new JPanel(new GridBagLayout());


            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;

            JButton toggleButton = new JButton("確認");

            gbc.gridy++;
            JPanel comboBoxPanel = new JPanel(new FlowLayout());
            String[] years = {"Select Year", "2023", "2024", "2025"};
            String[] months = {"Select Month", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
            String[] days = {"Select Day", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
            JComboBox<String> yearComboBox = new JComboBox<>(years);
            JComboBox<String> monthComboBox = new JComboBox<>(months);
            JComboBox<String> dayComboBox = new JComboBox<>(days);
            comboBoxPanel.add(yearComboBox);
            comboBoxPanel.add(monthComboBox);
            comboBoxPanel.add(dayComboBox);
            comboBoxPanel.add(toggleButton);
            panel.add(comboBoxPanel, gbc);

            gbc.gridy++;
            JLabel DateLabel = new JLabel("Selected Day: ");
            panel.add(DateLabel, gbc);

            toggleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) { 
                    // System.out.println("pressed");
                    int selectedYearIndex = yearComboBox.getSelectedIndex();
                    int selectedMonthIndex = monthComboBox.getSelectedIndex();
                    int selectedDayIndex = dayComboBox.getSelectedIndex();

                    if (selectedYearIndex != 0 && selectedMonthIndex != 0 && selectedDayIndex != 0 && mode == 0){
                        String formattedMonth = formatMonth(monthComboBox.getSelectedIndex() + "");
                        String formattedDay1 = formatMonth(dayComboBox.getSelectedIndex() + "");
                        String checkDate = yearComboBox.getSelectedItem().toString() + "-" + formattedMonth + "-" + formattedDay1;
                        DateLabel.setText(checkDate);
                        JLabel dateLabelInTableFrame = new JLabel("Selected Day: " + checkDate);

                        JFrame tableFrame = new JFrame("Schedule");
                        JTable table = new JTable();
                        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"時段", "寵物ID", "寵物名字", "備註"}, 0);
                        table.setModel(tableModel);

                        JButton fetchDataButton = new JButton("重新整理");
                        fetchDataButton.addActionListener(event -> {
                            fetchDataFromFirebase(tableModel,checkDate);
                        });

                        JPanel controlPanel = new JPanel();
                        controlPanel.add(fetchDataButton);

                        JScrollPane scrollPane = new JScrollPane(table);

                        tableFrame.setLayout(new BorderLayout());
                        tableFrame.add(controlPanel, BorderLayout.NORTH);
                        tableFrame.add(scrollPane, BorderLayout.CENTER);
                        tableFrame.add(dateLabelInTableFrame, BorderLayout.SOUTH);
                        tableFrame.setSize(600, 400);
                        tableFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                        tableFrame.setVisible(true);
                        // String formattedMonth = formatMonth(monthComboBox.getSelectedIndex() + "");
                        // String formattedDay1 = formatMonth(dayComboBox.getSelectedIndex() + "");
                        // String checkDate = yearComboBox.getSelectedItem().toString() + "-" + formattedMonth + "-" + formattedDay1;
                        // DateLabel.setText(checkDate);

                        // JFrame tableFrame = new JFrame("Schedule");
                        // JTable table = new JTable(new Object[][]{}, new Object[]{"Date","Perid","Pet ID","Pet ID","Comment"});
                        // JLabel dateLabelInTableFrame = new JLabel("Selected Day: " + checkDate);
                        
                        // JPanel tablePanel = new JPanel(new BorderLayout());
                        // tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
                        // tablePanel.add(dateLabelInTableFrame, BorderLayout.SOUTH);
                        
                        // tableFrame.add(tablePanel);
                        // tableFrame.setSize(400, 300);
                        // tableFrame.setVisible(true);
                        
                    }else if(selectedYearIndex != 0 && selectedMonthIndex != 0 && selectedDayIndex != 0 && mode == 1){
                        String formattedMonth = formatMonth(monthComboBox.getSelectedIndex() + "");
                        String formattedDay1 = formatMonth(dayComboBox.getSelectedIndex() + "");
                        String checkDate = yearComboBox.getSelectedItem().toString() + "-" + formattedMonth + "-" + formattedDay1;
                        openFeedbackWindow(container,checkDate);
                    }
                }
            });

        // Add the DatePickerApp content to the provided container
        container.add(panel, BorderLayout.CENTER);
    }

    private static void initializeIDPickerApp(Container container) {
        ArrayList<String> healthList = new ArrayList<>();
        healthList = fetchDataFromFirebase4HealthList();
        Collections.sort(healthList);
        healthList.add(0,"Select ID");
        // System.out.println(healthList);
        // DatePickerApp code goes here
        JPanel panel = new JPanel(new GridBagLayout());


            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;

            JButton toggleButton = new JButton("確認");

            gbc.gridy++;
            JPanel comboBoxPanel = new JPanel(new FlowLayout());
            String[] IDs = healthList.toArray(new String[0]);
            
            JComboBox<String> IDComboBox = new JComboBox<>(IDs);
            comboBoxPanel.add(IDComboBox);
            comboBoxPanel.add(toggleButton);
            panel.add(comboBoxPanel, gbc);

            gbc.gridy++;
            JLabel IDLabel = new JLabel("Selected ID: ");
            panel.add(IDLabel, gbc);

            toggleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) { 
                    int selectedYearIndex = IDComboBox.getSelectedIndex();

                    if (selectedYearIndex != 0){

                        String checkID = IDComboBox.getSelectedItem().toString();
                        IDLabel.setText(checkID);
                        JLabel IDLabelInTableFrame = new JLabel("Selected ID: " + checkID);

                        JFrame tableFrame = new JFrame("健康紀錄");

                        JTable table = new JTable();
                        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"紀錄", "重量"}, 0);
                        table.setModel(tableModel);

                        JTable table2 = new JTable();
                        DefaultTableModel tableModel2 = new DefaultTableModel(new Object[]{"藥品", "劑量"}, 0);
                        table2.setModel(tableModel2);

                        JButton fetchDataButton = new JButton("重新整理");
                        fetchDataButton.addActionListener(event -> {
                            fetchDataFromFirebase2(tableModel,tableModel2,checkID);
                        });

                        JPanel controlPanel = new JPanel();
                        controlPanel.add(fetchDataButton);
                        table.setPreferredScrollableViewportSize(new Dimension(250, 300));
                        table2.setPreferredScrollableViewportSize(new Dimension(250, 300));

                        JScrollPane scrollPane = new JScrollPane(table);
                        JScrollPane scrollPane2 = new JScrollPane(table2);
                        tableFrame.setLayout(new BorderLayout());
                        tableFrame.add(controlPanel, BorderLayout.NORTH);
                        tableFrame.add(scrollPane, BorderLayout.WEST);
                        tableFrame.add(scrollPane2, BorderLayout.EAST);
                        tableFrame.add(IDLabelInTableFrame, BorderLayout.SOUTH);
                        tableFrame.setSize(550, 400);
                        tableFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                        tableFrame.setVisible(true);                        
                    }
                }
            });

        // Add the DatePickerApp content to the provided container
        container.add(panel, BorderLayout.CENTER);
    }

    private static String formatMonth(String monthString) {
        return String.format("%02d", Integer.parseInt(monthString));
    }
}