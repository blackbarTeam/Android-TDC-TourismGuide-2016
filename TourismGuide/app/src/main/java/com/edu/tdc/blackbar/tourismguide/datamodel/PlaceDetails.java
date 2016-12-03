package com.edu.tdc.blackbar.tourismguide.datamodel;

import java.util.ArrayList;

/**
 * Created by Shiro on 01/12/2016.
 */

public class PlaceDetails {

    private String placeID;
    private String name;
    private ArrayList<String> review;
    private ArrayList<String> photo;
    private String rating;
    private boolean openNow;
    private String website;

    public PlaceDetails(String placeID, String name, ArrayList<String> review, ArrayList<String> photo, String rating, boolean openNow, String website) {
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
        this.rating = "unKnow";
        this.website = "unKnow";
        this.openNow = false;
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

    public ArrayList<String> getReview() {
        return review;
    }

    public void setReview(ArrayList<String> review) {
        this.review = review;
    }

    public ArrayList<String> getPhoto() {
        return photo;
    }

    public void setPhoto(ArrayList<String> photo) {
        this.photo = photo;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
