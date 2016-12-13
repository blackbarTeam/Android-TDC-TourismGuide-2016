package com.edu.tdc.blackbar.tourismguide.JsonParse;


import android.graphics.Point;
import android.util.Log;

import com.edu.tdc.blackbar.tourismguide.datamodel.Directions;
import com.edu.tdc.blackbar.tourismguide.datamodel.NearByPlaces;
import com.edu.tdc.blackbar.tourismguide.datamodel.PlaceDetails;
import com.edu.tdc.blackbar.tourismguide.datamodel.Review;
import com.edu.tdc.blackbar.tourismguide.datamodel.StepsDirections;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Shiro on 02/12/2016.
 */

public class JSONParsePlaces {

    public NearByPlaces JSONParseNearBy(JSONObject objJSON) {
      //  Log.d("testString",stringJSON);
        NearByPlaces place = new NearByPlaces();
        try {
                JSONObject geometry = objJSON.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");

                //  add lat long
                place.setLatitude(location.getDouble("lat"));
                place.setLongitude(location.getDouble("lng"));
                //add  name
                place.setName(objJSON.getString("name"));
                // add address
                if(objJSON.has("vicinity"))
                    place.setAddress(objJSON.getString("vicinity"));
                //  add place ID
                if(objJSON.has("place_id"))
                    place.setPlaceID(objJSON.getString("place_id"));
                // add type
                 if(objJSON.has("types")) {
                     JSONArray types = objJSON.getJSONArray("types");
                     ArrayList<String> stringTypes = new ArrayList<String>();
                     for (int j = 0; j < types.length(); j++) {
                         stringTypes.add(types.getString(j));
                     }

                     place.setType(stringTypes);
                 }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return place;
    }

    public PlaceDetails DetailsParse (String string){
        PlaceDetails place = new PlaceDetails();

        try {
            JSONObject rootJSON = new JSONObject(string);
            JSONObject objJSON = rootJSON.getJSONObject("result");
            if(objJSON.has("opening_hours")) {
                JSONObject openNow = objJSON.getJSONObject("opening_hours");
                place.setOpenNow(openNow.getString("open_now"));
            }
            place.setName(objJSON.getString("name"));
            place.setAddress(objJSON.getString("formatted_address"));
            place.setPlaceID(objJSON.getString("place_id"));
            if(objJSON.has("website"))
                place.setWebsite(objJSON.getString("website"));
            if(objJSON.has("international_phone_number"))
                place.setPhoneNumber(objJSON.getString("international_phone_number"));
            else
                if(objJSON.has("formatted_phone_number"))
                    place.setPhoneNumber(objJSON.getString("formatted_phone_number"));
            if(objJSON.has("rating"))
                place.setRating(objJSON.getDouble("rating"));

            if(objJSON.has("photos")){
               JSONArray photo = objJSON.getJSONArray("photos");
                ArrayList<String> picture = new ArrayList<String>();
               for(int i = 0; i < photo.length(); i++){
                   JSONObject img = photo.getJSONObject(i);
                   picture.add(img.getString("photo_reference"));
               }
                place.setPhoto(picture);
            }

            if(objJSON.has("reviews")){
                JSONArray reviews = objJSON.getJSONArray("reviews");

                ArrayList<Review> arrReviews = new ArrayList<Review>();
                for(int i = 0; i < reviews.length(); i++){
                    Review review = new Review();
                    JSONObject r = reviews.getJSONObject(i);
                    review.setRating(r.getDouble("rating"));
                    review.setName(r.getString("author_name"));
                    review.setContent(r.getString("text"));
                    review.setTimeAgo(r.getString("relative_time_description"));
                    arrReviews.add(review);
                }
                place.setReview(arrReviews);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return place;
    }

//parse directions
    public Directions parseDirections(String json){
        Directions directions = new Directions();
        try {
            JSONObject rootJSON = new JSONObject(json);
            String status = rootJSON.getString("status");
            if(status.equals("OK")){
                //set status
                directions.setStatus(status);


                JSONArray routes = rootJSON.getJSONArray("routes");

                for(int i = 0; i < routes.length(); i++){
                    JSONObject route = routes.getJSONObject(i);
                    //ad copyrights
                    directions.setCopyrights(route.getString("copyrights"));

                    //legs
                    JSONArray legs = route.getJSONArray("legs");
                    for(int j = 0; j < legs.length(); j++){
                        JSONObject leg = legs.getJSONObject(j);
                        //add distance
                        JSONObject distance = leg.getJSONObject("distance");
                        directions.setDistance(distance.getString("text"));

                        //add duration
                        JSONObject duration = leg.getJSONObject("duration");
                        directions.setDuration(duration.getString("text"));

                        //add end address
                        directions.setEndAddress(leg.getString("end_address"));
                        //add end location
                        JSONObject endLocation = leg.getJSONObject("end_location");
                        directions.setEndLocation(new LatLng(endLocation.getDouble("lat"),endLocation.getDouble("lng")));

                        //begin get steps
                        JSONArray steps = leg.getJSONArray("steps");

                        ArrayList<StepsDirections> stepsDirectionses = new ArrayList<StepsDirections>();
                        for(int k = 0; k < steps.length(); k++){
                            StepsDirections stepDirections = new StepsDirections();
                            JSONObject step = steps.getJSONObject(k);

                            //set distance of step
                            JSONObject stepDistance = step.getJSONObject("distance");
                            stepDirections.setDistance(stepDistance.getString("text"));

                            //set duration of step
                            JSONObject stepDuration = step.getJSONObject("duration");
                            stepDirections.setDuration(stepDuration.getString("text"));

                            //set polyline point
                            JSONObject poly = step.getJSONObject("polyline");
                            String polyEncoded = poly.getString("points");

                            stepDirections.setPolyPoint(decodePoly(polyEncoded));
                            //set instructions
                            stepDirections.setInstructions(step.getString("html_instructions"));

                            stepsDirectionses.add(stepDirections);
                        } // end for k

                        //set steps
                        directions.setSteps(stepsDirectionses);
                    }//end for j

                }//end for i
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  directions;
    }

    // decode polyline point
    private ArrayList<LatLng> decodePoly(String encoded) {

        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
