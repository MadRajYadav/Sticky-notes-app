package com.example.stickynotesapp;

import static android.os.Build.VERSION.SDK_INT;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PICK_FILE_REQUEST_CODE = 1;
    private EditText editText, fileName;
    private TextView plus, minus;
    private  Button save, open, newBtn, createBtn;
    private String selectedFilePath;
    private ActivityResultLauncher<String> filePickerLauncher;
    float textSize = 20.0F;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        save = findViewById(R.id.savebutton);
        open = findViewById(R.id.openbutton);
        editText = findViewById(R.id.edittext);
        newBtn = findViewById(R.id.newbtn);
        createBtn = findViewById(R.id.createbtn);
        fileName = findViewById(R.id.filename);
        plus = findViewById(R.id.plus);
        minus = findViewById(R.id.minus);
        requestPermission();
        editText.setTextSize(textSize);


        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.linear_create_new_one).setVisibility(View.VISIBLE);
                editText.setVisibility(View.GONE);
            }
        });
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filePickerLauncher.launch("text/plain"); // Only select text (txt) files
            }
        });

        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {

                            selectedFilePath = result.getPath();
                            selectedFilePath = Environment.getExternalStorageDirectory()+"/"+selectedFilePath.substring(18);
                            editFile(selectedFilePath);

                        }
                    }
                }
        );


        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file = new File(Environment.getExternalStorageDirectory()+"/StickyNotes");
                if(!file.isDirectory()){
                    file.mkdir();
                }
                StringBuffer stringBuffer = new StringBuffer(fileName.getText());
                if(stringBuffer.indexOf(".")>-1){
                    Toast.makeText(MainActivity.this, "File name should not have dot(.). ", Toast.LENGTH_SHORT).show();
                }
                else if(stringBuffer.toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "File name can't be empty or null.", Toast.LENGTH_SHORT).show();
                }
                else if(Character.isDigit(stringBuffer.charAt(0))) {
                    Toast.makeText(MainActivity.this, "First later can't be digit", Toast.LENGTH_SHORT).show();
                }
                else {
                    selectedFilePath = Environment.getExternalStorageDirectory() + "/StickyNotes/" + fileName.getText() + ".txt";
                    CreateFile(selectedFilePath);
                }

            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedFilePath!=null) {
                    performSave(selectedFilePath);
                }else{
                    Toast.makeText(MainActivity.this, "First Create a new file or open a file to edit.", Toast.LENGTH_LONG).show();
                }

            }
        });


        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSize = textSize + 1F;
                editText.setTextSize(textSize);
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSize = textSize - 1F;
                editText.setTextSize(textSize);
            }
        });
    }
    private void editFile(String path) {
        requestPermission();
        try {
            File file = new File(path);

            BufferedReader br = new BufferedReader(new FileReader(file));

            StringBuilder content = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }

            br.close();

            String fileContent = content.toString();

            editText.setText(fileContent);
            editText.setVisibility(View.VISIBLE);
            findViewById(R.id.linear_create_new_one).setVisibility(View.GONE);

        } catch (IOException e) {
            requestPermission();
        }
    }





    private void performSave(String path) {
        requestPermission();
        // Code for saving the notes

            try {
                String content = editText.getText().toString();

                FileOutputStream fileOutputStream = new FileOutputStream(path);
                fileOutputStream.write(content.getBytes());
                fileOutputStream.close();

                Toast.makeText(this, "File saved successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("error", e.toString());
            }


    }

    public void CreateFile(String path){

            File file = new File(path);

            if (file.exists()) {
                Toast.makeText(this, "File already exists", Toast.LENGTH_LONG).show();
            } else {
                try {
                    boolean isFileCreated = file.createNewFile();
                    if (isFileCreated) {
                        Toast.makeText(this, "File created successfully", Toast.LENGTH_SHORT).show();
                        editText.setVisibility(View.VISIBLE);
                        editText.setText("");
                        findViewById(R.id.linear_create_new_one).setVisibility(View.GONE);
                    } else {
                        Toast.makeText(this, "Failed to create the file", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    requestPermission();
                }
            }
        }


    private void requestPermission() {

        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                //request for the permission
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }


    }
}
