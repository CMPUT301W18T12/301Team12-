package com.example.dada.Model.Task;

import android.graphics.Bitmap;

import com.example.dada.Exception.TaskException;
import com.example.dada.Model.Locations;

/**
 * Task that has been Confirmed done by the requester
 *
 * @version 1.0
 * @see Task
 */

public class DoneTask extends Task{
    public DoneTask(String requesterUserName, String providerUserName, double price){
        super(requesterUserName, providerUserName, price);
    }

    public DoneTask(String requesterUserName, String providerUserName, double price, Locations location) {
        super(requesterUserName, providerUserName, price, location);
    }
}
