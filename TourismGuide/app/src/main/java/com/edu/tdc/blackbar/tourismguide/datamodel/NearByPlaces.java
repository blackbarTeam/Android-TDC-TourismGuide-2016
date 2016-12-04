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
    private String address;

    public NearByPlaces(){
        this.placeID ="";
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.types = null;
        this.name="Unknow";
        this.address="Unknow";
    }

    public NearByPlaces(String placeID, Double latitude, Double longitude, ArrayList<String> types, String name, String address) {
        this.placeID = placeID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.types = types;
        this.name = name;
        this.address = address;
    }

    public String getPlaceID() {
        return placeID;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<String> getTypes() {
        return types;
    }
}
