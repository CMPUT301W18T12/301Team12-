/* ProviderMainTest
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
import com.example.dada.View.ProviderMainActivity;
import com.robotium.solo.Solo;

public class ProviderMainTest extends ActivityInstrumentationTestCase2{
    private Solo solo;

    public ProviderMainTest(){
        super(com.example.dada.View.ProviderMainActivity.class);
    }

    public void testStart() throws Exception {
        Activity activity = getActivity();

    }

    /**
     * please make sure there is a item exist otherwise the test will failed
     * test to view the detail of the first item in the listview
     */
    public void testView(){
        solo.assertCurrentActivity("wrong activity", ProviderMainActivity.class);
        try{
            solo.clickInList(0);
        } catch (Exception e){
            Log.i("Test ------>", "testView: view has no value");
        }

        solo.enterText((EditText) solo.getView(R.id.editTextInput), "100");
        solo.clickOnButton("Bidded");
        solo.assertCurrentActivity("wrong activity", ProviderMainActivity.class);
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
}
