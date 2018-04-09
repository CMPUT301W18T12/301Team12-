/* FileIOUtil
 *
 * Version 1.0
 *
 * March 15, 2018
 *
 * Copyright (c) 2018 Team 12 CMPUT 301. University of Alberta - All Rights Reserved.
 * You may use distribute or modify this code under terms and condition of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of licence in this project. Otherwise please contact contact sfeng3@ualberta.ca.
 */

package com.example.dada.Util;

import android.content.Context;
import android.util.Log;

import com.example.dada.Constant;
import com.example.dada.Model.Task.NormalTask;
import com.example.dada.Model.Task.Task;
import com.example.dada.Model.User;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * The type File io util.
 */
public class FileIOUtil {
    /**
     * An utility method that save user profile locally
     *
     * @param user    the user object to be saved
     * @param context an android activity component
     */
    public static void saveUserInFile(User user, Context context) {
        try {
            Gson gson = new Gson();
            String jsonStr = gson.toJson(user);
            // write json string into corresponding file
            Log.i("Debug", jsonStr);
            FileOutputStream fos = context.openFileOutput(Constant.USER_PROFILE_FILENAME, 0);
            fos.write(jsonStr.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * An utility method that retrieve user profile locally
     *
     * @param context an android activity component
     * @return an User object
     */
    public static User loadUserFromFile(Context context) {
        User user = new User();
        try {
            Gson gson = new Gson();
            FileInputStream fis = context.openFileInput(Constant.USER_PROFILE_FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            user = gson.fromJson(in, User.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Save task in file.
     *
     * @param task    the requests
     * @param context the context
     */
    public static void saveOfflineTaskInFile(Task task, Context context) {
        try {
            Gson gson = new Gson();
            String jsonStr = gson.toJson(task);
            String fileName = TaskUtil.generateOfflineTaskFileName(task);
            FileOutputStream fos = context.openFileOutput(fileName, 0);
            if (fos == null) {
                Log.i("Debug", "null fos in save request");
            }
            try {
                fos.write(jsonStr.getBytes());
            } catch (NullPointerException e) {
                Log.i("Debug", "getBytes() threw null pointer exception");
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * save Task in the file
     * @param task task
     * @param fileName the filename of saving file
     * @param context
     */
    public static void saveTaskInFile(Task task, String fileName, Context context) {
        try {
            Gson gson = new Gson();
            String jsonStr = gson.toJson(task);
            FileOutputStream fos = context.openFileOutput(fileName, 0);
            if (fos == null) {
                Log.i("Debug", "null fos in save request");
            }
            try {
                fos.write(jsonStr.getBytes());
            } catch (NullPointerException e) {
                Log.i("Debug", "getBytes() threw null pointer exception");
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * save requester task
     * @param task task
     * @param context
     */
    public static void saveRequesterTaskInFile(Task task, Context context) {
        try {
            Gson gson = new Gson();
            String jsonStr = gson.toJson(task);
            String fileName = TaskUtil.generateRequesterTaskFileName(task);
            FileOutputStream fos = context.openFileOutput(fileName, 0);
            if (fos == null) {
                Log.i("Debug", "null fos in save request");
            }
            try {
                fos.write(jsonStr.getBytes());
            } catch (NullPointerException e) {
                Log.i("Debug", "getBytes() threw null pointer exception");
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Load task from file array list.
     *
     * @param context the context
     * @return the array list of requests
     */
    public static ArrayList<Task> loadTaskFromFile(Context context, ArrayList<String> fileList) {
        ArrayList<Task> taskList = new ArrayList<>();
        Gson gson = new Gson();
        for (String f : fileList) {
            FileInputStream fis = null;
            try {
                fis = context.openFileInput(f);
                BufferedReader in = new BufferedReader(new InputStreamReader(fis));
                NormalTask req = gson.fromJson(in, NormalTask.class);
                taskList.add(req);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return taskList;
    }

    /**
     * load one task
     * @param fileName file load from
     * @param context
     * @return task
     */
    public static Task loadSingleTaskFromFile(String fileName, Context context) {
        Task request = new NormalTask();
        FileInputStream fis = null;
        Gson gson = new Gson();
        try {
            fis = context.openFileInput(fileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            request = gson.fromJson(in, NormalTask.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return request;
    }
}

