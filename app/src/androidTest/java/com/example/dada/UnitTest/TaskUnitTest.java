/* TaskUnitTest
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

import android.location.Location;
import android.media.Image;
import android.os.AsyncTask;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;

import com.example.dada.Exception.TaskException;
import com.example.dada.Model.OnAsyncTaskCompleted;
import com.example.dada.Model.Task.AssignedTask;
import com.example.dada.Model.Task.BiddedTask;
import com.example.dada.Model.Task.NormalTask;
import com.example.dada.Model.Task.RequestedTask;
import com.example.dada.Model.Task.Task;
import com.example.dada.R;
import com.example.dada.View.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

/**
 * Unit test cases for task model Since the controller class are designed to be as thick as
 * possible, all business logic are inside the model class, which fits the MVC pattern. Therefore,
 * it's pretty much no need to test controller class.
 */
public class TaskUnitTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private Solo solo;

    /**
     * Mock callback method
     */
    OnAsyncTaskCompleted mockTask = new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {

        }
    };

    /**
     * Instantiates a new Task test.
     */
    public TaskUnitTest() {
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

    /**
     * This method is only here for making Junit test method work on robotinum
     * http://stackoverflow.com/questions/11390276/android-junit-tests-not-detecting-in-robotium
     * Author: BlackHatSamurai
     */
    public void testClickButton() {
        solo.enterText((EditText) solo.getView(R.id.edit_text_login_username), "balabl");
        solo.clickOnButton("Requester");
        solo.clickOnButton("Login");
        assertTrue(solo.waitForText("User does not exist, please signup"));
    }



    /**
     * Test requester confirm task's request.
     */
    public void testProviderConfirmTaskComplete() {
        Task requester = new RequestedTask("titile1", "description", "sfeng3");
        assertEquals(requester.getStatus(), "requested");
    }

    /**
     * Test requester assign provider.
     */
    public void testRequesterAssignProvider() throws TaskException {
        Task request = new RequestedTask("title1", "description1", "sfeng3");
        request.providerBidTask("yz6_1", 1);
        request.providerBidTask("yz6_2",2);
        try {
            request.requesterAssignProvider("yz6_1");
        } catch (TaskException e) {
            e.printStackTrace();
        }
        assertEquals(request.getProviderUserName(), "yz6_1");
    }

    /**
     * Test provider bid task.
     */
    public void testRequesterBidTask() throws TaskException {
        Task request = new RequestedTask("title1", "description1", "sfeng3");
        request.providerBidTask("yz6", 6);
        assertEquals("bidded", request.getStatus());
    }

    /**
     * Test requester cancel provider's assigned task.
     */
    public void testRequesterCancelTask() throws TaskException{
        // if only one bid
        Task assign = new AssignedTask("hq1", "sfeng3", 100);
        assign.requesterCancelAssigned("sfeng3");
        assertEquals("requested", assign.getStatus());

        // if multiple bids
        Task request = new RequestedTask("title1", "description1", "sfeng3");
        request.providerBidTask("yz6", 6);
        request.providerBidTask("yz7", 7);
        request.providerBidTask("yz8", 8);
        request.requesterAssignProvider("yz8");
        request.requesterCancelAssigned("sfeng3");
        assertEquals("bidded", request.getStatus());
    }

    /**
     * Test requester mark task as done.
     */
    public void testRequesterDoneTask() throws TaskException{
        Task assign = new AssignedTask("hq1", "sfeng3", 100);
        assign.requesterDoneTask();
        assertEquals("done", assign.getStatus());
    }

    /**
     * Test constrain of the task title
     */
    @Test
    public void testTitle(){
        Task request = new RequestedTask("title1", "description1", "sfeng3");
        try {
            request.setTitle("1234567890123456789012345678901");
        } catch (Exception e){
            e.printStackTrace();
        }
        assertNotEquals(request.getTitle(), "1234567890123456789012345678901");
        assertEquals(request.getTitle(), "title1");
    }

    /**
     * Test description is too long to test
     */
}
