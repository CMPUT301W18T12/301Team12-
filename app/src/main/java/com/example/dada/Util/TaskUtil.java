/* TaskUtil
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

import com.example.dada.Model.Task.NormalTask;
import com.example.dada.Model.Task.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

/**
 * Utility class to help pass task object through intent or update to the server
 */
public class TaskUtil {

    /**
     * change task to string
     * @param task Task
     * @return String
     */
    public static String serializer(Task task) {
        Gson gson = new Gson();
        return gson.toJson(task);
    }

    /**
     * change string to task
     * @param string task string
     * @return Task
     */
    public static Task deserializer(String string) {
        Gson gson = new Gson();
        return gson.fromJson(string, NormalTask.class);
    }

    /**
     * offline create task
     * @param task task
     * @return offline json
     */
    public static String generateOfflineTaskFileName(Task task) {
        return "offline-" + task.getID() + ".json";
    }

    /**
     * Task been accpted
     * @param task Task
     * @return task string
     */
    public static String generateAcceptedTaskFileName(Task task) {
        return "accepted-" + task.getID() + ".json";
    }

    /**
     * task request change
     * @param task Task
     * @return String
     */
    public static String generateRequesterTaskFileName(Task task) {
        return "requester-" + task.getID() + ".json";
    }

    /**
     * provider task list
     * @param task Task
     * @return String
     */
    public static String generateProviderTaskFileName(Task task) {
        return "provider-" + task.getID() + ".json";
    }

    /**
     * due with offline task list
     * @param context
     * @return offline list
     */
    public static ArrayList<String> getOfflineTaskList(Context context) {
        String[] fileList = context.fileList();
        ArrayList<String> offlineRequestFileList = new ArrayList<>();
        for (String f : fileList) {
            if (f != null && f.startsWith("offline-")) {
                offlineRequestFileList.add(f);
            }
        }
        return offlineRequestFileList;
    }

    /**
     * get requester list
     * @param context
     * @return list
     */
    public static ArrayList<String> getRequesterTaskList(Context context) {
        String[] fileList = context.fileList();
        ArrayList<String> offlineAcceptedRequestFileList = new ArrayList<>();
        for (String f : fileList) {
            if (f != null && f.startsWith("requester-")) {
                Log.i("Debug", f);
                offlineAcceptedRequestFileList.add(f);
            }
        }
        return offlineAcceptedRequestFileList;
    }

    /**
     * get provider list
     * @param context
     * @return list
     */
    public static ArrayList<String> getProviderTaskList(Context context) {
        String[] fileList = context.fileList();
        ArrayList<String> offlineAcceptedRequestFileList = new ArrayList<>();
        for (String f : fileList) {
            if (f != null && f.startsWith("provider-")) {
                Log.i("Debug", f);
                offlineAcceptedRequestFileList.add(f);
            }
        }
        return offlineAcceptedRequestFileList;
    }

    /**
     * get accepted list
     * @param context
     * @return
     */
    public static ArrayList<String> getAcceptedTaskList(Context context) {
        String[] fileList = context.fileList();
        ArrayList<String> offlineAcceptedRequestFileList = new ArrayList<>();
        for (String f : fileList) {
            if (f != null && f.startsWith("accepted-")) {
                Log.i("Debug", f);
                offlineAcceptedRequestFileList.add(f);
            }
        }
        return offlineAcceptedRequestFileList;
    }
}