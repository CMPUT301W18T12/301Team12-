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



    public customAdapter(Context context, int resource, ArrayList<Task> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String title = getItem(position).getTitle();
        String status = getItem(position).getStatus();



        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView taskTitle = (TextView) convertView.findViewById(R.id.taskTitle_requester_main);
        TextView taskStatus = (TextView) convertView.findViewById(R.id.taskStatus_requester_main);

        taskStatus.setTextColor(Color.parseColor("white"));
        if (status.equals("requested")){
            taskStatus.setBackgroundColor(Color.parseColor("red"));
        }
        else if (status.equals("bidded")){
            taskStatus.setBackgroundColor(Color.parseColor("blue"));
        }
        else if (status.equals("assigned")){
            taskStatus.setBackgroundColor(Color.parseColor("green"));
        }
        else if (status.equals("completed")){
            taskStatus.setBackgroundColor(Color.parseColor("cyan"));
        }
        else if (status.equals("done")){
            taskStatus.setBackgroundColor(Color.parseColor("darkgray"));
        }


//        taskStatus.setTextColor(Color.parseColor("#fe00fb"));
        taskTitle.setText(title);
        taskStatus.setText(status);

        return convertView;
    }
}
