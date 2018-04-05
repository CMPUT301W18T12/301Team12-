/* NormalTask
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

import java.util.ArrayList;

public class NormalTask extends Task {

    public NormalTask() {
    }

    public NormalTask(String title, String description, String requesterUserName, String status) {
        super(title, description, requesterUserName, status);
    }

    public NormalTask(String title, String description, String requesterUserName, String status, Locations location) {
        super(title, description, requesterUserName, status, location);
    }

    public NormalTask(String requesterUserName, String providerUserName, double price) {
        super(requesterUserName, providerUserName, price);
    }

    public NormalTask(String requesterUserName, String providerUserName, double price, Locations location) {
        super(requesterUserName, providerUserName, price, location);
    }

    public NormalTask(String requesterUserName, ArrayList<String> providerList, double price) {
        super(requesterUserName, providerList, price);
    }

    public NormalTask(String requesterUserName, ArrayList<String> providerList, double price, Locations location) {
        super(requesterUserName, providerList, price, location);
    }

}
