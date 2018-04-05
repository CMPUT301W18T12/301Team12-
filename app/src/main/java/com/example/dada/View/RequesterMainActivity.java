package com.example.dada.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dada.Controller.TaskController;
import com.example.dada.Exception.TaskException;
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

public class RequesterMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Connectable, Disconnectable, Bindable {

    private User requester;

    protected Merlin merlin;

    private ListView requestedTaskListView;
    private ListView biddedTaskListView;
    private ListView assignedTaskListView;
    private ListView completedTaskListView;

    private ArrayAdapter<Task> requestedTaskAdapter;
    private ArrayAdapter<Task> biddedTaskAdapter;
    private ArrayAdapter<Task> assignedTaskAdapter;
    private ArrayAdapter<Task> completedTaskAdapter;

    private ArrayList<Task> requestedTaskList = new ArrayList<>();
    private ArrayList<Task> biddedTaskList = new ArrayList<>();
    private ArrayList<Task> assignedTaskList = new ArrayList<>();
    private ArrayList<Task> completedTaskList = new ArrayList<>();

    private TaskController requestedTaskController = new TaskController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            requestedTaskList = (ArrayList<Task>) o;
            requestedTaskAdapter.clear();
            requestedTaskAdapter.addAll(requestedTaskList);
            requestedTaskAdapter.notifyDataSetChanged();
        }
    });

    private TaskController biddedTaskController = new TaskController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            biddedTaskList = (ArrayList<Task>) o;
            biddedTaskAdapter.clear();
            biddedTaskAdapter.addAll(biddedTaskList);
            biddedTaskAdapter.notifyDataSetChanged();
        }
    });

    private TaskController assignedTaskController = new TaskController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            assignedTaskList = (ArrayList<Task>) o;
            assignedTaskAdapter.clear();
            assignedTaskAdapter.addAll(assignedTaskList);
            assignedTaskAdapter.notifyDataSetChanged();
        }
    });

    private TaskController completedTaskController = new TaskController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            completedTaskList = (ArrayList<Task>) o;
            completedTaskAdapter.clear();
            completedTaskAdapter.addAll(completedTaskList);
            completedTaskAdapter.notifyDataSetChanged();
        }
    });

    // assign bidded task
    private TaskController assignBiddedTaskController = new TaskController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            biddedTaskAdapter.remove((Task) o);
            assignedTaskAdapter.add((Task) o);
            biddedTaskAdapter.notifyDataSetChanged();
            assignedTaskAdapter.notifyDataSetChanged();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requester_main);

        // monitor network connectivity
        merlin = new Merlin.Builder().withConnectableCallbacks().withDisconnectableCallbacks().withBindableCallbacks().build(this);
        merlin.registerConnectable(this);
        merlin.registerDisconnectable(this);
        merlin.registerBindable(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab_requester_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentRequesterAddTask = new Intent(getApplicationContext(), RequesterAddTaskActivity.class);
                startActivity(intentRequesterAddTask);
                finish();
            }
        });

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
        requestedTaskListView = findViewById(R.id.listView_requestedTask_RequesterMainActivity);
        requestedTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // open requested task info dialog
                openRequestedTaskDialog(requestedTaskList.get(position));
            }
        });

        biddedTaskListView = findViewById(R.id.listView_biddedTask_RequesterMainActivity);
        biddedTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // open bidded task info dialog
                openBiddedTaskDialog(biddedTaskList.get(position));
            }
        });

        assignedTaskListView = findViewById(R.id.listView_assignedTask_RequesterMainActivity);
        assignedTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // open assigned task info dialog
                openAssignedTaskDialog(assignedTaskList.get(position));
            }
        });

        completedTaskListView = findViewById(R.id.listView_completedTask_RequesterMainActivity);
        completedTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // open completed task info dialog
                openCompletedTaskDialog(completedTaskList.get(position));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        requestedTaskAdapter = new ArrayAdapter<>(this, R.layout.task_list_item, requestedTaskList);
        biddedTaskAdapter = new ArrayAdapter<>(this, R.layout.task_list_item, biddedTaskList);
        assignedTaskAdapter = new ArrayAdapter<>(this, R.layout.task_list_item, assignedTaskList);
        completedTaskAdapter = new ArrayAdapter<>(this, R.layout.task_list_item, completedTaskList);
        requestedTaskListView.setAdapter(requestedTaskAdapter);
