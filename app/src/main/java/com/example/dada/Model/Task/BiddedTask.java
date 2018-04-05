/* BiddedTask
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

import com.example.dada.Exception.TaskException;
import com.example.dada.Model.Locations;

import java.util.ArrayList;

/**
 * Task that has been sent by the requester,
 * and also bidded by one or more provider, but
 * has not been assigned by the requester.
 *
 * @version 1.0
 * @see Task
 */
public class BiddedTask extends Task {

    public BiddedTask(String requesterUserName, String providerUserName, Double price, Locations location) {
        super(requesterUserName, providerUserName, price, location);
    }

    public BiddedTask(String requesterUserName, String providerUserName, Double price) {
        super(requesterUserName, providerUserName, price);
    }

    public BiddedTask(String requesterUserName, ArrayList<String> providerList, Double price) {
        super(requesterUserName, providerList, price);
    }

    public BiddedTask(String requesterUserName, ArrayList<String> providerList, Double price, Locations location) {
        super(requesterUserName, providerList, price, location);
    }

    @Override
    public void requesterAssignProvider(String providerUserName) throws TaskException {
        super.requesterAssignProvider(providerUserName);
    }
}