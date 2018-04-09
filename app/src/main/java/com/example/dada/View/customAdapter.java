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
        else if (status.equals("done")){
            taskStatus.setBackgroundColor(Color.parseColor("#3333FF"));//darker blue
        }


        taskTitle.setText(title);
        taskStatus.setText(status);

        convertView.setBackgroundColor(Color.parseColor("white"));

        return convertView;
    }

    public static class ProviderDetailAvitivity extends AppCompatActivity {

        /**
         * @param task is stand for the task
         * @param statusRequested just help to avoid spelling mistake
         * @param statusAssigned same
         * @param statusBidded same
         * @param statusDone same
         * @param providerName providerName that be selected(for use in innner class)
         * @param taskController save and load tha task
         */
        private Task task;
        private String statusRequested = "REQUESTED";
        private String statusAssigned = "ASSIGNED";
        private String statusBidded = "BIDDED";
        private String statusDone = "DONE";
        private String providerName;
        private TaskController taskController = new TaskController(new OnAsyncTaskCompleted() {
            @Override
            public void onTaskCompleted(Object o) {
                Task t = (Task) o;
                FileIOUtil.saveRequesterTaskInFile(t, getApplicationContext());
            }
        });
        private User requester;
        private User provider;
        /**
         private TaskController taskController = new TaskController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
        Task task = (Task) o;
        FileIOUtil.saveTaskInFile(task, "temp", getApplicationContext()); //    //^_^//
        }
        }, new OnAsyncTaskFailure() {
        @Override
        public void onTaskFailed(Object o) {
        Task task = (Task) o;
        FileIOUtil.saveOfflineTaskInFile(task, getApplicationContext());
        }
        });
         **/
        private UserController userController = new UserController(new OnAsyncTaskCompleted() {
            @Override
            public void onTaskCompleted(Object o) {
                User user = (User) o;
                FileIOUtil.saveUserInFile(user, getApplicationContext());
            }
        });


        /**
         * main function of requesterdetailclass; control the listview click action
         * @param savedInstanceState Bundle that tran objects
         */
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_provider_detail);
            //more intent part need                                                              //^_^//
            Intent intent = getIntent();
            task = TaskUtil.deserializer(intent.getStringExtra("Task"));

            setViews();

        }

        /**
         * set the view of the activity
         */

        private void setViews(){
            // set Toolbar
            Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
            toolbar.setTitle(task.getTitle());

            // Hidden the view that will not in requested page

            Button button = (Button)findViewById(R.id.buttonBid);
            if (button != null) {
                button.setVisibility(View.GONE);
            }
            EditText input = (EditText)findViewById(R.id.editTextInput);
            input.setVisibility(View.GONE);
            ImageView inputBack = (ImageView)findViewById(R.id.imageViewInput);
            inputBack.setVisibility(View.GONE);
            Log.i("ActivityStart----->", "1");

            input.setHint("Please in put the bid price");

            // Get from https://stackoverflow.com/questions/19452269/android-set-text-to-textview
            // Consider about 2 sides gravity                                                    //^_^//
            TextView textViewDescription = (TextView)findViewById(R.id.textViewDescription);
            textViewDescription.setText(task.getDescription());

            double lowestPrice = task.getLowestPrice();
            TextView textViewLowestPrice = (TextView)findViewById(R.id.textViewLowestPrice);
            textViewLowestPrice.setText("Lowest Price Right Now: $" + lowestPrice);
            //Should not allow it since bid cannot with price 0
            if (lowestPrice == 0 && statusRequested.equals(task.getStatus().toUpperCase()) ) {
                Log.i("MayBeError", "Lowest Price equal to 0 and status is not Request");
                textViewLowestPrice.setText("Lowest Price Right Now: $" + "0.00");
            }
            Log.i("ActivityStart----->", "2");

            TextView textViewStatus = (TextView)findViewById(R.id.textViewStatus);
            ImageView imageViewStatus = (ImageView)findViewById(R.id.imageViewStatus);
            ImageView imageView = (ImageView)findViewById(R.id.imageView);
            if (task.getImg() != null) {
                //imageView.setImageBitmap();
                //imageView.setImageBitmap(task.getImg());
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            } else {
                Toast.makeText(this, "Did not find task img. Replace by default", Toast.LENGTH_SHORT).show();
                imageView.setImageResource(R.drawable.temp_taskimg);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
            // set map button
            ImageButton imageButton = (ImageButton)findViewById(R.id.taskDetailMapBtn);
            imageButton.setImageResource(R.drawable.ic_launcher_foreground);
            imageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            // set requester info
            TextView textViewName = (TextView)findViewById(R.id.textViewName);
            TextView textViewPhone = (TextView)findViewById(R.id.textViewPhone);
            ImageView imageViewHead = (ImageView)findViewById(R.id.circleImageView);

            requester = userController.getUser(task.getRequesterUserName());

            textViewName.setText(requester.getUserName());
            textViewPhone.setText(requester.getPhone());
            if (requester.getProfile_photo() != null) {//^_^//
                // imageViewHead.setImageBitmap(requester.getProfile_photo());
            } else {
                Toast.makeText(this, "Did not find user image. Replace by default.", Toast.LENGTH_SHORT).show();
                imageViewHead.setImageResource(R.drawable.temp_head);
            }                         // temp


            textViewStatus.setText(task.getStatus().toUpperCase());
            if (task.getStatus().toUpperCase().equals(statusRequested)) {
                button.setVisibility(View.VISIBLE);
                input.setVisibility(View.VISIBLE);
                inputBack.setVisibility(View.VISIBLE);
            }
            if (task.getStatus().toUpperCase().equals(statusBidded)) {
                imageViewStatus.setColorFilter(Color.MAGENTA);
                button.setVisibility(View.VISIBLE);
                input.setVisibility(View.VISIBLE);
                inputBack.setVisibility(View.VISIBLE);
            }
            if (task.getStatus().toUpperCase().equals(statusAssigned)) {
                imageViewStatus.setColorFilter(Color.RED);
            }

            if (task.getStatus().toUpperCase().equals(statusDone)) {
                imageViewStatus.setColorFilter(Color.GREEN);
            }

            //wait to set location

        }

        public void onClickBid(View view) {
            EditText input = (EditText)findViewById(R.id.editTextInput);
            String value_str = input.getText().toString();
            if (value_str == null) {
                Toast.makeText(this, "Please add price.", Toast.LENGTH_SHORT).show();
                return;
            }

            double valueDouble = Double.parseDouble(value_str);

            /**
            ArrayList<ArrayList<String>> bidList = task.getBidList();

            if (task.getStatus().toUpperCase().equals(statusBidded)) {
                for (ArrayList<String> bided : bidList) {
                    if (bided.get(0).equals(FileIOUtil.loadUserFromFile(getApplicationContext()).getUserName())) {
                        bided.remove(1);
                        bided.add(value_str);
                        taskController.updateTask(task);
                        setViews();
                        finish();
                    }
                }
            }

            Log.i("debug----->","Not find in history");
            ArrayList<String> bid = new ArrayList<String>();
            bid.add(FileIOUtil.loadUserFromFile(getApplicationContext()).getUserName());
            bid.add(value_str);
            task.setStatus(statusBidded.toLowerCase());
            bidList.add(bid);
            task.setBidList(bidList);
            taskController.updateTask(task);
            **/
            try {
                Log.i("debug---->", FileIOUtil.loadUserFromFile(getApplicationContext()).);
                task.providerBidTask(FileIOUtil.loadUserFromFile(getApplicationContext()).getUserName(), valueDouble);
            } catch(Exception e) {
                Toast.makeText(this, "Cannot bidded test.", Toast.LENGTH_SHORT);
            }
            taskController.updateTask(task);
            setViews();
            finish();
        }
    }
}
