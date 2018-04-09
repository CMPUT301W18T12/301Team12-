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

        solo.enterText((EditText) solo.getView(R.id.));
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
