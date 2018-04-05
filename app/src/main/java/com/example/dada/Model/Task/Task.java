/* Task
 *
 * Version 1.0
 *
 * March 15, 2018
 *
 * Copyright (c) 2018 Team 12 CMPUT 301. University of Alberta - All Rights Reserved.
 * You may use distribute or modify this code under terms and condition of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of licence in this project. Otherwise please contact contact sfeng3@ualberta.ca.
 */

package com.example.dada.Model.Task;

import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;

import com.example.dada.Constant;
import com.example.dada.Exception.TaskException;
import com.example.dada.Model.Locations;
import com.example.dada.Model.OnAsyncTaskCompleted;
import com.example.dada.Model.OnAsyncTaskFailure;
import com.example.dada.Util.TaskUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import org.osmdroid.util.GeoPoint;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

public abstract class Task {

    private String ID;
    private double price;
    private double lowestPrice;
    private String title;
    private String description;
    private String status;
    private String requesterUserName;
    private String providerUserName;
    private Bitmap img;
    private Locations location;

    private ArrayList<ArrayList<String>> bidList = new ArrayList<>();

    private transient static JestDroidClient client;

    /**
     * Empty constructor for fun :)
     */
    public Task() {

    }

    /**
     * Constructor for a requested task.
     *
     * @param title         title of the task
     * @param description   description of the task
     * @param requesterUserName
     * @param status
     * @param location
     */
    public Task(String title, String description, String requesterUserName, String status, Locations location) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.requesterUserName = requesterUserName;
        this.location = location;
    }

    /**
     * Constructor for a requested task.
     *
     * @param title         title of the task
     * @param description   description of the task
     * @param requesterUserName
     * @param status
     */
    public Task(String title, String description, String requesterUserName, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.requesterUserName = requesterUserName;
    }

    public Task(String title, String description, String requesterUserName, String status, Bitmap img) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.img = img;
        this.requesterUserName = requesterUserName;
    }

    /**
     * Constructor for AssignedTask or CompletedTask.
     *
     * @param requesterUserName  the requester user name
     * @param providerUserName   the provider user name
     * @param price              the price
     * @param location
     */
    public Task(String requesterUserName, String providerUserName, double price, Locations location) {
        this.requesterUserName = requesterUserName;
        this.providerUserName = providerUserName;
        this.price = price;
        this.location = location;
    }

    /**
     * Constructor for AssignedTask or CompletedTask.
     *
     * @param requesterUserName  the requester user name
     * @param providerUserName   the provider user name
     * @param price              the price
     */
    public Task(String requesterUserName, String providerUserName, double price) {
        this.requesterUserName = requesterUserName;
        this.providerUserName = providerUserName;
        this.price = price;
    }

    /**
     * Constructor for BiddedTask
     *
     * @param requesterUserName     the requester user name
     * @param bid                   the list of providers username and price who bidded the task
     * @param price                 the price
     * @param location
     */
    public Task(String requesterUserName, ArrayList<String> bid, Double price, Locations location){
        this.requesterUserName = requesterUserName;
        this.bidList.add(bid);
        this.price = price;
        this.location = location;
    }

    /**
     * Constructor for BiddedTask
     *
     * @param requesterUserName     the requester user name
     * @param bid                   the list of providers username and price who bidded the task
     * @param price                 the price
     */
    public Task(String requesterUserName, ArrayList<String> bid, Double price){
        this.requesterUserName = requesterUserName;
        this.bidList.add(bid);
        this.price = price;
    }

    /**
     * Static class that adds the task
     */
    public static class CreateTaskTask extends AsyncTask<Task, Void, Task> {
        /**
         * The Listener.
         */
        public OnAsyncTaskCompleted listener;
        public OnAsyncTaskFailure offlineHandler;

        // http://stackoverflow.com/questions/9963691/android-asynctask-sending-callbacks-to-ui
        // Author: Dmitry Zaitsev
        private TaskException taskException;

        /**
         * Constructor for CreateRequestTask class
         *
         * @param listener the customize job after the async task is done
         */
        public CreateTaskTask(OnAsyncTaskCompleted listener) {
            this.listener = listener;
        }

        /**
         * Constructor for create request async task
         * @param listener the customize job after the async task is done
         * @param offlineHandler the customize job after the async task is fail
         */
        public CreateTaskTask(OnAsyncTaskCompleted listener, OnAsyncTaskFailure offlineHandler) {
            this.listener = listener;
            this.offlineHandler = offlineHandler;
        }

        /**
         * Update the task when user bid a bidded or requested task
         * @param tasks the task object to be create
         */
        @Override
        protected Task doInBackground(Task... tasks) {
            verifySettings();
            for (Task t : tasks) {
                Index index = new Index.Builder(t).index("team12").type("task").id(t.getID()).build();
                try {
                    DocumentResult result = client.execute(index);
                    if (result.isSucceeded()) {
                        // set ID
                        t.setID(result.getId());
                        Log.i("Debug", "Successful create task");
                    } else {
                        Log.i("Debug", "Elastic search was not able to add the request.");
                    }
                } catch (Exception e) {
                    taskException = new TaskException("Application lost connection to the server");
                    Log.i("Debug", "We failed to add a request to elastic search!");
                    e.printStackTrace();
                }
            }
            return tasks[0];
        }

        /**
         * Execute after async task is finished
         * Stuff like notify ArrayAdapter the data set is changed
         * @param task the request
         */
        @Override
        protected void onPostExecute(Task task) {
            if (listener != null && taskException == null) {
                listener.onTaskCompleted(task);
            } else if (offlineHandler != null && taskException != null) {
                Log.i("Debug", "Fail to upload");
                offlineHandler.onTaskFailed(task);
            }
        }
    }

    /**
     * Static class that update the task
     */
    public static class UpdateTaskTask extends AsyncTask<Task, Void, Task> {
        /**
         * The Listener.
         */
        public OnAsyncTaskCompleted listener;
        public OnAsyncTaskFailure offlineHandler;
        private TaskException taskException;

        /**
         * Constructor for UpdateTaskTask class
         *
         * @param listener the customize job after the async task is done
         */
        public UpdateTaskTask(OnAsyncTaskCompleted listener) {
            this.listener = listener;
        }

        /**
         * Constructor for UpdateTaskTask async task
         * @param listener the customize job after the async task is done
         * @param offlineHandler the customize job after the async task is fail
         */
        public UpdateTaskTask(OnAsyncTaskCompleted listener, OnAsyncTaskFailure offlineHandler) {
            this.listener = listener;
            this.offlineHandler = offlineHandler;
        }

        /**
         * Update the task when user assigned, delete a task
         * @param tasks the task object to be updated
         */
        @Override
        protected Task doInBackground(Task... tasks) {
            verifySettings();
            // Constructs json string
            String query = TaskUtil.serializer(tasks[0]);
            Log.i("Debug", query);
            Index index = new Index.Builder(query)
                    .index("team12").type("task").id(tasks[0].getID()).build();
            try {
                DocumentResult result = client.execute(index);

                if (result.isSucceeded()) {
                    Log.i("Debug", "Successful update the request");
                } else {
                    Log.i("Debug", "Elastic search was not able to add the request.");
                }
            } catch (Exception e) {
                taskException = new TaskException("Application lost connection to the server");
                Log.i("Debug", "We failed to add a request to elastic search!");
                e.printStackTrace();
            }
            return tasks[0];
        }

        /**
         * Execute after async task is finished
         * Stuff like notify arrayadapter the data set is changed
         * @param task the task
         */
        @Override
        protected void onPostExecute(Task task) {
            if (listener != null && taskException == null) {
                listener.onTaskCompleted(task);
            } else if (offlineHandler != null && taskException != null) {
                Log.i("Debug", "Fail to upload");
                offlineHandler.onTaskFailed(task);
            }
        }
    }

    /**
     * TODO Static class that cancel the task
     */
    public static class DeleteTaskTask extends AsyncTask<Task, Void, Task> {
        /**
         * The Listener.
         */
        public OnAsyncTaskCompleted listener;

        /**
         * Constructor for DeleteTaskTask class
         *
         * @param listener the customize job after the async task is done
         */
        public DeleteTaskTask(OnAsyncTaskCompleted listener) {
            this.listener = listener;
        }

        /**
         * Cancel the request
         * @param tasks the request object to be canceled
         */
        @Override
        protected Task doInBackground(Task... tasks) {
            verifySettings();

            for (Task t : tasks) {
                Delete delete = new Delete.Builder(t.getID()).index("team12").type("task").build();
                try {
                    DocumentResult result = client.execute(delete);
                    if (result.isSucceeded()) {
                        Log.i("Debug", "Successful delete request");
                    } else {
                        Log.i("Error", "Elastic search was not able to add the request.");
                    }
                } catch (Exception e) {
                    Log.i("Error", "We failed to add a request to elastic search!");
                    e.printStackTrace();
                }
            }
            return tasks[0];
        }

        /**
         * Excute after async task is finished
         * Stuff like notify arrayadapter the data set is changed
         * @param task nothing
         */
        @Override
        protected void onPostExecute(Task task) {
            if (listener != null) {
                listener.onTaskCompleted(task);
            }
        }
    }

    /**
     * Static class that fetch request from server
     */
    public static class GetTasksListTask extends AsyncTask<String, Void, ArrayList<NormalTask>> {
        /**
         * The Listener.
         */
        public OnAsyncTaskCompleted listener;

        /**
         * Instantiates a new Get requests list task.
         *
         * @param listener the listener
         */
        public GetTasksListTask(OnAsyncTaskCompleted listener) {
            this.listener = listener;
        }

        /**
         * Fetch request list that matched the parameters, by keyword, geo-location, and all requests
         * @param search_parameters the parameter to search
         * @return an ArrayList of tasks
         */
        @Override
        protected ArrayList<NormalTask> doInBackground(String... search_parameters) {
            verifySettings();

            ArrayList<NormalTask> tasks = new ArrayList<>();

            // assume that search_parameters[0] is the only search term we are interested in using
            Search search = new Search.Builder(search_parameters[0])
                    .addIndex("team12")
                    .addType("task")
                    .build();
            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    List<NormalTask> findTask = result.getSourceAsObjectList(NormalTask.class);
                    tasks.addAll(findTask);
                    Log.i("Debug", "Successful get the task list");
                }
                else {
                    Log.i("Error", "The search query failed to find any tweets that matched.");
                }
            }
            catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }
            return tasks;
        }

        @Override
        protected void onPostExecute(ArrayList<NormalTask> normalTasks) {
            if (listener != null) {
                ArrayList<Task> tasksList = new ArrayList<>();
                for (NormalTask t : normalTasks) {
                    tasksList.add(t);
                }
                listener.onTaskCompleted(tasksList);
            }
        }
    }


    /**
     * Set up the connection with server
     */
    private static void verifySettings() {
        // if the client hasn't been initialized then we should make it!
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig
                    .Builder(Constant.ELASTIC_SEARCH_URL);
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }

    /**
     * Overide toString method
     * @return the description of the task
     */
    @Override
    public String toString() {
        if ( title == null ) return null;
        if ( status.equals("requested")){
            return title + " " + status;
        }else{
            return title + " " + status + " " + lowestPrice + " ";
        }
    }

    /**
     * Provider bids the requested task.
     *
     * @param providerUserName the provider user name who bids the requested task
     * @param price the bidded price
     */
    public void providerBidTask(String providerUserName, double price) throws TaskException{
        if ( getStatus().equals("requested") ){
            setStatus("bidded");
        }
        boolean found = false;
        Double lowestPrice = Double.MAX_VALUE;
        for ( ArrayList<String> bid : bidList ){
            if ( bid.get(0).equals(providerUserName) ){
                found = true;
                bid.set(1, Double.toString(price));
            }
            lowestPrice = (lowestPrice > Double.parseDouble(bid.get(1))) ? Double.parseDouble(bid.get(1)) : lowestPrice;
        }
        if ( !found ){
            ArrayList<String> bid = new ArrayList<>();
            bid.add(providerUserName);
            bid.add(Double.toString(price));
            bidList.add(bid);
            lowestPrice = (lowestPrice > Double.parseDouble(bid.get(1))) ? Double.parseDouble(bid.get(1)) : lowestPrice;
        }
        setLowestPrice(lowestPrice);
    }

    /**
     * Requester assign provider.
     *
     * @param  providerUserName the provider user namen
     * @throws TaskException    raise exception when request has not been confirmed
     */
    public void requesterAssignProvider(String providerUserName) throws TaskException {
        if (bidList == null || bidList.isEmpty()) {
            // If the task has not been bidded yet
            throw new TaskException("This task has not been bidded by any provider yet");
        } else {
            // Assigned provider
            assert getStatus().equals("bidded");
            setStatus("assigned");
            setProviderUserName(providerUserName);

            for ( ArrayList<String> bid : bidList ){
                if ( bid.get(0).equals(providerUserName) ){
                   setPrice(Double.parseDouble(bid.get(1)));
                }
            }

            bidList.clear();
        }
    }

    /**
     * Provider confirm task complete.
     */
    public void providerCompleteTask() throws TaskException {
        assert getStatus().equals("assigned");
        setStatus("completed");
    }

    /**
     * Requester confirm task not complete.
     */
    public void requesterConfirmTaskNotComplete() {
        this.setStatus("requested");
    }

    /**
     * Getters and Setters
     */
    public String getID(){ return this.ID; }

    public void setID(String ID){
        this.ID = ID;
    }

    public Double getPrice(){ return this.price; }

    public void setPrice(Double price){ this.price = price; }

    public Double getLowestPrice(){ return this.lowestPrice; }

    public void setLowestPrice(Double price){ this.lowestPrice = price; }

    public String getTitle(){ return this.title; }

    public void setTitle(String title){ this.title = title; }

    public String getDescription(){
        return this.description;
    }

    public void setDescription(String description){ this.description = description; }

    public String getStatus(){
        return this.status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getRequesterUserName(){ return this.requesterUserName; }

    public void setRequesterUserName(String requesterUserName){ this.requesterUserName = requesterUserName; }

    public String getProviderUserName(){ return this.providerUserName; }

    public void setProviderUserName(String providerUserName){ this.providerUserName = providerUserName; }

    public ArrayList<ArrayList<String>> getBidList(){ return this.bidList; }

    public void setBidList(ArrayList<ArrayList<String>> bidList){ this.bidList = bidList; }

    public GeoPoint get_User_location(){
        return location.getUser_location();
    }

    public GeoPoint get_Task_location(){
        return location.getTask_location();
    }

    public Double get_distance(){
        return location.get_distance();
    }

    public void set_distance(double distance){
        location.setDistance(distance);
    }

}
