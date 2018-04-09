package com.example.dada.View;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

public class providerDetailActivity extends AppCompatActivity {

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
        providerName = intent.getStringExtra("Name");
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

        double lowestPrice = 0.0;
        lowestPrice = task.getLowestPrice();
        TextView textViewLowestPrice = (TextView)findViewById(R.id.textViewLowestPrice);
        textViewLowestPrice.setText("Lowest Price Right Now: $" + lowestPrice);
        //Should not allow it since bid cannot with price 0
        if (lowestPrice == 0 && statusRequested.equals(task.getStatus().toUpperCase()) ) {
            Log.i("MayBeError", "Lowest Price equal to 0 and status is not Request");
            textViewLowestPrice.setText("Lowest Price Right Now: $" + "0.00");
        }

        // check the provider bidded before or not

        ArrayList<ArrayList<String>> bids = task.getBidList();
        for (ArrayList<String> bid : bids) {
            if (bid.get(0).equals(providerName)) {
                if (bid.get(1).equals(String.valueOf(lowestPrice))){
                    input.setHint("Your bid is the lowest.");
                    input.setHintTextColor(Color.parseColor("#ca7a2c"));
                } else {
                    input.setHint("Your bid is " + bid.get(1));
                    input.setHintTextColor(Color.parseColor("#EEA9A9"));
                }
            }
        }


        TextView textViewStatus = (TextView)findViewById(R.id.textViewStatus);
        ImageView imageViewStatus = (ImageView)findViewById(R.id.imageViewStatus);
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        if (task.getImg() != null) {
            //imageView.setImageBitmap();
            imageView.setImageBitmap(task.getImg());
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
        assert imageButton != null;
        imageButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if (task.getCoordinatesString() != null) {
                    String coordinates = task.getCoordinatesString();
                    Intent intentDetailMap = new Intent(getApplicationContext(), ProviderDetailMapActivity.class);
                    intentDetailMap.putExtra("coordinates", coordinates);

                    startActivity(intentDetailMap);
                } else {
                    Toast.makeText(getApplicationContext(), "Requester did not set location.", Toast.LENGTH_SHORT).show();
                }
            }
        });



        // set requester info
        TextView textViewName = (TextView)findViewById(R.id.textViewName);
        TextView textViewPhone = (TextView)findViewById(R.id.textViewPhone);
        ImageView imageViewHead = (ImageView)findViewById(R.id.circleImageView);

        textViewName.setText(task.getRequesterUserName());



        requester = userController.getUser(task.getRequesterUserName());
        if (requester != null) {
            textViewName.setText(requester.getUserName());
            textViewPhone.setText(requester.getPhone());

            if (requester.getProfile_photo() != null) {//^_^//
                imageViewHead.setImageBitmap(requester.getProfile_photo());
            } else {
                Toast.makeText(this, "Did not find user image. Replace by default.", Toast.LENGTH_SHORT).show();
                imageViewHead.setImageResource(R.drawable.temp_head);
            }
        }// temp



        textViewStatus.setText(task.getStatus().toUpperCase());
        if (task.getStatus().toUpperCase().equals(statusRequested)) {
            imageViewStatus.setBackgroundColor(Color.parseColor("#FF3333"));
            button.setVisibility(View.VISIBLE);
            input.setVisibility(View.VISIBLE);
            inputBack.setVisibility(View.VISIBLE);
        }
        if (task.getStatus().toUpperCase().equals(statusBidded)) {
            imageViewStatus.setBackgroundColor(Color.parseColor("#33FFFF"));
            imageViewStatus.setColorFilter(Color.MAGENTA);
            button.setVisibility(View.VISIBLE);
            input.setVisibility(View.VISIBLE);
            inputBack.setVisibility(View.VISIBLE);
        }
        if (task.getStatus().toUpperCase().equals(statusAssigned)) {
            textViewLowestPrice.setText("Your assigned price is $"+ task.getPrice());
            imageViewStatus.setBackgroundColor(Color.parseColor("#33FF33"));
            imageViewStatus.setColorFilter(Color.RED);
        }

        if (task.getStatus().toUpperCase().equals(statusDone)) {
            textViewLowestPrice.setText("Your final price is $"+ task.getPrice());
            imageViewStatus.setBackgroundColor(Color.parseColor("#3333FF"));
            imageViewStatus.setColorFilter(Color.GREEN);
        }

        //wait to set location

    }

    public void onClickBid(View view) {
        EditText input = (EditText)findViewById(R.id.editTextInput);
        String value_str = input.getText().toString();
        if (value_str.equals("") || value_str.equals(".")) {
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
            Log.i("debug---->", providerName);
            task.providerBidTask(providerName, valueDouble);
        } catch(Exception e) {
            Toast.makeText(this, "Cannot bidded test.", Toast.LENGTH_SHORT);
        }
        taskController.updateTask(task);
        setViews();
        finish();
    }
}

