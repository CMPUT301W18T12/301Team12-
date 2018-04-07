package com.example.dada.View;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.dada.Model.Task.Task;
import com.example.dada.R;
import java.util.ArrayList;


public class customAdapter extends ArrayAdapter<Task>{
    private Context mContext;
    private int mResource;


    /**
     * Default constructor for custom adapter
     * @param context
     * @param resource
     * @param objects
     */
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

        taskStatus.setTextColor(Color.parseColor("white"));
        if (status.equals("requested")){
            taskStatus.setBackgroundColor(Color.parseColor("#FF3333"));//red
        }
        else if (status.equals("bidded")){
            taskStatus.setBackgroundColor(Color.parseColor("#33FFFF"));//blue
        }
        else if (status.equals("assigned")){
            taskStatus.setBackgroundColor(Color.parseColor("#33FF33"));//green
        }
        else if (status.equals("completed")){
            taskStatus.setBackgroundColor(Color.parseColor("#3333FF"));//darker blue
        }
        else if (status.equals("done")){
            taskStatus.setBackgroundColor(Color.parseColor("#A0A0A0"));//dark
        }


        taskTitle.setText(title);
        taskStatus.setText(status);

        convertView.setBackgroundColor(Color.parseColor("white"));

        return convertView;
    }
}
