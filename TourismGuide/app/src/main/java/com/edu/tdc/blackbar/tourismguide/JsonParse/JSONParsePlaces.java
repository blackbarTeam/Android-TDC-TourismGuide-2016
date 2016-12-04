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
                place.setAddress(objJSON.getString("vicinity"));
                //  add place ID
                place.setPlaceID(objJSON.getString("place_id"));
                // add type
                JSONArray types = objJSON.getJSONArray("types");
                ArrayList<String> stringTypes = new ArrayList<String>();
                for (int j = 0; j < types.length(); j++){
                    stringTypes.add(types.getString(j));
                }

                place.setType(stringTypes);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return place;
    }
}
