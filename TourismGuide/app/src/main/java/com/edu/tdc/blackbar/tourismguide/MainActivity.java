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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity
        implements GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener {

    private FloatingActionButton fabPlus, fabNearBy, fabSchedule, fabLocateMe; //main button
    private boolean flagMenuOpen = false; // for drawer navigation
    private boolean flagSearchFocus = false; // for edit text search

    private Animation fabMoveUpRotate, plusOpenRotate, plusCloseRotate,
            fabMoveDown, textMoveUp, textMoveOut, edtAnimOpen, edtAniClose;

    private TextView txtLocate, txtNearBy, txtSchedule; //text decription for main floating button
    private DrawerLayout drawerNearBy; // drawer navigation
    private EditText edtsearch;  // edit text search
    private LinearLayout lnHeader; // linear layout header of drawer navigation

    private GoogleMap mMap; // main map
    private LatLng locateMe; // location of user
    private  LatLng locatePin;



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



    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


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
                        .zoom(17)                   // Sets the zoom
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawerNearBy.closeDrawer(GravityCompat.START);
                    }
                }, DELAY_ON_CLICK_ITEM);

                switch (i){
                    case 0 :{
                        typeSearch = getResources().getString(R.string.cafe);
                        break;
                    }
                    case 1 :{
                        typeSearch = getResources().getString(R.string.food);
                        break;
                    }
                    case 2 :{
                        typeSearch = getResources().getString(R.string.bar);
                        break;
                    }
                    case 3 :{
                        typeSearch = getResources().getString(R.string.gym);
                        break;
                    }
                    case 4 :{
                        typeSearch = getResources().getString(R.string.park);
                        break;
                    }
                    case 5 :{
                        typeSearch = getResources().getString(R.string.car_repair);
                        break;
                    }
                    case 6 :{
                        typeSearch = getResources().getString(R.string.post_office);
                        break;
                    }
                    case 7 :{
                        typeSearch = getResources().getString(R.string.airport);
                        break;
                    }
                    case 8 :{
                        typeSearch = getResources().getString(R.string.atm);
                        break;
                    }
                    case 9 :{
                        typeSearch = getResources().getString(R.string.bank);
                        break;
                    }
                    case 10 :{
                        typeSearch = getResources().getString(R.string.police);
                        break;
                    }
                    case 11 :{
                        typeSearch = getResources().getString(R.string.hospital);
                        break;
                    }
                    default:{
                        typeSearch = DEFAULT_TYPES_SEARCH;
                    }
                }
               // Log.d("testParse",typeSearch);
                buidURLSearch();
               // Log.d("testParse",urlNearBy);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(locateMe)      // Sets the center of the map to Mountain View
                        .zoom(16)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(15)                   // Sets the tilt of the camera to 90 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                PlacesTask placesTask = new PlacesTask();
                placesTask.execute(urlNearBy);

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
        //
        locateMe = new LatLng(10.851077, 106.758353); // test

        latSearch = String.valueOf(locateMe.latitude);
        lngSearch = String.valueOf(locateMe.longitude);
        radiusSearch = "1000";
        typeSearch = "cafe|food";

        buidURLSearch();
        //Log.d("testParse",urlNearBy);

        PlacesTask placesTask = new PlacesTask();
        placesTask.execute(urlNearBy);




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
        mMap = googleMap;
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
                .tilt(15)                   // Sets the tilt of the camera to 90 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //do something here
            }
        });
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        //Log.d("Camera postion change" + "", cameraPosition + "");
        locatePin = cameraPosition.target;
        try {

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

    /** A class, to download Google Places */
    private class PlacesTask extends AsyncTask<String, Integer, String> {

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

            ParseTask parseTask = new ParseTask();
            parseTask.execute(result);
        }

    }

    // update places
    private void upDatePlacesNB(ArrayList<NearByPlaces> nearByPlaces){
        mMap.clear();
        for(NearByPlaces place : nearByPlaces) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(place.getLatitude(), place.getLongitude()));
            markerOptions.title(place.getName());
            if (place.getType().contains("restaurant")) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_food));
            } else {
                if (place.getType().contains("food") && place.getType().contains("cafe")) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_cafe));
                } else {
                    if (place.getType().contains("food") && !place.getType().contains("cafe")) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_food));
                    } else {
                        if (place.getType().contains("gym")) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_gym));
                        } else {
                            if (place.getType().contains("hospital")) {
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_hospital));
                            } else {
                                if (place.getType().contains("park")) {
                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_park));
                                } else {
                                    if (place.getType().contains("zoo")) {
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_park));
                                    } else {
                                        if (place.getType().contains("bar") && place.getType().contains("night_club")) {
                                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_bar));
                                        } else {
                                            if (place.getType().contains("police")) {
                                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_police));
                                            } else {
                                                if (place.getType().contains("airport")) {
                                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_airport));
                                                }else{
                                                    if (place.getType().contains("car_repair")) {
                                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_car_rp));
                                                    }else{
                                                        if (place.getType().contains("post_office")) {
                                                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_office));
                                                        }else{
                                                            if (place.getType().contains("bank")) {
                                                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_bank));
                                                            }else
                                                            {
                                                                if (place.getType().contains("atm")) {
                                                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_atm));
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

                }
            }

    /** A class, to parse Json  */
    private class ParseTask extends AsyncTask<String, Integer, ArrayList<NearByPlaces> >{

        ArrayList<NearByPlaces> data = null;

        // Invoked by execute() method of this object
        @Override
        protected ArrayList<NearByPlaces> doInBackground(String... stringJson) {
            try{
                JSONParsePlaces parsePlaces = new JSONParsePlaces();
                data = parsePlaces.JSONParseNearBy(stringJson[0]);
                //Log.v("test", data);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<NearByPlaces> nearByPlaces) {
            //when
            upDatePlacesNB(nearByPlaces);
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

}