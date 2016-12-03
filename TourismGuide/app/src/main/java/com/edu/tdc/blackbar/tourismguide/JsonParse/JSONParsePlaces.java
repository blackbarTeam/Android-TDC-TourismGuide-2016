package com.edu.tdc.blackbar.tourismguide.JsonParse;


import com.edu.tdc.blackbar.tourismguide.datamodel.NearByPlaces;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Shiro on 02/12/2016.
 */

public class JSONParsePlaces {

    public ArrayList<NearByPlaces> JSONParseNearBy(String stringJSON) {
      //  Log.d("testString",stringJSON);
        ArrayList<NearByPlaces> places = new ArrayList<NearByPlaces>();
        try {
            JSONObject rootObject = new JSONObject(stringJSON);
            JSONArray result = rootObject.getJSONArray("results");
            for(int i = 0; i < result.length(); i++){
                NearByPlaces nearByPlace = new NearByPlaces();

                JSONObject place = result.getJSONObject(i);

                JSONObject geometry = place.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");

                //  add lat long
                nearByPlace.setLatitude(location.getDouble("lat"));
                nearByPlace.setLongitude(location.getDouble("lng"));
                //add  name
                nearByPlace.setName(place.getString("name"));
                //  add place ID
                nearByPlace.setPlaceID(place.getString("place_id"));
                // add type
                JSONArray types = place.getJSONArray("types");
                ArrayList<String> stringTypes = new ArrayList<String>();
                for (int j = 0; j < types.length(); j++){
                    stringTypes.add(types.getString(j));
                }

                nearByPlace.setType(stringTypes);
                //  add array list place
                places.add(nearByPlace);


            }



        } catch (JSONException e) {
            e.printStackTrace();
        }


        return places;
    }
}
