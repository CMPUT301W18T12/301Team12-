/* CompletedTask
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

import com.example.dada.Model.Locations;

/**
 * Task that has been completed in the past.
 *
 * @version 1.0
 * @see Task
 */
public class CompletedTask extends Task {

    public CompletedTask(String requesterUserName, String providerUserName, double price) {
        super(requesterUserName, providerUserName, price);
    }

    public CompletedTask(String requesterUserName, String providerUserName, double price, Locations location) {
        super(requesterUserName, providerUserName, price, location);
    }
}