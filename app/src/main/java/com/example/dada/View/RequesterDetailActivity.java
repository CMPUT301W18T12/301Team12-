/*
 * RequesterDetailActivity
 *
 *
 * April 9, 2018
 *
 * Copyright (c) 2018 Team 12. CMPUT301, University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and condition of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise please contact me.
 */
package com.example.dada.View;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.dada.Controller.TaskController;
import com.example.dada.Controller.UserController;
import com.example.dada.Exception.UserException;
import com.example.dada.Model.OnAsyncTaskCompleted;
import com.example.dada.Model.OnAsyncTaskFailure;
import com.example.dada.Model.Task.Task;
import com.example.dada.Model.User;
import com.example.dada.R;
import com.example.dada.Util.FileIOUtil;
import com.example.dada.Util.TaskUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * The view of all 4 status of task detail for Requester
 * all view is in one class be careful with disable or active views
 */

public class RequesterDetailActivity extends ListActivity {
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
    private int photoIndex = 0;
    private TaskController taskController = new TaskController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            Task t = (Task) o;
            FileIOUtil.saveRequesterTaskInFile(t, getApplicationContext());
        }
    });
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
    private User requester;
    private User provider;

    /**
     * main function of requesterdetailclass; control the listview click action
     * @param savedInstanceState Bundle that tran objects
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requester_detail);
        //more intent part need                                                              //^_^//
        Intent intent = getIntent();
        Log.i("ActivityStart----->", "RequesterDetailActivity");
        task = TaskUtil.deserializer(intent.getStringExtra("Task"));

        setViews();
        task.setNewBid("0");
        taskController.updateTask(task);

        /**
         * listener of listview click action
         */

        if (task.getStatus().toUpperCase().equals(statusBidded)) {
            final ListView listView = (ListView)findViewById(android.R.id.list);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    ArrayList<String> bid = task.getBidList().get(position);
                    providerName = bid.get(0);
                    AlertDialog.Builder builder = new AlertDialog.Builder(RequesterDetailActivity.this);
                    builder.setMessage("What do you want to do with " + providerName + "'s bid?").setTitle("Notification");

                    builder.setPositiveButton("Assign", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                task.requesterAssignProvider(providerName);
                                task.setStatus(statusAssigned.toLowerCase());
                                taskController.updateTask(task);
                                setViews();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    builder.setNeutralButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ArrayList<ArrayList<String>> bidList = task.getBidList();
                            if (bidList.size() > 1){
                                bidList.remove(position);
                                task.setBidList(bidList);
                            }
                            else{
                                bidList.clear();
                                task.setBidList(bidList);
                                task.setStatus(statusRequested.toLowerCase());
                            }
                            taskController.updateTask(task);
                            // check lowest price
                            checkLowestPrice();
                            setViews();
                        }
                    });

                    taskController.updateTask(task);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }


    }

    private void checkLowestPrice() {
        ArrayList<ArrayList<String>> bids = task.getBidList();
        Double lowestPrice = Double.parseDouble(bids.get(0).get(1));
        task.setLowestPrice(lowestPrice);
        for (ArrayList<String> bid : bids) {
            String price = bid.get(1);
            Double priceD = Double.parseDouble(price);
            if (priceD < lowestPrice) {
                lowestPrice = priceD;
                task.setLowestPrice(priceD);
            }
        }
        Log.i("Debug----->", ""+lowestPrice);
        taskController.updateTask(task);
    }

    /**
     * set the view of the activity
     */

    private void setViews(){
        Log.i("debug---->", ""+task.getBidList());
        // set Toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(task.getTitle());

        // Hidden the view that will not in requested page
        ListView listView = (ListView)findViewById(android.R.id.list);
        listView.setVisibility(View.GONE);
        TextView textViewName = (TextView)findViewById(R.id.textViewName);
        textViewName.setVisibility(View.GONE);
        TextView textViewPhone = (TextView)findViewById(R.id.textViewPhone);
        textViewPhone.setVisibility(View.GONE);
        ImageView imageViewHead = (ImageView)findViewById(R.id.circleImageView);
        imageViewHead.setVisibility(View.GONE);
        Button buttonDone = (Button)findViewById(R.id.buttonDone);
        buttonDone.setVisibility(View.GONE);
        Button buttonNotComplete = (Button)findViewById(R.id.buttonNotComplete);
        buttonNotComplete.setVisibility(View.GONE);

        // getActionBar().setTitle(task.getTitle());        set actionbar

        // Get from https://stackoverflow.com/questions/19452269/android-set-text-to-textview
        // Consider about 2 sides gravity                                                    //^_^//
        TextView textViewDescription = (TextView)findViewById(R.id.textViewDescription);
        textViewDescription.setText(task.getDescription());


        double lowestPrice = task.getLowestPrice();
        TextView textViewLowestPrice = (TextView)findViewById(R.id.textViewLowestPrice);
        textViewLowestPrice.setText("Lowest Price Right Now: $" + lowestPrice);
        //Should not allow it since bid cannot with price 0
        if (lowestPrice == 0 && statusRequested.equals(task.getStatus()) ) {
            Log.i("MayBeError", "Lowest Price equal to 0 and status is not Request");
            textViewLowestPrice.setText("Lowest Price Right Now: $" + "0.00");
        }
        Log.i("ActivityStart----->", "2");

        TextView textViewStatus = (TextView)findViewById(R.id.textViewStatus);
        ImageView imageViewStatus = (ImageView)findViewById(R.id.imageViewStatus);
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        //imageView.setImageBitmap();


        ArrayList<Bitmap> imgs = task.getImg();
        if (imgs != null) {
            //imageView.setImageBitmap();
            Log.i("photo"+photoIndex, "---------------------"+imgs.size());
            int i = photoIndex % imgs.size();
            imageView.setImageBitmap(task.getImg().get(i));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else {
            Toast.makeText(this, "Did not find task img. Replace by default", Toast.LENGTH_SHORT).show();
            imageView.setImageResource(R.drawable.temp_taskimg);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }

        // set map button
        ImageButton locationBtn = (ImageButton)findViewById(R.id.taskDetailMapBtn);
        locationBtn.setImageResource(R.drawable.ic_launcher_foreground);
        locationBtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        assert locationBtn != null;
        locationBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if (task.getCoordinatesString() != null) {
                String coordinates = task.getCoordinatesString();
                Intent intentDetailMap = new Intent(getApplicationContext(), RequesterDetailMapActivity.class);
                intentDetailMap.putExtra("coordinates", coordinates);

                    startActivity(intentDetailMap);
                } else {
                    Toast.makeText(getApplicationContext(), "Requester did not set location.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewStatus.setText(task.getStatus().toUpperCase());

        if (task.getStatus().toUpperCase().equals(statusRequested)) {
            imageViewStatus.setBackgroundColor(Color.parseColor("#FF3333"));
        }

        if (task.getStatus().toUpperCase().equals(statusBidded)) {
            imageViewStatus.setBackgroundColor(Color.parseColor("#33FFFF"));
            imageViewStatus.setColorFilter(Color.MAGENTA);
            listView.setVisibility(View.VISIBLE);
            setListview();
        }
        if (task.getStatus().toUpperCase().equals(statusAssigned)) {

            textViewLowestPrice.setText("Your assigned price is $"+ task.getPrice());
            imageViewStatus.setBackgroundColor(Color.parseColor("#33FF33"));
            imageViewStatus.setColorFilter(Color.RED);
            imageViewHead.setVisibility(View.VISIBLE);
            provider = userController.getUser(task.getProviderUserName());
            if (provider.getProfile_photo() != null) {//^_^//
                imageViewHead.setImageBitmap(provider.getProfile_photo());
            } else {
                Toast.makeText(this, "Did not find user image. Replace by default", Toast.LENGTH_SHORT).show();
                imageViewHead.setImageResource(R.drawable.temp_head);
            }
            textViewName.setVisibility(View.VISIBLE);
            textViewName.setText(provider.getUserName());
            textViewPhone.setVisibility(View.VISIBLE);
            textViewPhone.setText(provider.getPhone());
            buttonDone.setVisibility(View.VISIBLE);
            buttonNotComplete.setVisibility(View.VISIBLE);
        }

        if (task.getStatus().toUpperCase().equals(statusDone)) {
            textViewLowestPrice.setText("Your final price is $"+ task.getPrice());
            imageViewStatus.setBackgroundColor(Color.parseColor("#3333FF"));
            imageViewStatus.setColorFilter(Color.GREEN);
            imageViewHead.setVisibility(View.VISIBLE);
            provider = userController.getUser(task.getProviderUserName());                                                        //^_^//
            if (provider.getProfile_photo() != null) {//^_^//
                imageViewHead.setImageBitmap(provider.getProfile_photo());
            } else {
                Toast.makeText(this, "Did not find user image. Replace by default", Toast.LENGTH_SHORT).show();
                imageViewHead.setImageResource(R.drawable.temp_head);
            }
            textViewName.setVisibility(View.VISIBLE);
            textViewName.setText(provider.getUserName());
            textViewPhone.setVisibility(View.VISIBLE);
            textViewPhone.setText(provider.getPhone());
        }

        //Wait to set Picture                                                                //^_^//


        //wait to set location

    }

    // from https://www.cnblogs.com/allin/archive/2010/05/11/1732200.html

    /**
     * set the listview (adapter)
     */
    private void setListview() {
        Log.i("Tracing----->", "setListview");

        SimpleAdapter listViewAdapter = new SimpleAdapter(this, setListItem(),
                R.layout.activity_requester_detail_listitem, new String[]{"img", "title", "price"},
                new int[]{R.id.img, R.id.title, R.id.price});
        setListAdapter(listViewAdapter);
    }

    /**
     * perpare the thing for listview
     * @return list of map(listview item content)
     */
    private List<Map<String, Object>> setListItem() {
        List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
        ArrayList<ArrayList<String>> providerNames = task.getBidList();
        for (int i=0; i < providerNames.size(); i++) {

            Map<String, Object> map = new HashMap<String, Object>();
            Bitmap img = userController.getUser(providerNames.get(i).get(0)).getProfile_photo();
            // set picture                                                                   //^_^//
            map.put("img", img);
            String name = providerNames.get(i).get(0);
            if (providerNames.get(i).get(2).equals("1")) {
                name = providerNames.get(i).get(0)+ " new~";
                providerNames.get(i).set(2, "0");
                Log.i("_---------------->", providerNames.get(i).get(2));
                taskController.updateTask(task);
            }
            map.put("title", name);
            map.put("price", "$"+providerNames.get(i).get(1));
            itemList.add(map);
        }
        return itemList;
    }

    /**
     * perpare the thing for listview
     * @return list of map(listview item content)
     */
//
//    private List<Map<String, Object>> setListItemRatingSorted() {
//        List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
//        ArrayList<ArrayList<String>> providerNames = task.getBidList();
//        Map<ArrayList<String>, Double> ratingMap = new HashMap<>();
//
//        PriorityQueue<Map.Entry<ArrayList<String>, Double>> maxheap = new PriorityQueue<Double>(new Comparator<Map.Entry<ArrayList<String>, Double>>() {
//            @Override
//            public int compare(Map.Entry<ArrayList<String>, Double> o1, Map.Entry<ArrayList<String>, Double> o2) {
//
//                if ( o2.getValue()-o1.getValue() > 0.0 ) {
//                    return 1;
//                }
//                if ( o2.getValue()-o1.getValue() == 0.0 ) {
//                    return 0;
//                }
//
//                return -1;
//            }
//        });
//
//        PriorityQueue<Map.Entry<ArrayList<String>, Integer>> maxHeap = new PriorityQueue<>((a,b)->(b.getValue()-a.getValue()));
//
//        for (int i=0; i < providerNames.size(); i++) {
//            ArrayList<Double> ratings = userController.getUser(providerNames.get(i).get(0)).getRatings();
//            Double ratings_avg = 0.0;
//            for ( Double rating : ratings ){
//                ratings_avg += rating;
//            }
//            ratings_avg = ratings_avg / (double) ratings.size();
//            ratingMap.put(providerNames.get(i), ratings_avg);
//        }
//
//            Map<String, Object> map = new HashMap<String, Object>();
//
//
//
//
//            Bitmap img = userController.getUser(providerNames.get(i).get(0)).getProfile_photo();
//            // set picture                                                                   //^_^//
//            map.put("img", img);
//            String name = providerNames.get(i).get(0);
//            map.put("title", name);
//            map.put("price", "$"+providerNames.get(i).get(1));
//            itemList.add(map);
//        }
//        return itemList;
//    }

    /**
     * done button click
     * @param view click action
     */
    public void doneOnClick(View view) {

        android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(this);

        alert.setTitle("Rating");
        alert.setMessage("Give Rating, 1 to 5");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                double rating = Double.parseDouble(input.getText().toString());

                if (task.getStatus().toUpperCase().equals(statusAssigned)) {
                    task.setStatus(statusDone.toLowerCase());
                    provider.addRating(rating);
                    taskController.updateTask(task);

                    try {
                        userController.addUserRating(provider);
                    } catch (UserException e) {
                        e.printStackTrace();
                    }

                    setViews();
                }

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();

    }

    public void notComOnClick(View view) {
        if (task.getStatus().toUpperCase().equals(statusAssigned)) {
            try {
                task.requesterCancelAssigned(task.getProviderUserName());
            } catch (Exception e){

            }
            taskController.updateTask(task);
            setViews();
        }
    }

    public void nextPic(View view) {
        photoIndex += 1;
        if (photoIndex > 9) {
            photoIndex -= 10;
        }
        if (photoIndex < 0) {
            photoIndex += 10;
        }
        setViews();
    }

    public void prePic(View view) {
        photoIndex -= 1;
        if (photoIndex > 9) {
            photoIndex -= 10;
        }
        if (photoIndex < 0) {
            photoIndex += 10;
        }
        setViews();
    }

}
