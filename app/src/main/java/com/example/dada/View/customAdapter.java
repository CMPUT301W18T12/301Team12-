/*
 * customAdapter
 *
 *
 * April 9, 2018
 *
 * Copyright (c) 2018 Team 12. CMPUT301, University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and condition of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise please contact me.
 */

package com.example.dada.View;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dada.Controller.TaskController;
import com.example.dada.Controller.UserController;
import com.example.dada.Model.OnAsyncTaskCompleted;
import com.example.dada.Model.Task.Task;
import com.example.dada.Model.User;
import com.example.dada.R;
import com.example.dada.Util.FileIOUtil;
import com.example.dada.Util.TaskUtil;

import java.util.ArrayList;


public class customAdapter extends ArrayAdapter<Task> {
    private Context mContext;
    private int mResource;
    private int userType = 0;


    /**
     * Default constructor for custom adapter
     *
     * @param context
     * @param resource
     * @param objects
     */
    public customAdapter(Context context, int resource, ArrayList<Task> objects, int userType) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        this.userType = userType;
    }

    public customAdapter(Context context, int resource, ArrayList<Task> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get row needed info
        String title = getItem(position).getTitle();

        String status = getItem(position).getStatus();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView taskTitle = (TextView) convertView.findViewById(R.id.taskTitle_requester_main);
        TextView taskStatus = (TextView) convertView.findViewById(R.id.taskStatus_requester_main);

        if (getItem(position).getNewBid().equals("1") && userType==1 && getItem(position).getStatus().toLowerCase().equals("requested")) {
            taskTitle.setTextColor(Color.parseColor("#FF3333"));
        }

        taskStatus.setTextColor(Color.parseColor("white"));
        if (status.equals("requested")) {
            taskStatus.setBackgroundColor(Color.parseColor("#FF3333"));//red
        } else if (status.equals("bidded")) {
            taskStatus.setBackgroundColor(Color.parseColor("#33FFFF"));//blue
        } else if (status.equals("assigned")) {
            taskStatus.setBackgroundColor(Color.parseColor("#33FF33"));//green
        } else if (status.equals("done")) {
            taskStatus.setBackgroundColor(Color.parseColor("#3333FF"));//darker blue
        }


        taskTitle.setText(title);
        taskStatus.setText(status);

        convertView.setBackgroundColor(Color.parseColor("white"));

        return convertView;
    }

}