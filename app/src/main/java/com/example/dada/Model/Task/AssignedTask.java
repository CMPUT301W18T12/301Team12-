/* AssignedTask
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
 * Task that has been sent by the requester,
 * and accepted by one or more driver. Also,
 * it has been confirmed by the requester as well.
 *
 * @version 1.0
 * @see Task
 */
public class AssignedTask extends Task{

    public AssignedTask(String requesterUserName, String providerUserName, double price) {
        super(requesterUserName, providerUserName, price);
    }

    public AssignedTask(String requesterUserName, String providerUserName, double price, ArrayList<Bitmap> img) {
        super(requesterUserName, providerUserName, price, img);
    }

    public AssignedTask(String requesterUserName, String providerUserName, double price, ArrayList<Bitmap> img, List<Double> coordinates) {
        super(requesterUserName, providerUserName, price, img, coordinates);
    }

    public AssignedTask(String requesterUserName, String providerUserName, double price, List<Double> coordinates) {
        super(requesterUserName, providerUserName, price, coordinates);
    }

    public void requesterDoneTask() throws TaskException {
        super.requesterDoneTask();
    }
}