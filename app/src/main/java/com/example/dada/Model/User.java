/* User
 *
 * Version 1.0
 *
 * March 15, 2018
 *
 * Copyright (c) 2018 Team 12 CMPUT 301. University of Alberta - All Rights Reserved.
 * You may use distribute or modify this code under terms and condition of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of licence in this project. Otherwise please contact contact sfeng3@ualberta.ca.
 */

package com.example.dada.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.example.dada.Constant;
import com.example.dada.Util.UserUtil;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.IOException;
import java.util.ArrayList;

import io.searchbox.client.JestResult;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

public class User {

    private String ID;
    private String userName;
    private String phone;
    private String email;

    private int type;
    private String profile_photo;
    private ArrayList<Double> ratings = new ArrayList<>();

    private transient static JestDroidClient client;

    public User(String userName, String phone, String profile_photo, String email){
        this.userName = userName;
        this.phone = phone;
        this.profile_photo = profile_photo;
        this.phone = email;
    }

    /**
     * Instantiates a new User.
     */
    public User() {

    }

    /**
     * Instantiates a new User.
     */
    public User(String userName, String phone, String email) {
        this.userName = userName;
        this.phone = phone;
        this.email = email;
    }

    /**
     * Static class that check user profile
     */
    public static class SearchUserExistTask extends AsyncTask<String, Void, Boolean> {

        /**
         * Check if username has been taken
         * @param query the username to be searched
         * @return True or False
         */
        @Override
        protected Boolean doInBackground(String... query) {
            verifySettings();

            User user = new User();
            Search search = new Search.Builder(query[0])
                    .addIndex("team12")
                    .addType("user")
                    .build();
            try {
                JestResult result = client.execute(search);
                if (result.isSucceeded()) {
                    user = result.getSourceAsObject(User.class);
                    if (user != null) {
                        Log.i("Debug", "Username has been taken");
                        return true;
                    }
                    Log.i("Debug", "Successful");
                } else {
                    Log.i("Debug", "The search query failed to find any user that matched.");
                }
            } catch (Exception e) {
                Log.i("Debug", "Something went wrong when we tried to communicate with the elastic search server!");
            }
            return false;
        }
    }

    /**
     * Static class that create user profile
     */
    public static class CreateUserTask extends AsyncTask<User, Void, User> {
        /**
         * The Listener.
         */
        public OnAsyncTaskCompleted listener;

        /**
         * Instantiates a new Create user task.
         *
         * @param listener the listener
         */
        public CreateUserTask(OnAsyncTaskCompleted listener) {
            this.listener = listener;
        }

        /**
         * Update the user profile to the server
         * @param user the user object to be updated
         * @return user
         */
        @Override
        protected User doInBackground(User... user) {
            verifySettings();
            User newUser = new User();
            for (User u : user) {
                Index index = new Index.Builder(u)
                        .index("team12")
                        .type("user")
                        .id(u.getID())
                        .build();
                try {
                    DocumentResult result = client.execute(index);
                    if (result.isSucceeded()) {
                        u.setID(result.getId());
                        newUser = u;
                        Log.i("Debug", "Successful create user");
                    } else {
                        Log.i("Debug", "Elastic search was not able to add the update user.");
                    }
                } catch (Exception e) {
                    Log.i("Error", "We failed to add user profile to elastic search!");
                    e.printStackTrace();
                }
            }
            return newUser;
        }

        @Override
        protected void onPostExecute(User user) {
            if (listener != null) {
                listener.onTaskCompleted(user);
            }
        }
    }

    /**
     * Static class that update user profile
     */
    public static class UpdateUserTask extends AsyncTask<User, Void, User> {
        /**
         * The Listener.
         */
        public OnAsyncTaskCompleted listener;

        /**
         * Instantiates a new Update user task.
         *
         * @param listener the listener
         */
        public UpdateUserTask(OnAsyncTaskCompleted listener) {
            this.listener = listener;
        }
        @Override
        protected User doInBackground(User... users) {
            verifySettings();
            // Serialize object into Json string
            String query = UserUtil.serializer(users[0]);
            Index index = new Index.Builder(query)
                    .index("team12").type("user").id(users[0].getID()).build();

            try {
                DocumentResult result = client.execute(index);
                if (result.isSucceeded()) {
                    Log.i("Debug", "Successful update user profile");
                } else {
                    Log.i("Error", "We failed to update user profile to elastic search!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return users[0];
        }

        @Override
        protected void onPostExecute(User user) {
            if (listener != null) {
                listener.onTaskCompleted(user);
            }
        }
    }

    /**
     * Static class that get user profile
     */
    public static class GetUserProfileTask extends AsyncTask<String, Void, User> {
        /**
         * The Listener.
         */
        public OnAsyncTaskCompleted listener;

        /**
         * Instantiates a new Get user profile task.
         *
         * @param listener the listener
         */
        public GetUserProfileTask(OnAsyncTaskCompleted listener) {
            this.listener = listener;
        }
        /**
         * Get the user profile from the server
         * @param query the username to be searched
         * @return the mathed user obejct
         */
        @Override
        protected User doInBackground(String... query) {
            verifySettings();

            User user = new User();
            Search search = new Search.Builder(query[0])
                    .addIndex("team12")
                    .addType("user")
                    .build();
            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    User getUser = result.getSourceAsObject(User.class);
                    user = getUser;
                    Log.i("Debug", "Successful get user profile");
                    if (user == null) {
                        Log.i("Debug", "fail to deserilize");
                    }
                } else {
                    Log.i("Error", "The search query failed to find any user that matched.");
                }
            } catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elastic search server!");
            }
            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            if (listener != null) {
                listener.onTaskCompleted(user);
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

    public String getUserName(){
        return this.userName;
    }

    public void setUserName(String userName){
        if (userName.length() > 8){
            throw new IllegalArgumentException("username exceeds 8 characters");
        }
        else {
            this.userName = userName;
        }
    }

    public String getID(){
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getType(){
        return type;
    }

    public void setType(int type){
        this.type = type;
    }

//    public Image getProfile_photo(){
//        return profile_photo;
//    }
//
//    public void setProfile_photo(Image profile_photo){
//        this.profile_photo = profile_photo;
//    }

    public String getPhone(){
        return this.phone;
    }

    public void setPhone(String phone){ this.phone = phone; }

    public String getEmail(){ return this.email; }

    public void setEmail(String email){ this.email = email; }

    public void setProfile_photo(String profile_photo) {this.profile_photo = profile_photo;}

    public Bitmap getProfile_photo() {
        try {
            byte[] encodeByte = Base64.decode(profile_photo, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void addRating(double rating){
        ratings.add(rating);
    }

    public ArrayList<Double> getRatings(){
        return ratings;
    }

}
