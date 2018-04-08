/*
 * RequesterAddTaskActivity
 *
 *
 * Mar 19, 2018
 *
 * Copyright (c) 2018 Haotian Qi. CMPUT301, University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and condition of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise please contact me.
 */
package com.example.dada.View;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dada.Controller.TaskController;
import com.example.dada.Exception.TaskException;
import com.example.dada.Model.Locations;
import com.example.dada.Model.OnAsyncTaskCompleted;
import com.example.dada.Model.OnAsyncTaskFailure;
import com.example.dada.Model.Task.RequestedTask;
import com.example.dada.Model.Task.Task;
import com.example.dada.Model.User;
import com.example.dada.R;
import com.example.dada.Util.FileIOUtil;
import com.example.dada.Util.TaskUtil;



import java.io.File;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import im.delight.android.location.SimpleLocation;

/**
 * activity to handle interface of adding new task from user
 */

public class RequesterAddTaskActivity extends AppCompatActivity {
    private EditText titleText;
    private EditText descriptionText;
    private User requester;
    private static int RESULT_LOAD_IMAGE = 1;
    private static int RESULT_LOAD_LOC = 2;
    private Button doneButton;
    private Bitmap photo;
    private Button locationButton;
//    private Locations location;
    private List<Double> coordinates = new ArrayList<>();

    private ArrayList<Task> offlineRequesterList = new ArrayList<>();

    private TaskController taskController = new TaskController(
        new OnAsyncTaskCompleted() {
            @Override
            public void onTaskCompleted(Object o) {
                Task t = (Task) o;
                FileIOUtil.saveRequesterTaskInFile(t, getApplicationContext());
            }
        },
        new OnAsyncTaskFailure() {
            @Override
            public void onTaskFailed (Object o){
                Toast.makeText(getApplication(), "Device offline", Toast.LENGTH_SHORT).show();
                offlineRequesterList.add((Task) o);
                FileIOUtil.saveOfflineTaskInFile((Task) o, getApplicationContext());
            }
        });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requester_add_task);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        titleText = findViewById(R.id.editText_requester_add_task_title);
        descriptionText = findViewById(R.id.editText_requester_add_task_description);

        locationButton  = findViewById(R.id.addTaskLocationButtion);
        assert locationButton != null;
        locationButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                addLocation(v);
            }
        });

        doneButton = findViewById(R.id.newTask_done_button);
        assert doneButton != null;
        doneButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                addTask();
            }
        });

        // Get user profile
        requester = FileIOUtil.loadUserFromFile(getApplicationContext());

    }

    public void addTask() {
        String title = titleText.getText().toString();
        String description = descriptionText.getText().toString();


        boolean validTitle = !(title.isEmpty() || title.trim().isEmpty());
        boolean validDescription = !(description.isEmpty() || description.trim().isEmpty());

        if (!(validTitle && validDescription)) {
            Toast.makeText(this, "Task Title/Description is not valid.", Toast.LENGTH_SHORT).show();
        } else {
            if (title.length() > 30){
                Toast.makeText(this, "Max length of task title is 30.", Toast.LENGTH_SHORT).show();
            } else {
                if (description.length() > 300) {
                    Toast.makeText(this, "Max length of task description is 300.", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println(coordinates.toString());
                    Task task = new RequestedTask(title, description, requester.getUserName(), coordinates);
                    task.setID(UUID.randomUUID().toString());
                    taskController.createTask(task);
                    finish();
                }
            }
        }
    }


    public void addLocation(View view) {
        Intent intentRequesterAddTaskLoc = new Intent(getApplicationContext(), RequesterAddTaskLocationActivity.class);
        startActivityForResult(intentRequesterAddTaskLoc, RESULT_LOAD_LOC);
    }

    public void addImage(View view) {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            // https://blog.csdn.net/nupt123456789/article/details/7844076
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String path = cursor.getString(columnIndex);
            cursor.close();
            File file = new File(path);
            if (file.length() > 65536) {
                Toast.makeText(this, "Image file is larger than 64 KB.", Toast.LENGTH_SHORT);
                return;
            }
            if (file.length() == 0) {
                Toast.makeText(this, "File cannot be access or not exist.", Toast.LENGTH_SHORT);
                return;
            }
            try {
                photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton2);
                imageButton.setImageBitmap(photo);

            } catch (Exception e) {

            }
        }

        if (requestCode == RESULT_LOAD_LOC && resultCode == RESULT_OK) {
            try {
                String str = data.getStringExtra("coordinates");
                String s[] = str.split(",");
                Double lan = Double.parseDouble(s[0]);
                Double lon = Double.parseDouble(s[1]);


                coordinates.add(lon);
                coordinates.add(lan);
            } catch (Exception e){

            }

        }
    }
}
