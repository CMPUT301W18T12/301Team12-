/* RequesterMainTest
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
import android.view.View;
import android.widget.EditText;

import com.example.dada.R;
import com.example.dada.View.RequesterAddTaskActivity;
import com.example.dada.View.RequesterEditTaskActivity;
import com.example.dada.View.RequesterMainActivity;
import com.robotium.solo.Solo;


public class RequesterMainTest extends ActivityInstrumentationTestCase2{
    private Solo solo;

    public RequesterMainTest(){
        super(com.example.dada.View.RequesterMainActivity.class);
    }

    public void testStart() throws Exception {
        Activity activity = getActivity();

    }

    @Override
    public void setUp() throws Exception {
        Log.d("TAG1", "setUp()");
        solo = new Solo(getInstrumentation(), getActivity());
    }


    public void testAdd(){
        solo.assertCurrentActivity("wrong activity", RequesterMainActivity.class);
        View fab = getActivity().findViewById(R.id.fab_requester_main);
        solo.clickOnView(fab);

        solo.assertCurrentActivity("Wrong Activity", RequesterAddTaskActivity.class);
        solo.enterText((EditText) solo.getView(R.id.editText_requester_add_task_title), "validtitle");
        solo.enterText((EditText) solo.getView(R.id.editText_requester_add_task_description), "description");
        solo.clickOnButton("DONE");
        solo.assertCurrentActivity("Wrong Activity", RequesterMainActivity.class);
    }

    /**
     * please make sure there is a item exist otherwise the test will failed
     * Delete first item in list.
     */
    public void testDelete(){
        solo.assertCurrentActivity("Wrong Activity", RequesterMainActivity.class);
        try {
            solo.clickLongInList(0);
        } catch (Exception e){
            Log.i("Test-------->", "testMain: view has no value");
        }
        solo.clickOnButton("Delete");
    }

    /**
     * please make sure there is a item exist otherwise the test will failed
     * test to view the detail of the first item in the listview
     *
     * there is no need to do intent test for requester detail activity after this one
     */
    public void testView(){
        testAdd();
        solo.assertCurrentActivity("wrong activity", RequesterMainActivity.class);
        try{
            solo.clickInList(0);
        } catch (Exception e){
            Log.i("Test ------>", "testView: view has no value");
        }
        solo.assertCurrentActivity("wrong activity", RequesterMainActivity.class);
    }

    /**
     * please make sure there is a item exist otherwise the test will failed
     * test to edit the detail of the first item in the listview
     *
     * there is no need to do intent test for requester edit activity after this one
     */
    public void testEdit(){

        solo.assertCurrentActivity("Wrong Activity", RequesterMainActivity.class);
        try {
            solo.clickLongInList(0);
        } catch (Exception e){
            Log.i("Test-------->", "testMain: view has no value");
        }
        solo.clickOnButton("Edit");

        solo.assertCurrentActivity("Wrong Activity", RequesterEditTaskActivity.class);
        solo.enterText((EditText) solo.getView(R.id.editText_requester_add_task_title), "title");
        solo.enterText((EditText) solo.getView(R.id.editText_requester_add_task_description), "description");
        solo.clickOnButton("DONE");
        solo.assertCurrentActivity("Wrong Activity", RequesterMainActivity.class);
    }




    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}

