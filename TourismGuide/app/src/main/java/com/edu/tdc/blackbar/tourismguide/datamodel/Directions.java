package com.edu.tdc.blackbar.tourismguide.datamodel;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Shiro on 08/12/2016.
 */

public class Directions {

    private String distance;
    private String duration;
    private String endAddress;
    private ArrayList<StepsDirections> steps ;
    private String status;
    private String copyrights;
    private LatLng endLocation;


    public Directions(){
        this.distance = null;
        this.duration = null;
        this.endAddress = null;
        this.steps = null;
        this.status = "NOT_FOUND";
        this.copyrights = null;
        this.endLocation = null;
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

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public ArrayList<StepsDirections> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<StepsDirections> steps) {
        this.steps = steps;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCopyrights() {
        return copyrights;
    }

    public void setCopyrights(String copyrights) {
        this.copyrights = copyrights;
    }

    public LatLng getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LatLng endLocation) {
        this.endLocation = endLocation;
    }
}
