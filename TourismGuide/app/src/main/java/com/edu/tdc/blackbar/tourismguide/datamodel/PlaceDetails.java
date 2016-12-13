package com.edu.tdc.blackbar.tourismguide.datamodel;

import java.util.ArrayList;

/**
 * Created by Shiro on 01/12/2016.
 */

public class PlaceDetails {

    private String placeID;
    private String name;
    private ArrayList<Review> review;
    private ArrayList<String> photo;
    private double rating;
    private String openNow;
    private String website;
    private String address;
    private String phoneNumber;

    public PlaceDetails(String placeID, String name, ArrayList<Review> review, ArrayList<String> photo, double rating, String openNow, String website) {
        this.placeID = placeID;
        this.name = name;
        this.review = review;
        this.photo = photo;
        this.rating = rating;
        this.openNow = openNow;
        this.website = website;
    }
    public  PlaceDetails(){
        this.placeID = "unKnow";
        this.name = "unKnow";
        this.review = null;
        this.photo = null;
        this.rating = -1;
        this.website = "unKnow";
        this.openNow = "nodata";
        this.address = "unKnow";
        this.phoneNumber="unKnow";
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Review> getReview() {
        return review;
    }

    public void setReview(ArrayList<Review> review) {
        this.review = review;
    }

    public ArrayList<String> getPhoto() {
        return photo;
    }

    public void setPhoto(ArrayList<String> photo) {
        this.photo = photo;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getOpenNow() {
        return openNow;
    }

    public void setOpenNow(String openNow) {
        this.openNow = openNow;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
