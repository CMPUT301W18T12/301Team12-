/* Locations
 *
 * Version 1.0
 *
 * March 15, 2018
 *
 * Copyright (c) 2018 Team 12 CMPUT 301. University of Alberta - All Rights Reserved.
 * You may use distribute or modify this code under terms and condition of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of licence in this project. Otherwise please contact contact sfeng3@ualberta.ca.
 */
package com.example.dada.Model;

import org.osmdroid.util.GeoPoint;


/**
 * The Location class will implements in
 * the requester and provider classes, contains the geo-coordinate
 * of task location and destination.
 */
public class Locations {
    private GeoPoint user_location;
    private GeoPoint task_location;
    private double distance;

    /**
     * Instantiates a new geo_point.
     *
     * @param task_location the coordinate of the task
     */
    public Locations(GeoPoint task_location){
        this.task_location = task_location;
    }

    /**
     * Instantiates a new geo_point.
     *
     * @param user_location     the user coordinate
     * @param task_location the coordinate of the task
     */

    public Locations(GeoPoint user_location, GeoPoint task_location){
        this.user_location = user_location;
        this.task_location = task_location;
    }

    /**
     * Gets location of task.
     *
     * @return the task coordinate
     */

    public GeoPoint getTask_location(){
        return this.task_location;
    }

    /**
     * Gets location of user.
     *
     * @return the user's coordinate
     */
    public GeoPoint getUser_location(){
        return this.user_location;
    }

    /**
     * Gets distance between task and user.
     *
     * @return the distance between coordinates
     */
    public Double get_distance(){
        return this.distance;
    }

    /**
     * Set the distance between points.
     * @param distance
     */
    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
