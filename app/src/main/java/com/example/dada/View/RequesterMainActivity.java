/*
 * RequesterMainActivity
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
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dada.Controller.TaskController;
import com.example.dada.Model.OnAsyncTaskCompleted;
import com.example.dada.Model.OnAsyncTaskFailure;
import com.example.dada.Model.Task.Task;
import com.example.dada.Model.User;
import com.example.dada.R;
import com.example.dada.Util.FileIOUtil;
import com.example.dada.Util.TaskUtil;
import com.google.gson.Gson;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.NetworkStatus;
import com.novoda.merlin.registerable.bind.Bindable;
import com.novoda.merlin.registerable.connection.Connectable;
import com.novoda.merlin.registerable.disconnection.Disconnectable;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 *  Main activity for requester main interface, including showing and managing list of tasks
 *
 *  @see RequesterDetailActivity
 *  @see RequesterAddTaskActivity
 *  @see RequesterBrowseTaskActivity
 *  @see RequesterEditTaskActivity
 *  @see UserEditProfileActivity
 *
 */
public class RequesterMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Connectable, Disconnectable, Bindable {

    private User requester;

    // For internet connection checking
    protected Merlin merlin;

    // List view for 4 different kinds of task status
    private ListView requestedTaskListView;
    private ListView biddedTaskListView;
    private ListView assignedTaskListView;
    private ListView doneTaskListView;

    // Adapter for 4 different kinds of task status
    private customAdapter requestedTaskAdapter;
    private customAdapter biddedTaskAdapter;
    private customAdapter assignedTaskAdapter;
    private customAdapter doneTaskAdapter;

