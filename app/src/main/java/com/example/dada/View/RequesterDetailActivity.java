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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.dada.Controller.TaskController;
import com.example.dada.Controller.UserController;
import com.example.dada.Model.OnAsyncTaskCompleted;
import com.example.dada.Model.OnAsyncTaskFailure;
import com.example.dada.Model.Task.Task;
import com.example.dada.Model.User;
import com.example.dada.R;
import com.example.dada.Util.FileIOUtil;
import com.example.dada.Util.TaskUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Log.i("ActivityStart----->", "ResquesterDetailActivity");
        task = TaskUtil.deserializer(intent.getStringExtra("Task"));

        setViews();

        /**
         * listener of listview click action
         */

        if (task.getStatus().toUpperCase().equals(statusBidded)) {
            final ListView listView = (ListView)findViewById(android.R.id.list);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ArrayList<String> bid = task.getBidList().get(position);
                    providerName = bid.get(0);
                    AlertDialog.Builder builder = new AlertDialog.Builder(RequesterDetailActivity.this);
                    builder.setMessage("What do you due with " + providerName + "'s bidded").setTitle("Notofocation");

                    builder.setPositiveButton("Is Him", new DialogInterface.OnClickListener() {
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

                    builder.setNeutralButton("Delete Him", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ArrayList<ArrayList<String>> bidList = task.getBidList();
                            if (bidList.size() > 1){
                                bidList.remove(i);
                                task.setBidList(bidList);
                            }
                            else{
                                bidList.clear();
                                task.setBidList(bidList);
                                task.setStatus(statusRequested.toLowerCase());
                            }
                            taskController.updateTask(task);
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

    /**
     * set the view of the activity
     */

    private void setViews(){
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

        double lowestPrice = task.getPrice();
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
        if (true) {
            //imageView.setImageBitmap();
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
                String coordinates = task.getCoordinatesString();
                Intent intentDetailMap = new Intent(getApplicationContext(), RequesterDetailMapActivity.class);
                intentDetailMap.putExtra("coordinates", coordinates);
                startActivity(intentDetailMap);
            }
        });

        textViewStatus.setText(task.getStatus().toUpperCase());

        if (task.getStatus().toUpperCase().equals(statusBidded)) {
            Log.i("Tracing----->", task.getStatus());
            imageViewStatus.setColorFilter(Color.MAGENTA);
            listView.setVisibility(View.VISIBLE);
            setListview();
        }
        if (task.getStatus().toUpperCase().equals(statusAssigned)) {
            Log.i("Tracing----->", task.getStatus());
            imageViewStatus.setColorFilter(Color.RED);
            imageViewHead.setVisibility(View.VISIBLE);
            provider = userController.getUser(task.getProviderUserName());                                                        //^_^//
            imageViewHead.setImageBitmap(provider.getProfile_photo());
            textViewName.setVisibility(View.VISIBLE);
            textViewName.setText(provider.getUserName());
            textViewPhone.setVisibility(View.VISIBLE);
            textViewPhone.setText(provider.getPhone());
            buttonDone.setVisibility(View.VISIBLE);
            buttonNotComplete.setVisibility(View.VISIBLE);
        }

        if (task.getStatus().toUpperCase().equals(statusDone)) {
            Log.i("Tracing----->", task.getStatus());
            imageViewStatus.setColorFilter(Color.GREEN);
            imageViewHead.setVisibility(View.VISIBLE);
            provider = userController.getUser(task.getProviderUserName());                                                        //^_^//
            imageViewHead.setImageBitmap(provider.getProfile_photo());
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
            map.put("title", name);
            map.put("price", "$"+providerNames.get(i).get(1));
            itemList.add(map);
        }
        return itemList;
    }

    /**
     * done button click
     * @param view click action
     */
    public void doneOnClick(View view) {
        if (task.getStatus().toUpperCase().equals(statusAssigned)) {
            task.setStatus(statusDone.toLowerCase());
            taskController.updateTask(task);
            setViews();
        }
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

}
