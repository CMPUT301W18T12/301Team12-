/* RequesterAddTaskTest
 *
 * Version 1.0
 *
 * March 15, 2018
 *
 * Copyright (c) 2018 Team 12 CMPUT 301. University of Alberta - All Rights Reserved.
 * You may use distribute or modify this code under terms and condition of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of licence in this project. Otherwise please contact contact sfeng3@ualberta.ca.
 */

package com.example.dada.ActivityTest;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.example.dada.R;
import com.example.dada.View.RequesterMainActivity;
import com.robotium.solo.Solo;

public class RequesterAddTaskTest extends ActivityInstrumentationTestCase2{
    private Solo solo;

    public RequesterAddTaskTest(){
        super(com.example.dada.View.RequesterAddTaskActivity.class);
    }

    public void testStart() throws Exception {
        Activity activity = getActivity();

    }

    @Override
    public void setUp() throws Exception {
        Log.d("TAG1", "setUp()");
        solo = new Solo(getInstrumentation(), getActivity());
    }

    /**
     * Test for add task
     */
    public void Testadd(){
        solo.assertCurrentActivity("Wrong Activity", RequesterAddTaskTest.class);
        solo.getView(R.layout.activity_requester_add_task);
        solo.clickOnButton(R.id.newTask_done_button);
        solo.assertCurrentActivity("Wrong Activity", RequesterMainActivity.class);
    }


    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