    // List of objects(Task) for 4 different kinds of task status
    private ArrayList<Task> requestedTaskList = new ArrayList<>();
    private ArrayList<Task> biddedTaskList = new ArrayList<>();
    private ArrayList<Task> assignedTaskList = new ArrayList<>();
    private ArrayList<Task> doneTaskList = new ArrayList<>();

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
    private TaskController doneTaskController = new TaskController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            doneTaskList = (ArrayList<Task>) o;
            doneTaskAdapter.clear();
            doneTaskAdapter.addAll(doneTaskList);
            doneTaskAdapter.notifyDataSetChanged();
        }
    });


    // List for offline situation
    private ArrayList<Task> offlineRequesterList = new ArrayList<>();

    // Controller: handle task creating and deleting
    private TaskController taskController = new TaskController(
            // Online
            new OnAsyncTaskCompleted() {
                @Override
                public void onTaskCompleted(Object o) {
                    Task t = (Task) o;
                    FileIOUtil.saveRequesterTaskInFile(t, getApplicationContext());
                }
            },
            // Offline
            new OnAsyncTaskFailure() {
                @Override
                public void onTaskFailed (Object o){
                    Toast.makeText(getApplication(), "Device offline", Toast.LENGTH_SHORT).show();
                    offlineRequesterList.add((Task) o);
                    FileIOUtil.saveOfflineTaskInFile((Task) o, getApplicationContext());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requester_main);

        // Default: show all the tasks in list view
        sortType = "all";

        // Set activity background color
        ConstraintLayout rl = (ConstraintLayout)findViewById(R.id.content_Rmain_layout);
        rl.setBackgroundColor(Color.parseColor("#F3F3F3"));

        // monitor network connectivity
        merlin = new Merlin.Builder().withConnectableCallbacks().withDisconnectableCallbacks().withBindableCallbacks().build(this);
        merlin.registerConnectable(this);
        merlin.registerDisconnectable(this);
        merlin.registerBindable(this);
        merlin.bind();

        // Initialize tool bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Floating button for add new Task
        FloatingActionButton fab = findViewById(R.id.fab_requester_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent to another activity to handle new task
                Intent intentRequesterAddTask = new Intent(getApplicationContext(), RequesterAddTaskActivity.class);
                startActivity(intentRequesterAddTask);
            }
        });

        // Initialize side bar
        DrawerLayout drawer = findViewById(R.id.drawer_requester_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeader = navigationView.getHeaderView(0);
        TextView username = navHeader.findViewById(R.id.nav_drawer_requester_username);
        TextView email = navHeader.findViewById(R.id.nav_drawer_requester_email);

        // Get user profile
        requester = FileIOUtil.loadUserFromFile(getApplicationContext());

        // Set drawer text
        username.setText(requester.getUserName());
        email.setText(requester.getEmail());

        // list view
        setListView(sortType);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Initialize adapter for list view
        requestedTaskAdapter = new customAdapter(this, R.layout.task_list_item, requestedTaskList, 1);
        biddedTaskAdapter = new customAdapter(this, R.layout.task_list_item, biddedTaskList,1);
        assignedTaskAdapter = new customAdapter(this, R.layout.task_list_item, assignedTaskList,1);
        doneTaskAdapter = new customAdapter(this, R.layout.task_list_item, doneTaskList,1);

        // adapt to list view under current filter status
        setAdapter(sortType);

        // Update task list from server
        updateTaskList();
    }

    // Update task list for 4 task lists
    private void updateTaskList() {
        requestedTaskController.getRequesterRequestedTask(requester.getUserName());
        biddedTaskController.getRequesterBiddedTask(requester.getUserName());
        assignedTaskController.getRequesterAssignedTask(requester.getUserName());
        doneTaskController.getRequesterDoneTask(requester.getUserName());
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
        getMenuInflater().inflate(R.menu.requester_main, menu);
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
        else if (id == R.id.nav_allTask_Rmain) {

            onStart();
            clearListView(sortType);
            sortType = "all";
            setListView(sortType);
            setAdapter(sortType);
        }
        // Filter: requested task
        else if (id == R.id.nav_requestedTask_Rmain) {

            onStart();
            clearListView(sortType);
            sortType = "requested";
            setListView(sortType);
            setAdapter(sortType);
        }
        // Filter: bidded task
        else if (id == R.id.nav_biddedTask_Rmain) {

            onStart();
            clearListView(sortType);
            sortType = "bidded";
            setListView(sortType);
            setAdapter(sortType);
        }
        // Filter: assigned task
        else if (id == R.id.nav_assignedTask_Rmain) {

            onStart();
            clearListView(sortType);
            sortType = "assigned";
            setListView(sortType);
            setAdapter(sortType);
        }
        // Filter: done task
        else if (id == R.id.nav_doneTask_Rmain) {

            onStart();
            clearListView(sortType);
            sortType = "done";
            setListView(sortType);
            setAdapter(sortType);
        }
        // Logout
        else if (id == R.id.nav_logout) {
            finish();
        }


        DrawerLayout drawer = findViewById(R.id.drawer_requester_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Listener for requested task in list view to view detail
     *
     * @param task
     */
    private void openRequestedTaskDetail(final Task task){
        Log.i("Method start----->", "RequesterMainActivity openRequestedTaskDetail");
        Intent intent = new Intent(this, RequesterDetailActivity.class);
        intent.putExtra("Filename", "Task");
        saveInFile(task);
        startActivity(intent);
    }

    /**
     * Listener for bidded task in list view to view detail
     *
     * @param task
     */
    private void openBiddedTaskDetail(final Task task) {
        Log.i("Method start----->", "ResquesterMainActivity openRequestedTaskDetail");
        // get task info, and show it on the dialog
        Intent intent = new Intent(this, RequesterDetailActivity.class);
        intent.putExtra("Filename", "Task");
        saveInFile(task);
        startActivity(intent);
    }

    /**
     * Listener for assigned task in list view to view detail
     * @param task
     */
    private void openAssignedTaskDetail(final Task task) {
        Intent intent = new Intent(this, RequesterDetailActivity.class);
        intent.putExtra("Filename", "Task");
        saveInFile(task);
        startActivity(intent);
    }

    /**
     * Listener for done task in list view to view detail
     *
     * @param task
     */
    private void openDoneTaskDetail(final Task task) {
        Intent intent = new Intent(this, RequesterDetailActivity.class);
        intent.putExtra("Filename", "Task");
        saveInFile(task);
        startActivity(intent);
    }

    /**
     * Dialog for confirm delete Requested Task
     * @param task
     */
    private void AskDeleteRequestedTask(final Task task){
        AlertDialog.Builder builder  =new AlertDialog.Builder(RequesterMainActivity.this);
                //set message, title, and icon
        builder.setTitle("Delete")
                .setMessage("Do you want to Delete")

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        taskController.deleteTask(task);
                        requestedTaskAdapter.remove(task);
                        requestedTaskAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (task.getStatus().toUpperCase().equals("REQUESTED")) {
                            Intent intent = new Intent(getApplicationContext(), RequesterEditTaskActivity.class);
                            intent.putExtra("Task", TaskUtil.serializer(task));
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "You can long edit when status is Request", Toast.LENGTH_SHORT);
                        }
                        dialog.dismiss();

                    }
                });
        // Create & Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Dialog for confirm delete Bidded Task
     * @param task
     */
    private void AskDeleteBiddedTask(final Task task){
        AlertDialog.Builder builder  =new AlertDialog.Builder(RequesterMainActivity.this);
        //set message, title, and icon
        builder.setTitle("Delete")
                .setMessage("Do you want to Delete")

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        taskController.deleteTask(task);
                        biddedTaskAdapter.remove(task);
                        biddedTaskAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // Create & Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Dialog for confirm delete Assigned Task
     * @param task
     */
    private void AskDeleteAssignedTask(final Task task){
        AlertDialog.Builder builder  =new AlertDialog.Builder(RequesterMainActivity.this);
        //set message, title, and icon
        builder.setTitle("Delete")
                .setMessage("Do you want to Delete")

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        taskController.deleteTask(task);
                        assignedTaskAdapter.remove(task);
                        assignedTaskAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // Create & Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    /**
     * Dialog for confirm delete Done Task
     * @param task
     */
    private void AskDeleteDoneTask(final Task task){
        AlertDialog.Builder builder  =new AlertDialog.Builder(RequesterMainActivity.this);
        //set message, title, and icon
        builder.setTitle("Delete")
                .setMessage("Do you want to Delete")

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        taskController.deleteTask(task);
                        doneTaskAdapter.remove(task);
                        doneTaskAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // Create & Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    /**
     * Once the device went offline, try to get task list from internal storage
     */
    protected void offlineHandler() {
        requestedTaskController.getRequesterOfflineRequestedTask(requester.getUserName(), this);
        biddedTaskController.getRequesterOfflineBiddedTask(requester.getUserName(), this);
        assignedTaskController.getRequesterOfflineAssignedTask(requester.getUserName(), this);
        doneTaskController.getRequesterOfflineDoneTask(requester.getUserName(), this);
    }

    /**
     *  Set list view for different sorting situation
     * @param sortType
     */
    public void setListView(final String sortType){
        Log.i("Method start----->", "RequesterMainActivity setListView");
        TextView textView = (TextView) findViewById(R.id.editText_allTask_RequesterMainActivity);
        // Show all tasks
        if (sortType.equals("all")){

            textView.setText("All Tasks");

            requestedTaskListView = findViewById(R.id.listView_requestedTask_RequesterMainActivity);
            requestedTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // open requested task info
                    openRequestedTaskDetail(requestedTaskList.get(position));

                }
            });

            biddedTaskListView = findViewById(R.id.listView_biddedTask_RequesterMainActivity);
            biddedTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // open bidded task info
                    openBiddedTaskDetail(biddedTaskList.get(position));
                }
            });

            assignedTaskListView = findViewById(R.id.listView_assignedTask_RequesterMainActivity);
            assignedTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // open assigned task info
                    openAssignedTaskDetail(assignedTaskList.get(position));
                }
            });

            doneTaskListView = findViewById(R.id.listView_doneTask_RequesterMainActivity);
            doneTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // open done task info
                    openDoneTaskDetail(doneTaskList.get(position));
                }
            });

            requestedTaskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    AskDeleteRequestedTask(requestedTaskList.get(position));
                    return true;
                }
            });

            biddedTaskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    AskDeleteBiddedTask(biddedTaskList.get(position));
                    return true;
                }
            });

            assignedTaskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    AskDeleteAssignedTask(assignedTaskList.get(position));
                    return true;
                }
            });

            doneTaskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    AskDeleteDoneTask(doneTaskList.get(position));
                    return true;
                }
            });

        }
        // show requested tasks
        else if(sortType.equals("requested")){

            textView.setText("Requested Tasks");

            requestedTaskListView = findViewById(R.id.listView_requestedTask_RequesterMainActivity);
            requestedTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // open requested task info dialog
                    openRequestedTaskDetail(requestedTaskList.get(position));

                }
            });
            requestedTaskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    AskDeleteRequestedTask(requestedTaskList.get(position));
                    return true;
                }
            });
        }
        //show bidded tasks
        else if(sortType.equals("bidded")){

            textView.setText("Bidded Tasks");

            biddedTaskListView = findViewById(R.id.listView_biddedTask_RequesterMainActivity);
            biddedTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // open requested task info dialog
                    openBiddedTaskDetail(biddedTaskList.get(position));
                }
            });
            biddedTaskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    AskDeleteBiddedTask(biddedTaskList.get(position));
                    return true;
                }
            });
        }
        // show assigned tasks
        else if(sortType.equals("assigned")){

            textView.setText("Assigned Tasks");

            assignedTaskListView = findViewById(R.id.listView_assignedTask_RequesterMainActivity);
            assignedTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // open requested task info dialog
                    openAssignedTaskDetail(assignedTaskList.get(position));
                }
            });
            assignedTaskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    AskDeleteAssignedTask(assignedTaskList.get(position));
                    return true;
                }
            });
        }
        // show done tasks
        else if(sortType.equals("done")){

            textView.setText("Done Tasks");

            doneTaskListView = findViewById(R.id.listView_doneTask_RequesterMainActivity);
            doneTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // open requested task info dialog
                    openDoneTaskDetail(doneTaskList.get(position));
                }
            });
            doneTaskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    AskDeleteDoneTask(doneTaskList.get(position));
                    return true;
                }
            });
        }
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
    }

    /**
     * Update offline tasks when back online
     */
    protected void updateOfflineRequest() {
        ArrayList<String> offlineList = TaskUtil.getOfflineTaskList(getApplicationContext());
        if (offlineList == null) return;
        offlineRequesterList = FileIOUtil.loadTaskFromFile(getApplicationContext(), offlineList);
        for (Task t : offlineRequesterList) {
            if (t.getRequesterUserName().equals(requester.getUserName())) {
                taskController.createTask(t);
                deleteFile(TaskUtil.generateOfflineTaskFileName(t));
            }
        }
    }

    /**
     * check internet connection
     * @param networkStatus
     */
    @Override
    public void onBind(NetworkStatus networkStatus) {
        onStart();
        if (networkStatus.isAvailable()) {
            onConnect();
        } else if (!networkStatus.isAvailable()) {
            onDisconnect();
        }
    }

    /**
     * handle offline situation
     */
    @Override
    public void onDisconnect() {
        Log.i("Debug ---------->", "Offline");
        offlineHandler();
        requestedTaskController.getRequesterOfflineTask(requester.getUserName(), this);
    }

    /**
     * handle online situation
     */
    @Override
    public void onConnect() {
        // try to update after regain internet access

        updateOfflineRequest();
        taskController.updateRequesterOfflineTask(requester.getUserName(), this);
    }


    @Override
    protected void onPause() {
        merlin.unbind();
        super.onPause();
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

    /**
     * save file temp to local since intent do not allow file larger than 200kb
     * @param task
     */
    protected void saveInFile(Task task) {
        Log.i("LifeCycle ---->", "save file is called");
        try {
            FileOutputStream fos = openFileOutput("Task", Context.MODE_PRIVATE);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));

            Gson gson = new Gson();
            String file = TaskUtil.serializer(task);
            gson.toJson(file, out);
            out.flush();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Log.i("LifeCycle ---->", "save error1 is called");
            throw new RuntimeException();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.i("LifeCycle ---->", "save error2 is called");
            throw new RuntimeException();
        }
    }

}
