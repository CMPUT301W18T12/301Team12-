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
import android.widget.EditText;

import com.example.dada.R;
import com.example.dada.View.RequesterMainActivity;
import com.example.dada.View.RequesterAddTaskActivity;
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
        // add a long title task.
        solo.assertCurrentActivity("Wrong Activity", RequesterAddTaskActivity.class);
        solo.enterText((EditText) solo.getView(R.id.editText_requester_add_task_title), "0123456789012345678901234567890");
        solo.enterText((EditText) solo.getView(R.id.editText_requester_add_task_description), "description");
        solo.clickOnButton("DONE");
        assertTrue(solo.waitForText("max task length is 30"));
        solo.assertCurrentActivity("Wrong Activity", RequesterAddTaskActivity.class);

        // add a valid task.
        solo.assertCurrentActivity("Wrong Activity", RequesterAddTaskActivity.class);
        solo.enterText((EditText) solo.getView(R.id.editText_requester_add_task_title), "validtitle");
        solo.enterText((EditText) solo.getView(R.id.editText_requester_add_task_description), "description");
        solo.clickOnButton("DONE");
        solo.assertCurrentActivity("Wrong Activity", RequesterMainActivity.class);
    }


    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
