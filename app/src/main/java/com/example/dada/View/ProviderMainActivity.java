/*
 * ProviderMainActivity
 *
 *
 * April 9, 2018
 *
 * Copyright (c) 2018 Team 12 CMPUT301, University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and condition of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise please contact me.
 */
package com.example.dada.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dada.Controller.TaskController;
import com.example.dada.Model.OnAsyncTaskCompleted;
import com.example.dada.Model.Task.Task;
import com.example.dada.Model.User;
import com.example.dada.R;
import com.example.dada.Util.FileIOUtil;
import com.example.dada.Util.TaskUtil;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.NetworkStatus;
import com.novoda.merlin.registerable.bind.Bindable;
import com.novoda.merlin.registerable.connection.Connectable;
import com.novoda.merlin.registerable.disconnection.Disconnectable;

import java.util.ArrayList;

import im.delight.android.location.SimpleLocation;

/**
 *  Main activity for requester main interface, including showing and managing list of tasks
 *
 *  @see ProviderBrowseTaskActivity
 *  @see ProviderShowTasks5kmOnMap
 *  @see UserEditProfileActivity
 *
 */
public class ProviderMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private User provider;

    // List view for 4 different kinds of task status
    private ListView requestedTaskListView;
    private ListView biddedTaskListView;
    private ListView assignedTaskListView;
    private ListView doneTaskListView;
    private ListView requestedSearchTaskListView;
    private ListView biddedSearchTaskListView;

    // Adapter for 4 different kinds of task status
    private customAdapter requestedTaskAdapter;
    private customAdapter biddedTaskAdapter;
    private customAdapter assignedTaskAdapter;
    private customAdapter doneTaskAdapter;
    private customAdapter requestedSearchTaskAdapter;
    private customAdapter biddedSearchTaskAdapter;

    // List of objects(Task) for 4 different kinds of task status
    private ArrayList<Task> requestedTaskList = new ArrayList<>();
    private ArrayList<Task> biddedTaskList = new ArrayList<>();
    private ArrayList<Task> assignedTaskList = new ArrayList<>();
    private ArrayList<Task> doneTaskList = new ArrayList<>();
    private ArrayList<Task> requestedSearchTaskList = new ArrayList<>();
    private ArrayList<Task> biddedSearchTaskList = new ArrayList<>();

    private SimpleLocation location;

    // Indicating filter status
    private String sortType;

    // Handle double click of back button
    boolean doubleBackToExitPressedOnce = false;

    // Controller: get list of requested tasks
    private TaskController requestedTaskController = new TaskController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            requestedTaskList = (ArrayList<Task>) o;
            requestedTaskAdapter.clear();
            requestedTaskAdapter.addAll(requestedTaskList);
            requestedTaskAdapter.notifyDataSetChanged();
        }
    });

    // Controller: get list of bidded tasks
    private TaskController biddedTaskController = new TaskController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            biddedTaskList = (ArrayList<Task>) o;
            biddedTaskAdapter.clear();
            biddedTaskAdapter.addAll(biddedTaskList);
            biddedTaskAdapter.notifyDataSetChanged();
        }
    });

    // Controller: get list of assigned tasks
    private TaskController assignedTaskController = new TaskController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            assignedTaskList = (ArrayList<Task>) o;
            assignedTaskAdapter.clear();
            assignedTaskAdapter.addAll(assignedTaskList);
            assignedTaskAdapter.notifyDataSetChanged();
        }
    });

    // Controller: get list of done tasks
    private TaskController completedTaskController = new TaskController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            doneTaskList = (ArrayList<Task>) o;
            doneTaskAdapter.clear();
            doneTaskAdapter.addAll(doneTaskList);
            doneTaskAdapter.notifyDataSetChanged();
        }
    });

    // Controller: get list of requested tasks by keywords
    private TaskController requestedSearchTaskController = new TaskController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            requestedSearchTaskList = (ArrayList<Task>) o;
            requestedSearchTaskAdapter.clear();
            requestedSearchTaskAdapter.addAll(requestedSearchTaskList);
            requestedSearchTaskAdapter.notifyDataSetChanged();
        }
    });

    // Controller: get list of bidded tasks by keywords
    private TaskController biddedSearchTaskController = new TaskController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            biddedSearchTaskList = (ArrayList<Task>) o;
            biddedSearchTaskAdapter.clear();
            biddedSearchTaskAdapter.addAll(biddedSearchTaskList);
            biddedSearchTaskAdapter.notifyDataSetChanged();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_main);

        // Default: show all the tasks in list view
        sortType = "all";

        // Set activity background color
        ConstraintLayout rl = (ConstraintLayout)findViewById(R.id.content_Pmain_layout);
        rl.setBackgroundColor(Color.parseColor("#F3F3F3"));

        // Initialize tool bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize side bar
        DrawerLayout drawer = findViewById(R.id.drawer_provider_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeader = navigationView.getHeaderView(0);
        TextView username = navHeader.findViewById(R.id.nav_drawer_provider_username);
        TextView email = navHeader.findViewById(R.id.nav_drawer_provider_email);

        // Get user profile
        provider = FileIOUtil.loadUserFromFile(getApplicationContext());

        // Set drawer text
        username.setText(provider.getUserName());
        email.setText(provider.getEmail());

        // list view
        setListView(sortType);

    }

    @Override
    public void onStart() {
        super.onStart();

        // Initialize adapter for list view
        requestedTaskAdapter = new customAdapter(this, R.layout.task_list_item, requestedTaskList);
        biddedTaskAdapter = new customAdapter(this, R.layout.task_list_item, biddedTaskList);
        assignedTaskAdapter = new customAdapter(this, R.layout.task_list_item, assignedTaskList);
        doneTaskAdapter = new customAdapter(this, R.layout.task_list_item, doneTaskList);
        requestedSearchTaskAdapter = new customAdapter(this, R.layout.task_list_item, requestedSearchTaskList);
        biddedSearchTaskAdapter = new customAdapter(this, R.layout.task_list_item, biddedSearchTaskList);

        // adapt to list view under current filter status
        setAdapter(sortType);

        // Update task list from server
        updateTaskList();
    }

    /**
     * Give user option to double press back button to logout
     */
    @Override
    public void onBackPressed() {
        Log.i("Debug----->", "on back pressed");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        } else {
            // https://stackoverflow.com/questions/8430805/clicking-the-back-button-twice-to-exit-an-activity
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to logout", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    /**
     * Initialize menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.provider_main, menu);
        return true;
    }

    /**
     * Handle tool bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handle selection for side bar
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Manage user profile
        if (id == R.id.nav_manage) {

            // intent to UserEditProfileActivity
            Intent intentUserEditProfile = new Intent(getApplicationContext(), UserEditProfileActivity.class);
            startActivity(intentUserEditProfile);
            finish();
        }
        // Filter: all task
        else if (id == R.id.nav_allTask_Pmain) {

            onStart();
            clearListView(sortType);
            sortType = "all";
            setListView(sortType);
            setAdapter(sortType);
        }
        // Filter: requested task
        else if (id == R.id.nav_requestedTask_Pmain) {

            onStart();
            clearListView(sortType);
            sortType = "requested";
            setListView(sortType);
            setAdapter(sortType);
        }
        // Filter: bidded task
        else if (id == R.id.nav_biddedTask_Pmain) {

            onStart();
            clearListView(sortType);
            sortType = "bidded";
            setListView(sortType);
            setAdapter(sortType);
        }
        // Filter: assigned task
        else if (id == R.id.nav_assignedTask_Pmain) {

            onStart();
            clearListView(sortType);
            sortType = "assigned";
            setListView(sortType);
            setAdapter(sortType);
        }
        // Filter: done task
        else if (id == R.id.nav_doneTask_Pmain) {

            onStart();
            clearListView(sortType);
            sortType = "done";
            setListView(sortType);
            setAdapter(sortType);
        }
        // Search by keyword
        else if (id == R.id.nav_search_Pmain) {

            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Search Task");
            alert.setMessage("Enter the keyword for search");

            // Set an EditText view to get user input
            final EditText input = new EditText(this);
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    // Do something with value!
                    clearListView(sortType);
                    requestedSearchTaskController.searchRequestedTaskByKeyword(input.getText().toString());
                    biddedSearchTaskController.searchBiddedTaskByKeyword(input.getText().toString());
                    sortType = "search";
                    setListView(sortType);
                    setAdapter(sortType);


                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                    onStart();
                    clearListView(sortType);
                    sortType = "search";
                }
            });

            alert.show();

        }
        // Show task in area
        else if (id == R.id.nav_location) {
            // display all requested/bidded tasks on mao
            Intent intentLocationMap = new Intent(getApplicationContext(), ProviderShowTasks5kmOnMap.class);
//            intentDetailMap.putExtra("coordinates", coordinates);
            startActivity(intentLocationMap);
        }
        // Logout
        else if (id == R.id.nav_logout) {

            // intent to login activity
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_provider_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateTaskList() {

        requestedTaskController.getProviderRequestedTask();
        biddedTaskController.getProviderBiddedTask();
        assignedTaskController.getProviderAssignedTask(provider.getUserName());
        completedTaskController.getRequesterDoneTask(provider.getUserName());
    }

    /**
     * Listener for requested task in list view to view detail
     * @param task
     */
    private void openRequestedTaskDetail(final Task task) {
        Log.i("Method start----->", "ProviderMainActivity openRequestedTaskDetail");
        Intent intent = new Intent(this, providerDetailActivity.class);
        intent.putExtra("Task", TaskUtil.serializer(task));
        intent.putExtra("Name", provider.getUserName());
        startActivity(intent);
    }

    /**
     * Listener for bidded task in list view to view detail
     * @param task
     */
    private void openBiddedTaskDetail(final Task task) {
        Log.i("Method start----->", "ProviderMainActivity openRequestedTaskDetail");
        Intent intent = new Intent(this, providerDetailActivity.class);
        intent.putExtra("Task", TaskUtil.serializer(task));
        intent.putExtra("Name", provider.getUserName());
        startActivity(intent);

    }

    /**
     * Listener for assigned task in list view to view detail
     * @param task
     */
    private void openAssignedTaskDetail(final Task task) {
        Log.i("Method start----->", "ProviderMainActivity openRequestedTaskDetail");
        Intent intent = new Intent(this, providerDetailActivity.class);
        intent.putExtra("Task", TaskUtil.serializer(task));
        intent.putExtra("Name", provider.getUserName());
        startActivity(intent);

    }

    /**
     * Listener for done task in list view to view detail
     * @param task
     */
    private void openDoneTaskDetail(final Task task) {
        Log.i("Method start----->", "ProviderMainActivity openRequestedTaskDetail");
        Intent intent = new Intent(this, providerDetailActivity.class);
        intent.putExtra("Task", TaskUtil.serializer(task));
        intent.putExtra("Name", provider.getUserName());
        startActivity(intent);

    }

    private void openRequestedSearchTaskDetail(final Task task) {
        Log.i("Method start----->", "ProviderMainActivity openRequestedSearchTaskDetail");
        Intent intent = new Intent(this, providerDetailActivity.class);
        intent.putExtra("Task", TaskUtil.serializer(task));
        intent.putExtra("Name", provider.getUserName());
        startActivity(intent);

    }

    private void openBiddedSearchTaskDetail(final Task task) {
        Log.i("Method start----->", "ProviderMainActivity openBiddedSearchTaskDetail");
        Intent intent = new Intent(this, providerDetailActivity.class);
        intent.putExtra("Task", TaskUtil.serializer(task));
        intent.putExtra("Name", provider.getUserName());
        startActivity(intent);

    }

    /**
     * Adapt list to list view
     * @param sortType
     */
    public void setAdapter(String sortType){
        if (sortType.equals("all")){
            requestedTaskListView.setAdapter(requestedTaskAdapter);
            biddedTaskListView.setAdapter(biddedTaskAdapter);
            assignedTaskListView.setAdapter(assignedTaskAdapter);
            doneTaskListView.setAdapter(doneTaskAdapter);
        }
        else if (sortType.equals("requested")){
            requestedTaskListView.setAdapter(requestedTaskAdapter);
        }
        else if (sortType.equals("bidded")){
            biddedTaskListView.setAdapter(biddedTaskAdapter);
        }
        else if (sortType.equals("assigned")){
            assignedTaskListView.setAdapter(assignedTaskAdapter);
        }
        else if (sortType.equals("done")){
            doneTaskListView.setAdapter(doneTaskAdapter);
        }
        else if (sortType.equals("search")){
            requestedSearchTaskListView.setAdapter(requestedSearchTaskAdapter);
            biddedSearchTaskListView.setAdapter(biddedSearchTaskAdapter);
        }
    }

    /**
     *  Set list view for different sorting situation
     * @param sortType
     */
    public void setListView(final String sortType){
        TextView textView = (TextView) findViewById(R.id.editText_allTask_ProviderMainActivity);
        if (sortType.equals("all")){

            textView.setText("All Tasks");

            requestedTaskListView = findViewById(R.id.listView_requestedTask_ProviderMainActivity);
            requestedTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // open requested task info dialog
                    openRequestedTaskDetail(requestedTaskList.get(position));

                }
            });

            biddedTaskListView = findViewById(R.id.listView_biddedTask_ProviderMainActivity);
            biddedTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // open bidded task info dialog
                    openBiddedTaskDetail(biddedTaskList.get(position));
                }
            });

            assignedTaskListView = findViewById(R.id.listView_assignedTask_ProviderMainActivity);
            assignedTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // open assigned task info dialog
                    openAssignedTaskDetail(assignedTaskList.get(position));
                }
            });

            doneTaskListView = findViewById(R.id.listView_doneTask_ProviderMainActivity);
            doneTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // open done task info dialog
                    openDoneTaskDetail(doneTaskList.get(position));
                }
            });
        }
        else if(sortType.equals("requested")){

            textView.setText("Requested Tasks");

            requestedTaskListView = findViewById(R.id.listView_requestedTask_ProviderMainActivity);
            requestedTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // open requested task info dialog
                    openRequestedTaskDetail(requestedTaskList.get(position));

                }
            });
        }
        else if(sortType.equals("bidded")){

            textView.setText("Bidded Tasks");

            biddedTaskListView = findViewById(R.id.listView_biddedTask_ProviderMainActivity);
            biddedTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // open requested task info dialog
                    openBiddedTaskDetail(biddedTaskList.get(position));
                }
            });
        }
        else if(sortType.equals("assigned")){

            textView.setText("Assigned Tasks");

            assignedTaskListView = findViewById(R.id.listView_assignedTask_ProviderMainActivity);
            assignedTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // open requested task info dialog
                    openAssignedTaskDetail(assignedTaskList.get(position));
                }
            });
        }
        else if(sortType.equals("done")){

            textView.setText("Done Tasks");

            doneTaskListView = findViewById(R.id.listView_doneTask_ProviderMainActivity);
            doneTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // open requested task info dialog
                    openDoneTaskDetail(doneTaskList.get(position));
                }
            });
        }
        else if(sortType.equals("search")){
            textView.setText("Search Result");

            requestedSearchTaskListView = findViewById(R.id.listView_requestedSearchTask_ProviderMainActivity);
            requestedSearchTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // open requested task info dialog
                    openRequestedSearchTaskDetail(requestedSearchTaskList.get(position));

                }
            });

            biddedSearchTaskListView = findViewById(R.id.listView_biddedSearchTask_ProviderMainActivity);
            biddedSearchTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // open bidded task info dialog
                    openBiddedSearchTaskDetail(biddedSearchTaskList.get(position));
                }
            });
        }
    }

    /**
     * Clear list view when change filter status
     * @param oldSortType
     */
    public void clearListView(String oldSortType){
        if(oldSortType.equals("all")){
            requestedTaskListView.setAdapter(null);
            biddedTaskListView.setAdapter(null);
            assignedTaskListView.setAdapter(null);
            doneTaskListView.setAdapter(null);
        }
        else if(oldSortType.equals("requested")){
            requestedTaskListView.setAdapter(null);
        }
        else if(oldSortType.equals("bidded")){
            biddedTaskListView.setAdapter(null);
        }
        else if(oldSortType.equals("assigned")){
            assignedTaskListView.setAdapter(null);
        }
        else if(oldSortType.equals("done")){
            doneTaskListView.setAdapter(null);
        }
        else if(oldSortType.equals("search")) {
            requestedSearchTaskListView.setAdapter(null);
            biddedSearchTaskListView.setAdapter(null);
        }
    }

    /**
     * when restart, refresh everything
     */
    @Override
    public void onRestart(){
        super.onRestart();
        onStart();
        clearListView(sortType);
        setListView(sortType);
        setAdapter(sortType);
    }

    /**
     * when resume, refresh everything
     */
    @Override
    public void onResume(){
        super.onResume();
        onStart();
        clearListView(sortType);
        setListView(sortType);
        setAdapter(sortType);
    }
}
