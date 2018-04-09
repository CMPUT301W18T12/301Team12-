/* RequestedTask
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
 * Task that has been requested by the requester
 *
 * @version 1.0
 * @see Task
 */
public class RequestedTask extends Task {

    public RequestedTask() {
    }

    /**
     * Constructor for pending request
     * @param title         title of the task
     * @param description   description of the task
     */
    public RequestedTask(String title, String description, String requesterUserName, List<Double> coordinates) {
        super(title, description, requesterUserName, "requested", coordinates);
    }

    public RequestedTask(String title, String description, String requesterUserName, ArrayList<Bitmap> img, List<Double> coordinates) {
        super(title, description, requesterUserName, "requested", img, coordinates);
    }

    public RequestedTask(String title, String description, String requesterUserName, ArrayList<Bitmap> img) {
        super(title, description, requesterUserName, "requested", img);
    }


    public RequestedTask(String title, String description, String requesterUserName) {
        super(title, description, requesterUserName, "requested");
    }
    /**
     * Provider bids the requested task.
     *
     * @param providerUserName the provider user name who bids the requested task
     */
    @Override
    public void providerBidTask(String providerUserName, double price) throws TaskException {
        super.providerBidTask(providerUserName, price);
    }

}
