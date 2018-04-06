/* UserUnitTest
 *
 * Version 1.0
 *
 * March 15, 2018
 *
 * Copyright (c) 2018 Team 12 CMPUT 301. University of Alberta - All Rights Reserved.
 * You may use distribute or modify this code under terms and condition of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of licence in this project. Otherwise please contact contact sfeng3@ualberta.ca.
 */

package com.example.dada.UnitTest;

import android.os.AsyncTask;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;

import com.example.dada.Model.OnAsyncTaskCompleted;
import com.example.dada.Model.User;
import com.example.dada.R;
import com.example.dada.View.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotEquals;


public class UserUnitTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private Solo solo;

    /**
     * Mock callback method
     */
    OnAsyncTaskCompleted mockTask = new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {

        }
    };

    public UserUnitTest() {
        super("com.example.dada.View", LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        Log.d("TAG1", "setUp()");
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    /*
    This method is only here for making
    Junit test method work on robotinum
    http://stackoverflow.com/questions/11390276/android-junit-tests-not-detecting-in-robotium
    Author: BlackHatSamurai
     */
    public void testClickButton() {
        solo.enterText((EditText) solo.getView(R.id.edit_text_login_username), "balabl");
        solo.clickOnButton("Requester");
        solo.clickOnButton("Login");
        assertTrue(solo.waitForText("User does not exist, please signup"));
    }


    /**
     * Test cases for search user exist task
     */
    public void testSearchUserExist() {
        String query = String.format(
                "{\n" +
                        "    \"query\": {\n" +
                        "       \"term\" : { \"userName\" : \"%s\" }\n" +
                        "    }\n" +
                        "}", "sfeng3");
        User.SearchUserExistTask task = new User.SearchUserExistTask();
        task.execute(query);
        // Hang around till it's done
        AsyncTask.Status taskStatus;
        do {
            taskStatus = task.getStatus();
        } while (taskStatus != AsyncTask.Status.FINISHED);

        try {
            assertTrue(task.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test constrain of the username in the model
     */
    @Test
    public void testTitle(){
        User user = new User("user", "123456789", "user@email.ca");
        try {
            user.setUserName("longusername");
        } catch (Exception e){
            e.printStackTrace();
        }
        assertNotEquals(user.getUserName(), "longusername");
        assertEquals(user.getUserName(), "user");
    }
}