//        biddedTaskListView.setAdapter(biddedTaskAdapter);
//        assignedTaskListView.setAdapter(assignedTaskAdapter);
//        completedTaskListView.setAdapter(completedTaskAdapter);
        updateTaskList();
    }

    private void updateTaskList() {
        requestedTaskController.getRequesterRequestedTask(requester.getUserName());
        biddedTaskController.getRequesterBiddedTask(requester.getUserName());
        assignedTaskController.getRequesterAssignedTask(requester.getUserName());
        completedTaskController.getRequesterCompletedTask(requester.getUserName());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.requester_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {

            // intent to UserEditProfileActivity
            Intent intentUserEditProfile = new Intent(getApplicationContext(), UserEditProfileActivity.class);
            startActivity(intentUserEditProfile);

        } else if (id == R.id.nav_logout) {

            // intent to login activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_requester_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Dialog for Requested Task
     * @param task
     */
    private void openRequestedTaskDialog(final Task task){
        // get task info, and show it on the dialog
        String title = task.getTitle();
        String description = task.getDescription();

        AlertDialog.Builder builder = new AlertDialog.Builder(RequesterMainActivity.this);

        builder.setTitle("Task Information")
                .setMessage("Title: " + title + "\n" + "Description: " + description + "\n")
                .setNeutralButton("view map", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intentRequesterBrowse = new Intent(RequesterMainActivity.this, RequesterBrowseTaskActivity.class);

                        // http://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
                        // Serialize the task object and pass it over through the intent
                        intentRequesterBrowse.putExtra("task", TaskUtil.serializer(task));
                        startActivity(intentRequesterBrowse);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        // Create & Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Dialog for Bidded Task
     * @param task
     */
    private void openBiddedTaskDialog(final Task task) {
        // get task info, and show it on the dialog
        String title = task.getTitle();
        ArrayList<ArrayList<String>> bidList = task.getBidList();

        AlertDialog.Builder builder = new AlertDialog.Builder(RequesterMainActivity.this);

        final ArrayAdapter<String> bidAdapter = new ArrayAdapter<>(RequesterMainActivity.this, android.R.layout.select_dialog_singlechoice);

        for ( ArrayList<String> bid : bidList ){
            Log.d("Bid", bid.toString());
            bidAdapter.add(bid.get(0) + " " + bid.get(1));
        }

        builder.setTitle("Title: " + title);
        builder.setNeutralButton("view map", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent intentRequesterBrowse = new Intent(RequesterMainActivity.this, RequesterBrowseTaskActivity.class);

                // http://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
                // Serialize the task object and pass it over through the intent
                intentRequesterBrowse.putExtra("task", TaskUtil.serializer(task));
                startActivity(intentRequesterBrowse);
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.setAdapter(bidAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                final String[] provider_price = bidAdapter.getItem(id).split(" ");

                AlertDialog.Builder builderInner = new AlertDialog.Builder(RequesterMainActivity.this);
                builderInner.setMessage("Provider: " + provider_price[0] + "\n" + "Price: " + provider_price[1]);
                builderInner.setTitle("Your Task Assigned To: ");
                builderInner.setPositiveButton("Assign", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {

                        // Assign bidded task
                        try {
                            assignBiddedTaskController.requesterAssignTask(task, provider_price[0]);
                        } catch (TaskException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builderInner.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builderInner.show();
            }
        });

        // Create & Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Dialog for Assigned Task
     * @param task
     */
    private void openAssignedTaskDialog(final Task task) {
        // get task info, and show it on the dialog
        String title = task.getTitle();
        String description = task.getDescription();
        String price = task.getPrice().toString();
        String providerUserName = task.getProviderUserName();

        AlertDialog.Builder builder = new AlertDialog.Builder(RequesterMainActivity.this);

        builder.setTitle("Task Information")
                .setMessage("Title: " + title + "\n" + "Description: " + description + "\n" + "Price: " + price + "\n" + "Provider: " + providerUserName + "\n")
                .setNeutralButton("view map", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intentRequesterBrowse = new Intent(RequesterMainActivity.this, RequesterBrowseTaskActivity.class);

                        // http://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
                        // Serialize the task object and pass it over through the intent
                        intentRequesterBrowse.putExtra("task", TaskUtil.serializer(task));
                        startActivity(intentRequesterBrowse);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        // Create & Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Dialog for Completed Task
     * @param task
     */
    private void openCompletedTaskDialog(final Task task) {
        // get task info, and show it on the dialog
        String title = task.getTitle();
        String description = task.getDescription();
        String price = task.getPrice().toString();
        String providerUserName = task.getProviderUserName();

        AlertDialog.Builder builder = new AlertDialog.Builder(RequesterMainActivity.this);

        builder.setTitle("Task Information")
                .setMessage("Title: " + title + "\n" + "Description: " + description + "\n" + "Price: " + price + "\n" + "Provider: " + providerUserName + "\n")
                .setNeutralButton("view map", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intentRequesterBrowse = new Intent(RequesterMainActivity.this, RequesterBrowseTaskActivity.class);

                        // http://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
                        // Serialize the task object and pass it over through the intent
                        intentRequesterBrowse.putExtra("task", TaskUtil.serializer(task));
                        startActivity(intentRequesterBrowse);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
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
        completedTaskController.getRequesterOfflineCompletedTask(requester.getUserName(), this);
    }

    @Override
    public void onBind(NetworkStatus networkStatus) {
        if (networkStatus.isAvailable()) {
            onConnect();
        } else if (!networkStatus.isAvailable()) {
            onDisconnect();
        }
    }

    @Override
    public void onDisconnect() {
        Log.i("Debug", "Offline");
//        inProgressRequestController.getRiderOfflineRequest(requester.getUserName(), this);
    }

    @Override
    public void onConnect() {
        offlineHandler();
    }
}
