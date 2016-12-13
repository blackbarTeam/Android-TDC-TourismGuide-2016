package com.edu.tdc.blackbar.tourismguide;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.tdc.blackbar.tourismguide.JsonParse.JSONParsePlaces;
import com.edu.tdc.blackbar.tourismguide.datamodel.Directions;
import com.edu.tdc.blackbar.tourismguide.datamodel.NearByPlaces;
import com.edu.tdc.blackbar.tourismguide.datamodel.PlaceDetails;
import com.edu.tdc.blackbar.tourismguide.datamodel.StepsDirections;
import com.edu.tdc.blackbar.tourismguide.myAdapter.MyAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity
        implements GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener ,GoogleMap.OnMapLoadedCallback , GoogleMap.OnMapClickListener{

    private FloatingActionButton fabPlus, fabNearBy, fabSchedule, fabLocateMe; //main button
    private Button btnDirect, btnDetail;
    private boolean flagMenuOpen = false; // for drawer navigation
    private boolean flagSearchFocus = false; // for edit text search
    private boolean flagOpenBtnRight = false; //f
    private boolean flagDirectionMode = false; // to know application when at direction mode or no
    private boolean flagSearchmode = true;
    private boolean flagTextSearch = false;

    private Animation fabMoveUpRotate, plusOpenRotate, plusCloseRotate,
            fabMoveDown, textMoveUp, textMoveOut, edtAnimOpen, edtAniClose, btnRightAction, btnRightActionHide;

    private TextView txtLocate, txtNearBy, txtSchedule; //text decription for main floating button
    private DrawerLayout drawerNearBy; // drawer navigation
    private EditText edtsearch;  // edit text search
    private String textSearch = null; // text search of user
    private LinearLayout lnHeader; // linear layout header of drawer navigation
    private AVLoadingIndicatorView aviLoading ; //  progressbar when loading
    private ImageView imvPin;

    private GoogleMap mMap; // main map
    private LatLng locateMe; // location of user
    private  LatLng locatePin; //location of pin picker
    private float carmeraZoom; // catch index zoom of camera with each object
    private int timesUpdatePlace = 0; //count times call updatePlace()
    private int sizeJSONArr = 0; //length JSONArray results
    private boolean flagMapLoaded = false;
    private LatLng oldLocate; //cacht last locatepin before
    private LatLng endLocation; //end location directions
    private LatLng realLocateMe;

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

    private final String BASIC_URL_DIRECTIONS ="https://maps.googleapis.com/maps/api/directions/json?origin=";
    private final String TXT_DESTINATION = "&destination=place_id:";
    private  String language ="en";
    private final String KEY_DIRECTIONS = "AIzaSyBZVwSKR1huK5BFfJ-DqRen-CPZ07MCzqE";

    private final String BASIC_URL_TEXT_SEARCH = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=";

    private String typeSearch;
    private String latSearch;
    private String lngSearch;
    private String radiusSearch;
    private String urlNearBy;
    private ArrayList<NearByPlaces> listPlaces ;
    private String placeIDMarkerClicked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        fabPlus = (FloatingActionButton) findViewById(R.id.fab_plus);
        fabSchedule = (FloatingActionButton) findViewById(R.id.fab_setSchedule);
        fabNearBy = (FloatingActionButton) findViewById(R.id.fab_nearBy);
        fabLocateMe = (FloatingActionButton) findViewById(R.id.fab_locate_me);

        btnDirect = (Button)findViewById(R.id.btn_direct);
        btnDetail = (Button) findViewById(R.id.btn_detail);

        imvPin = (ImageView) findViewById(R.id.imageMarker);

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
        btnRightAction = AnimationUtils.loadAnimation(this,R.anim.btn_right_action);
        btnRightActionHide = AnimationUtils.loadAnimation(this,R.anim.btn_right_action_close);

        listPlaces = new ArrayList<NearByPlaces>();
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

        //fist load place
        locateMe = new LatLng(-33.882256, 151.207192); // test
        oldLocate = locateMe;
        realLocateMe = locateMe;

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
                        flagTextSearch = true;
                        textSearch = edtsearch.getText().toString();
                        textSearch.trim();
                        if(!textSearch.matches("") || textSearch != null && textSearch.length() > 0) {
                            timesUpdatePlace = 0;

                            Log.d("test","inside");
                            String url = buidURLTextSearch(textSearch);
                            Log.d("test",url);
                          //  Log.d("test", url);
                            PlacesTask placeSearch = new PlacesTask();
                            placeSearch.execute(url);
                        }
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
                } else {
                    hideChildMenu();
                }
                hideRinghtActionButton();
            }
        });

        //btn locate me  on click
        fabLocateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do something here
                locateMe = realLocateMe;
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(locateMe)      // Sets the center of the map to Mountain View
                        .zoom(carmeraZoom)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(50)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.addMarker(new MarkerOptions().position(locateMe).title("you"));
                imvPin.setVisibility(View.INVISIBLE);
                hideChildMenu();
                hideRinghtActionButton();
                flagMenuOpen = false;
                if(!flagDirectionMode){
                    flagSearchmode = false;
                }
            }
        });


        //btn nearby on click
        fabNearBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do something here
                drawerNearBy.openDrawer(GravityCompat.START);
                hideChildMenu();
                hideRinghtActionButton();
                flagMenuOpen = false;
                flagSearchmode = true;
            }
        });

        //btn setschedule  on click
        fabSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do something here


                hideChildMenu();
                hideRinghtActionButton();
                flagMenuOpen = false;
            }
        });
        //btn direct on click
        btnDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideRinghtActionButton();
                //do some thing here
                flagDirectionMode = true;
                flagSearchmode = false;
                imvPin.setVisibility(View.INVISIBLE);
                String url = buidURLDirections();
               // Log.d("test",url);
                DirectionsTask directionsTask = new DirectionsTask();
                directionsTask.execute(url);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(locateMe)      // Sets the center of the map to Mountain View
                        .zoom(carmeraZoom)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(50)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
        });

        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideRinghtActionButton();
                //do some thing here
                Intent intent = new Intent(MainActivity.this, PlaceDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("placeID",placeIDMarkerClicked);
                intent.putExtra("dataID",bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        listTypesSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                flagDirectionMode = false;
                flagSearchmode = false;
                imvPin.setVisibility(View.INVISIBLE);
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

                CameraPosition cameraPosition = carmeraBuider.build();  //buid position for camera map
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                buidURLSearch();
                PlacesTask placesTask = new PlacesTask();
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(locateMe).title("you"));
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
        if(!flagMenuOpen) {
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
            flagMenuOpen = true;
        }

    }

    //show right action button
    private  void showRinghtActionButton(){

        btnDetail.startAnimation(btnRightAction);
        btnDirect.startAnimation(btnRightAction);
        btnDetail.setClickable(true);
        btnDirect.setClickable(true);
        btnDetail.setVisibility(View.VISIBLE);
        btnDirect.setVisibility(View.VISIBLE);
        flagOpenBtnRight = true;
    }
    // hide action button
    private  void hideRinghtActionButton(){
        if(flagOpenBtnRight) {
            btnDetail.startAnimation(btnRightActionHide);
            btnDirect.startAnimation(btnRightActionHide);
            btnDetail.setClickable(false);
            btnDirect.setClickable(false);
            btnDetail.setVisibility(View.INVISIBLE);
            btnDirect.setVisibility(View.INVISIBLE);
            flagOpenBtnRight = false;
        }
    }

    private void hideChildMenu() {

        if(flagMenuOpen) {
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
            flagMenuOpen = false;
        }
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



        latSearch = String.valueOf(locateMe.latitude);
        lngSearch = String.valueOf(locateMe.longitude);
        radiusSearch = "1000";
        typeSearch = "cafe|food";
        buidURLSearch();
        PlacesTask placesTask = new PlacesTask();
        placesTask.execute(urlNearBy);

        imvPin.setVisibility(View.GONE);
        mMap.setOnCameraChangeListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20), 2000, null);


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

        MarkerOptions me = new MarkerOptions();
        me.position(locateMe);
        me.title("you");
        mMap.addMarker(me);

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        flagMapLoaded = false;
        //Log.d("Camera postion change" + "", cameraPosition + "");
        locatePin = cameraPosition.target;

        double distance = calculateDistance(locatePin,oldLocate);
        distance = Math.round(distance);
        int rdSearch = Integer.parseInt(radiusSearch);
       // flagMapLoaded = false;
        try {
             mMap.setOnMapLoadedCallback(this);
            if(distance > rdSearch && !flagDirectionMode){
                if(flagSearchmode) {
                    imvPin.setVisibility(View.VISIBLE);
                    mMap.clear();
                }
                latSearch = String.valueOf(locatePin.latitude);
                lngSearch = String.valueOf(locatePin.longitude);
                buidURLSearch();
                timesUpdatePlace = 0;
                PlacesTask placesTask = new  PlacesTask();
                placesTask.execute(urlNearBy);
                oldLocate = locatePin;
            }

            if(timesUpdatePlace == sizeJSONArr && flagMapLoaded){
                aviLoading.setVisibility(View.INVISIBLE);
                sizeJSONArr = 0;
                timesUpdatePlace =0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        flagSearchmode = true;
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
        //show action right button

        hideChildMenu();

        if(!listPlaces.isEmpty() && marker.getSnippet() != null){
            showRinghtActionButton();
            String makerAddress = marker.getSnippet();
            for(int i = 0; i < listPlaces.size(); i++){
                NearByPlaces place = listPlaces.get(i);
                String placeAddress = place.getAddress();
                if(placeAddress.equals(makerAddress)){
                    placeIDMarkerClicked = place.getPlaceID();
                    endLocation = new LatLng(place.getLatitude(),place.getLongitude());
                    //Log.d("test",placeIDMarkerClicked);
                }
            }
        }

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

    @Override
    public void onMapClick(LatLng latLng) {
        //hide right action button
        //hide right action button
        hideRinghtActionButton();
        hideChildMenu();
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
            listPlaces.clear();
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
            switch (typeSearch){
                case "cafe":{
                    if(nearByPlaces.getTypes().contains("cafe")) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_cafe));
                        mMap.addMarker(markerOptions);
                    }
                    break;
                }
                case "food" :{
                    if(nearByPlaces.getTypes().contains("food") | nearByPlaces.getTypes().contains("restaurant") ) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_food));
                        mMap.addMarker(markerOptions);
                    }
                    break;
                }
                case "gym":{
                    if(nearByPlaces.getTypes().contains("gym")) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_gym));
                        mMap.addMarker(markerOptions);
                    }
                    break;
                }
                case "park":{
                    if(nearByPlaces.getTypes().contains("park")) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_park));
                        mMap.addMarker(markerOptions);
                    }
                    break;
                }
                case "bar":{
                    if(nearByPlaces.getTypes().contains("bar")) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_bar));
                        mMap.addMarker(markerOptions);
                    }
                    break;
                }
                case "post_office":{
                    if(nearByPlaces.getTypes().contains("post_office")) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_office));
                        mMap.addMarker(markerOptions);
                    }
                    break;
                }
                case "airport":{
                    if(nearByPlaces.getTypes().contains("airport") &&
                            !nearByPlaces.getTypes().contains("food") &&
                            !nearByPlaces.getTypes().contains("moving_company") &&
                            !nearByPlaces.getTypes().contains("travel_agency") &&
                            !nearByPlaces.getTypes().contains("store") &&
                            !nearByPlaces.getTypes().contains("car_rental")

                            ) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_airport));
                        mMap.addMarker(markerOptions);
                    }
                    break;
                }
                case "car_repair":{
                    if(nearByPlaces.getTypes().contains("car_repair")) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_car_rp));
                        mMap.addMarker(markerOptions);
                    }
                    break;
                }
                case "atm":{
                    if(nearByPlaces.getTypes().contains("atm") &&
                            !nearByPlaces.getTypes().contains("food")) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_atm));
                        mMap.addMarker(markerOptions);
                    }
                    break;
                }
                case "bank":{
                    if(nearByPlaces.getTypes().contains("bank") &&
                            !nearByPlaces.getTypes().contains("food")) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_bank));
                        mMap.addMarker(markerOptions);
                    }
                    break;
                }
                case "police":{
                    if(nearByPlaces.getTypes().contains("police") &&
                            !nearByPlaces.getTypes().contains("food")) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_police));
                        mMap.addMarker(markerOptions);
                    }
                    break;
                }
                case "hospital":{
                    if(nearByPlaces.getTypes().contains("hospital") &&
                            !nearByPlaces.getTypes().contains("food") &&
                            !nearByPlaces.getTypes().contains("beauty_salon") &&
                            !nearByPlaces.getTypes().contains("store")){
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_hospital));
                        mMap.addMarker(markerOptions);
                    }
                    break;
                }

                //default search
                case "cafe|food":{
                    if(nearByPlaces.getTypes().contains("cafe") && nearByPlaces.getTypes().contains("food")){
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_cafe));
                        mMap.addMarker(markerOptions);
                    }else{
                        if(!nearByPlaces.getTypes().contains("cafe") && nearByPlaces.getTypes().contains("food") || nearByPlaces.getTypes().contains("restaurant"))
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_food));
                        mMap.addMarker(markerOptions);
                    }
                    break;
                }
                default:{
                    mMap.addMarker(markerOptions);
                }

            }

            if(timesUpdatePlace == sizeJSONArr && flagMapLoaded){
                aviLoading.setVisibility(View.INVISIBLE);
                sizeJSONArr = 0;
                timesUpdatePlace =0;
            }

            if(flagTextSearch && timesUpdatePlace == 0){
                flagTextSearch = false;
                locateMe = new LatLng(nearByPlaces.getLatitude(),nearByPlaces.getLongitude());
                //Log.d("test",locateMe.latitude+"");
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(locateMe)      // Sets the center of the map to Mountain View
                        .zoom(15)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(15)                   // Sets the tilt of the camera to 15 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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
            listPlaces.add(nearByPlaces);
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

    private double calculateDistance(LatLng newPoint, LatLng oldPoint){

        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(newPoint.latitude - oldPoint.latitude);
        double lngDiff = Math.toRadians(newPoint.longitude-oldPoint.longitude);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(oldPoint.latitude)) * Math.cos(Math.toRadians(newPoint.latitude)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }

    //xxx--xx--Directions--xx--xxx

    private String buidURLDirections(){

        String url = BASIC_URL_DIRECTIONS+locateMe.latitude+
                ","+locateMe.longitude+TXT_DESTINATION+placeIDMarkerClicked+TOKEN_AND
                +"language="+language+TOKEN_AND+TXT_KEY+KEY_DIRECTIONS;
        return url;
    }

    //draw directions
    private void drawDirections(Directions directions){
        PolylineOptions polylineOptions = new PolylineOptions();
       ArrayList<StepsDirections> steps = null;
        steps = directions.getSteps();
        for(StepsDirections step : steps){

            polylineOptions.addAll(step.getPolyPoint());
           // Log.d("test",step.getInstructions());
        }




        polylineOptions.width(11);
        polylineOptions.color(getResources().getColor(R.color.colorAccent));

        if(polylineOptions != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(locateMe).title("you"));
            mMap.addMarker(new MarkerOptions().position(directions.getEndLocation()).title(directions.getEndAddress()));
//            mMap.addMarker(new MarkerOptions().position());
           mMap.addPolyline(polylineOptions);

       }
    }

    private class DirectionsTask extends  AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String... strings) {
            String data ="";
            try {
                data =  downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String directions) {
            parseDirectionsTask directionsTask = new parseDirectionsTask();
            directionsTask.execute(directions);
        }
    }

    private class parseDirectionsTask extends AsyncTask<String, Integer, Directions>{
        @Override
        protected Directions doInBackground(String... strings) {
            Directions directions = null;

            JSONParsePlaces direct = new JSONParsePlaces();
            directions = direct.parseDirections(strings[0]);
            return directions;
        }

        @Override
        protected void onPostExecute(Directions directions) {
            drawDirections(directions);
        }
    }

    private String buidURLTextSearch(String textS){
        String url = BASIC_URL_TEXT_SEARCH + textS + TOKEN_AND + TXT_KEY + KEY_API_PLACES;
        return  url;
    }

}