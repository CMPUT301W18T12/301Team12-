/* DoneTask
 *
 * Version 1.0
 *
 * March 15, 2018
 *
 * Copyright (c) 2018 Team 12 CMPUT 301. University of Alberta - All Rights Reserved.
 * You may use distribute or modify this code under terms and condition of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of licence in this project. Otherwise please contact contact sfeng3@ualberta.ca.
 */
package com.example.dada.Model.Task;

import android.graphics.Bitmap;

import com.example.dada.Exception.TaskException;
import com.example.dada.Model.Locations;

import java.util.ArrayList;
import java.util.List;

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

    public DoneTask(String requesterUserName, String providerUserName, double price, List<Double> coordinates) {
        super(requesterUserName, providerUserName, price, coordinates);
    }

    public DoneTask(String requesterUserName, String providerUserName, double price, ArrayList<Bitmap> img) {
        super(requesterUserName, providerUserName, price, img);
    }

    public DoneTask(String requesterUserName, String providerUserName, double price, ArrayList<Bitmap> img, List<Double> coordinates) {
        super(requesterUserName, providerUserName, price, img, coordinates);
    }
}
