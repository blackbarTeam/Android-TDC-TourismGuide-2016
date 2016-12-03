package com.edu.tdc.blackbar.tourismguide.datamodel;

import java.util.ArrayList;

/**
 * Created by Shiro on 01/12/2016.
 */

public class NearByPlaces {
    private String placeID;
    private Double latitude;
    private Double longitude;
    private ArrayList<String> types;
    private String name;

    public NearByPlaces(){
        this.placeID ="";
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.types = null;
    }

    public NearByPlaces(String placeID, Double latitude, Double longitude, ArrayList<String> types, String name) {
        this.placeID = placeID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.types = types;
        this.name = name;
    }

    public String getPlaceID() {
        return placeID;
    }

    public ArrayList<String> getType() {
        return types;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public void setType(ArrayList<String> type) {
        this.types = type;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
