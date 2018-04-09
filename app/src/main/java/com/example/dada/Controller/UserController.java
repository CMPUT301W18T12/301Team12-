/* UserController
 *
 * Version 1.0
 *
 * March 15, 2018
 *
 * Copyright (c) 2018 Team 12 CMPUT 301. University of Alberta - All Rights Reserved.
 * You may use distribute or modify this code under terms and condition of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of licence in this project. Otherwise please contact contact sfeng3@ualberta.ca.
 */

package com.example.dada.Controller;

import android.util.Log;

import com.example.dada.Exception.UserException;
import com.example.dada.Model.OnAsyncTaskCompleted;
import com.example.dada.Model.User;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * User model's controller, a glue between
 * Activity and Model. Give access for activity(View)
 * to modify and update model.
 */
public class UserController {

    OnAsyncTaskCompleted listener;

    /**
     * The constructor
     * @param listener the task to do after async task is done.
     */
    public UserController(OnAsyncTaskCompleted listener) {
        this.listener = listener;
    }

    /**
     * Create a new user
     * @param user The user to be created
     * @throws UserException Raise exception when username has been taken
     */
    public void addUser(User user) throws UserException {
        String query = String.format(
                "{\n" +
                        "    \"query\": {\n" +
                        "       \"term\" : { \"userName\" : \"%s\" }\n" +
                        "    }\n" +
                        "}", user.getUserName());

        User.CreateUserTask task = new User.CreateUserTask(listener);
        User.SearchUserExistTask checkTask = new User.SearchUserExistTask();
        checkTask.execute(query);

        try {
            if (checkTask.get()) {
                throw new UserException("Username has been taken");
            } else {
                // generate document ID
                user.setID(UUID.randomUUID().toString());
                task.execute(user);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update user profile
     * @param user The user to be created
     * @param oldUserName The old user name
     * @throws UserException Raise exception when username has been taken
     */
    public void updateUser(User user, String oldUserName) throws UserException{
        String query = String.format(
                "{\n" +
                        "    \"query\": {\n" +
                        "       \"term\" : { \"userName\" : \"%s\" }\n" +
                        "    }\n" +
                        "}", user.getUserName());
        User.UpdateUserTask task = new User.UpdateUserTask(listener);
        User.SearchUserExistTask checkTask = new User.SearchUserExistTask();
        checkTask.execute(query);

        try {
            if (checkTask.get() && !oldUserName.equals(user.getUserName())) {
                throw new UserException("Username has been taken");
            } else {
                task.execute(user);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add user rating
     * @param user        The user to be created
     * @throws UserException Raise exception when username has been taken
     */
    public void addUserRating(User user) throws UserException{

        User.UpdateUserTask task = new User.UpdateUserTask(listener);
        task.execute(user);
    }

    /**
     * Retrive user profile from the server
     * @param username the username to search
     * @return The user object
     */
    public User getUser(String username) {
        User user = new User();
        String query = String.format(
                "{\n" +
                        "    \"query\": {\n" +
                        "       \"term\" : { \"userName\" : \"%s\" }\n" +
                        "    }\n" +
                        "}", username);
        User.GetUserProfileTask task = new User.GetUserProfileTask(listener);
        task.execute(query);

        try {
            Log.i("Error", "Searching");
            user = task.get();
        } catch (Exception e) {
            Log.i("Error", "Fail to get");
        }
        return user;
    }
}
