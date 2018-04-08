package com.example.dada.Model.Task;

import android.graphics.Bitmap;

import com.example.dada.Exception.TaskException;
import com.example.dada.Model.Locations;

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

    public DoneTask(String requesterUserName, String providerUserName, double price, Bitmap img) {
        super(requesterUserName, providerUserName, price, img);
    }

    public DoneTask(String requesterUserName, String providerUserName, double price, Bitmap img, List<Double> coordinates) {
        super(requesterUserName, providerUserName, price, img, coordinates);
    }
}
