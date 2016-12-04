package com.edu.tdc.blackbar.tourismguide;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.tdc.blackbar.tourismguide.JsonParse.JSONParsePlaces;
import com.edu.tdc.blackbar.tourismguide.datamodel.NearByPlaces;
import com.edu.tdc.blackbar.tourismguide.myAdapter.MyAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity
        implements GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener ,GoogleMap.OnMapLoadedCallback {

    private FloatingActionButton fabPlus, fabNearBy, fabSchedule, fabLocateMe; //main button
    private boolean flagMenuOpen = false; // for drawer navigation
    private boolean flagSearchFocus = false; // for edit text search

    private Animation fabMoveUpRotate, plusOpenRotate, plusCloseRotate,
            fabMoveDown, textMoveUp, textMoveOut, edtAnimOpen, edtAniClose;

    private TextView txtLocate, txtNearBy, txtSchedule; //text decription for main floating button
    private DrawerLayout drawerNearBy; // drawer navigation
    private EditText edtsearch;  // edit text search
    private LinearLayout lnHeader; // linear layout header of drawer navigation
    private AVLoadingIndicatorView aviLoading ; //  progressbar when loading

    private GoogleMap mMap; // main map
    private LatLng locateMe; // location of user
    private  LatLng locatePin; //location of pin picker
    private float carmeraZoom; // catch index zoom of camera with each object
    private int timesUpdatePlace = 0; //count times call updatePlace()
    private int sizeJSONArr = 0; //length JSONArray results
    private boolean flagMapLoaded = false;

    private final String KEY_API_PLACES = "AIzaSyC4U9eZCroaixKpvMgHbUMyNO-Ekni_AuU";
    private final int DELAY_ON_CLICK_ITEM = 400; // delay when drawer is closed
    private final int DEFAULT_CAMERA_MAP = 16; //  default camera of map
    private final String BASIC_PARSE_URL_NEARBY ="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    private final String TOKEN_AND = "&";
    private final String TOKEN_COMMA = ",";
    private final String TXT_TYPE = "types=";
    private final String TXT_KEY = "key=";
    private final String TXT_RADIUS ="radius=";
    private final String SENSOR = "sensor=false";
    private final String DEFAULT_TYPES_SEARCH = "cafe|food";

    private String typeSearch;
    private String latSearch;
    private String lngSearch;
    private String radiusSearch;
    private String urlNearBy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        fabPlus = (FloatingActionButton) findViewById(R.id.fab_plus);
        fabSchedule = (FloatingActionButton) findViewById(R.id.fab_setSchedule);
        fabNearBy = (FloatingActionButton) findViewById(R.id.fab_nearBy);
        fabLocateMe = (FloatingActionButton) findViewById(R.id.fab_locate_me);

        txtLocate = (TextView) findViewById(R.id.txt_decript_lme);
        txtNearBy = (TextView) findViewById(R.id.txt_decript_nearb);
        txtSchedule = (TextView) findViewById(R.id.txt_decript_schedule);
        lnHeader = (LinearLayout) findViewById(R.id.lnHeader_drawer);
        aviLoading = (AVLoadingIndicatorView) findViewById(R.id.aviLoading);
        aviLoading.setVisibility(View.VISIBLE);
        //load animation from anim
        fabMoveUpRotate = AnimationUtils.loadAnimation(this, R.anim.fab_move_up_rotate);
        plusOpenRotate = AnimationUtils.loadAnimation(this, R.anim.fab_plus_open_rotate);
        plusCloseRotate = AnimationUtils.loadAnimation(this, R.anim.fab_plus_close_rotate);
        fabMoveDown = AnimationUtils.loadAnimation(this, R.anim.fab_move_down);
        textMoveUp = AnimationUtils.loadAnimation(this, R.anim.text_move_up);
        textMoveOut = AnimationUtils.loadAnimation(this, R.anim.text_move_out);
        edtAnimOpen = AnimationUtils.loadAnimation(this, R.anim.edt_search_anim_open_width);
        edtAniClose = AnimationUtils.loadAnimation(this, R.anim.edt_search_anim_close_width);


        //load drawer near by
        drawerNearBy = (DrawerLayout) findViewById(R.id.drawer_layout);

        //edit text search
        edtsearch = (EditText) findViewById(R.id.edtSearch);

        //update list view body drawer
        ListView listTypesSearch = (ListView) findViewById(R.id.lv_mn_drawer);
        final String[] typesSearch = this.getResources().getStringArray(R.array.types_support);
        MyAdapter adapter = new MyAdapter(MainActivity.this,
                R.layout.list_types_search_item_layout
                ,typesSearch);
        listTypesSearch.setAdapter(adapter);

        //load map
        mapFragment.getMapAsync(this);


        edtsearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!flagSearchFocus) {
                    edtsearch.startAnimation(edtAnimOpen);
                    flagSearchFocus = true;
                } else {
                    edtsearch.startAnimation(edtAniClose);
                    flagSearchFocus = false;
                }
            }
        });
        edtsearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE ||
                        i == EditorInfo.IME_ACTION_SEARCH ||
                        keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        ) {
                    //do something here (load map)

                    //xxxxxxxxxxxxxxxxxxxxx

                    edtsearch.clearFocus();
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.TYPE_SEARCH_BAR);
                        im.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        edtsearch.startAnimation(edtAniClose);
                        flagSearchFocus = false;

                        //Toast.makeText(getApplicationContext(),edtsearch.getText(),Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;

            }


        });

        fabPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!flagMenuOpen) {
                    showChildMenu();
                    flagMenuOpen = true;
                } else {
                    hideChildMenu();
                    flagMenuOpen = false;
                }
            }
        });

        //btn locate me  on click
        fabLocateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do something here
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(locateMe)      // Sets the center of the map to Mountain View
                        .zoom(carmeraZoom)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(50)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                hideChildMenu();
                flagMenuOpen = false;
            }
        });

        //btn nearby on click
        fabNearBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do something here
                drawerNearBy.openDrawer(GravityCompat.START);
                hideChildMenu();
                flagMenuOpen = false;
            }
        });

        //btn setschedule  on click
        fabSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //so something here

                hideChildMenu();
                flagMenuOpen = false;
            }
        });

        listTypesSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CameraPosition.Builder carmeraBuider = new CameraPosition.Builder();
                carmeraBuider.target(locateMe);
                carmeraBuider.bearing(90);                // Sets the orientation of the camera to east
                carmeraBuider.tilt(15);                    // Sets the tilt of  degrees

                switch (i){
                    case 0 :{
                        typeSearch = getResources().getString(R.string.cafe);
                        radiusSearch = "1000";
                        carmeraBuider.zoom(16);
                        carmeraZoom = 16;
                        break;
                    }
                    case 1 :{
                        typeSearch = getResources().getString(R.string.food);
                        radiusSearch = "1000";
                        carmeraBuider.zoom(16);
                        carmeraZoom = 16;
                        break;
                    }
                    case 2 :{
                        typeSearch = getResources().getString(R.string.bar);
                        radiusSearch = "2000";
                        carmeraBuider.zoom(15);
                        carmeraZoom = 15;
                        break;
                    }
                    case 3 :{
                        typeSearch = getResources().getString(R.string.gym);
                        radiusSearch = "1000";
                        carmeraBuider.zoom(15);
                        carmeraZoom = 15;
                        break;
                    }
                    case 4 :{
                        typeSearch = getResources().getString(R.string.park);
                        radiusSearch = "5000";
                        carmeraBuider.zoom(13);
                        carmeraZoom = 13;
                        break;
                    }
                    case 5 :{
                        typeSearch = getResources().getString(R.string.car_repair);
                        radiusSearch = "4000";
                        carmeraBuider.zoom(14);
                        carmeraZoom = 14;
                        break;
                    }
                    case 6 :{
                        typeSearch = getResources().getString(R.string.post_office);
                        radiusSearch = "2000";
                        carmeraBuider.zoom(15);
                        carmeraZoom = 15;
                        break;
                    }
                    case 7 :{
                        typeSearch = getResources().getString(R.string.airport);
                        radiusSearch = "30000";
                        carmeraBuider.zoom(11);
                        carmeraZoom = 11;
                        break;
                    }
                    case 8 :{
                        typeSearch = getResources().getString(R.string.atm);
                        radiusSearch = "1000";
                        carmeraBuider.zoom(16);
                        carmeraZoom = 16;
                        break;
                    }
                    case 9 :{
                        typeSearch = getResources().getString(R.string.bank);
                        radiusSearch = "1000";
                        carmeraBuider.zoom(16);
                        carmeraZoom = 16;
                        break;
                    }
                    case 10 :{
                        typeSearch = getResources().getString(R.string.police);
                        radiusSearch = "2000";
                        carmeraBuider.zoom(14);
                        carmeraZoom = 14;
                        break;
                    }
                    case 11 :{
                        typeSearch = getResources().getString(R.string.hospital);
                        radiusSearch = "5000";
                        carmeraBuider.zoom(13);
                        carmeraZoom = 13;
                        break;
                    }
                    default:{
                        typeSearch = DEFAULT_TYPES_SEARCH;
                    }
                }
                // Log.d("testParse",typeSearch);
                buidURLSearch();
                // Log.d("testParse",urlNearBy);

                CameraPosition cameraPosition = carmeraBuider.build();  //buid position for camera map
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                PlacesTask placesTask = new PlacesTask();
                mMap.clear();
                placesTask.execute(urlNearBy);

                //delay drawer close
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawerNearBy.closeDrawer(GravityCompat.START);
                    }
                }, DELAY_ON_CLICK_ITEM);


            }
        });

        //listen drawer
        drawerNearBy.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    } // xxxxxxxxxxxxxx------end-onCreate-----xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    private void showChildMenu() {
        fabSchedule.show();
        fabNearBy.show();
        fabLocateMe.show();

        fabPlus.startAnimation(plusOpenRotate);
        fabLocateMe.startAnimation(fabMoveUpRotate);
        fabNearBy.startAnimation(fabMoveUpRotate);
        fabSchedule.startAnimation(fabMoveUpRotate);

        fabSchedule.setClickable(true);
        fabNearBy.setClickable(true);
        fabLocateMe.setClickable(true);

        txtSchedule.startAnimation(textMoveUp);
        txtNearBy.startAnimation(textMoveUp);
        txtLocate.startAnimation(textMoveUp);

        txtSchedule.setVisibility(View.VISIBLE);
        txtNearBy.setVisibility(View.VISIBLE);
        txtLocate.setVisibility(View.VISIBLE);


    }

    private void hideChildMenu() {

        txtSchedule.startAnimation(textMoveOut);
        txtNearBy.startAnimation(textMoveOut);
        txtLocate.startAnimation(textMoveOut);

        txtSchedule.setVisibility(View.INVISIBLE);
        txtNearBy.setVisibility(View.INVISIBLE);
        txtLocate.setVisibility(View.INVISIBLE);

        fabPlus.startAnimation(plusCloseRotate);
        fabSchedule.hide();
        fabNearBy.hide();
        fabLocateMe.hide();

        fabSchedule.setClickable(false);
        fabNearBy.setClickable(false);
        fabLocateMe.setClickable(false);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //load map
    @Override
    public void onMapReady(GoogleMap googleMap) {

        aviLoading.setVisibility(View.VISIBLE);
        flagMapLoaded = false;
        mMap = googleMap;
        //fist load place
        locateMe = new LatLng(10.851077, 106.758353); // test

        latSearch = String.valueOf(locateMe.latitude);
        lngSearch = String.valueOf(locateMe.longitude);
        radiusSearch = "1000";
        typeSearch = "cafe|food";

        buidURLSearch();
        //Log.d("testParse",urlNearBy);
        PlacesTask placesTask = new PlacesTask();
        placesTask.execute(urlNearBy);
        mMap.setOnCameraChangeListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20), 2000, null);
     //   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locateMe,DEFAULT_CAMERA_MAP));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(locateMe)      // Sets the center of the map to Mountain View
                .zoom(16)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(15)                   // Sets the tilt of the camera to 15 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        carmeraZoom = 16;
        // map loaded
        mMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        aviLoading.setVisibility(View.VISIBLE);
        //Log.d("Camera postion change" + "", cameraPosition + "");
        locatePin = cameraPosition.target;
       // flagMapLoaded = false;
        try {
             mMap.setOnMapLoadedCallback(this);
            Location mLocation = new Location("");
            mLocation.setLatitude(locatePin.latitude);
            mLocation.setLongitude(locatePin.longitude);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{

        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);


            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Log.d("clicMarker","onMarkerClick");
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(marker.getPosition())
                .zoom(19)
                .bearing(20)
                .tilt(80)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onMapLoaded() {

        if(timesUpdatePlace == sizeJSONArr){
            aviLoading.setVisibility(View.INVISIBLE);
            sizeJSONArr = 0;
            timesUpdatePlace =0;
        }
        flagMapLoaded = true;
    }

    /** A class, to download Google Places */
    private class PlacesTask extends AsyncTask<String, Integer, String> {
        public PlacesTask(){
            aviLoading.setVisibility(View.VISIBLE); // progress bar is visible when dowloadUrl() is called
        }
        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
                //Log.v("test", data);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result){

            JSONObject rootObject = null;
            try {
                rootObject = new JSONObject(result);
                JSONArray resultArr = rootObject.getJSONArray("results");
                sizeJSONArr = resultArr.length();
                for(int i = 0; i < resultArr.length(); i++){
                    JSONObject JSONplace = resultArr.getJSONObject(i);
                    ParseTask parseTask = new ParseTask();
                    parseTask.execute(JSONplace);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    // update places
    private void updatePlacesNB(NearByPlaces nearByPlaces){

           timesUpdatePlace++; // if timesUpdatePlace == length of JSON Array result -> the last update place

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(nearByPlaces.getLatitude(), nearByPlaces.getLongitude()));
            markerOptions.title(nearByPlaces.getName());
            markerOptions.snippet(nearByPlaces.getAddress());
            if (nearByPlaces.getTypes().contains("restaurant")) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_food));
            } else {
                if (nearByPlaces.getTypes().contains("food") && nearByPlaces.getTypes().contains("cafe")) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_cafe));
                } else {
                    if (nearByPlaces.getTypes().contains("food") && !nearByPlaces.getTypes().contains("cafe")) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_food));
                    } else {
                        if (nearByPlaces.getTypes().contains("gym")) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_gym));
                        } else {
                            if (nearByPlaces.getTypes().contains("hospital")) {
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_hospital));
                            } else {
                                if (nearByPlaces.getTypes().contains("park")) {
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_park));
                                } else {
                                    if (nearByPlaces.getTypes().contains("zoo")) {
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_park));
                                    } else {
                                        if (nearByPlaces.getTypes().contains("bar") && nearByPlaces.getTypes().contains("night_club")) {
                                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_bar));
                                        } else {
                                            if (nearByPlaces.getTypes().contains("police")) {
                                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_police));
                                            } else {
                                                if (nearByPlaces.getTypes().contains("airport")) {
                                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_airport));
                                                }else{
                                                    if (nearByPlaces.getTypes().contains("car_repair")) {
                                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_car_rp));
                                                    }else{
                                                        if (nearByPlaces.getTypes().contains("post_office")) {
                                                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_office));
                                                        }else{
                                                            if (nearByPlaces.getTypes().contains("bank")) {
                                                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_bank));
                                                            }else
                                                            {
                                                                if (nearByPlaces.getTypes().contains("atm")) {
                                                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_atm));
                                                                }else{
                                                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_unknow));
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            mMap.addMarker(markerOptions);
            if(timesUpdatePlace == sizeJSONArr && flagMapLoaded){
                aviLoading.setVisibility(View.INVISIBLE);
                sizeJSONArr = 0;
                timesUpdatePlace =0;
            }
    }

    /** A class, to parse Json  */
    private class ParseTask extends AsyncTask<JSONObject, Integer, NearByPlaces >{

        NearByPlaces data = null;

        // Invoked by execute() method of this object
        @Override
        protected NearByPlaces doInBackground(JSONObject... ObjJSON) {
            try{
                JSONParsePlaces parsePlaces = new JSONParsePlaces();
                data = parsePlaces.JSONParseNearBy(ObjJSON[0]);
                //Log.v("test", data);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(NearByPlaces nearByPlaces) {
            updatePlacesNB(nearByPlaces);
        }
    }
    //buid url ... call it when have change (lat,lng,types,radius,..)
    private void buidURLSearch(){
        urlNearBy = BASIC_PARSE_URL_NEARBY+latSearch+TOKEN_COMMA+lngSearch+TOKEN_AND
                +TXT_RADIUS+radiusSearch+TOKEN_AND+
                TXT_TYPE+typeSearch+TOKEN_AND+
                SENSOR+TOKEN_AND+
                TXT_KEY+KEY_API_PLACES;
    }

    private int calculateDistance(LatLng newPoint, LatLng oldPoint){
        int distance = 0;
        
        return distance;
    }

}