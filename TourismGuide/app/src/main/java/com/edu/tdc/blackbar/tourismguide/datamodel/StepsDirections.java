package com.edu.tdc.blackbar.tourismguide.datamodel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.face.Landmark;

import java.util.ArrayList;

/**
 * Created by Shiro on 08/12/2016.
 */

public class StepsDirections {
    private String distance;
    private String duration;
    private String instructions;
    private ArrayList<LatLng> polyPoint;

    public StepsDirections(){
        this.distance = null;
        this.duration = null;
        this.instructions = null;
        this.polyPoint = new ArrayList<LatLng>();
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public ArrayList<LatLng> getPolyPoint() {
        return polyPoint;
    }

    public void setPolyPoint(ArrayList<LatLng> polyPoint) {
        this.polyPoint.addAll(polyPoint);
    }
}
