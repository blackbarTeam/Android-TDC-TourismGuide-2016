package com.edu.tdc.blackbar.tourismguide;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.tdc.blackbar.tourismguide.JsonParse.JSONParsePlaces;
import com.edu.tdc.blackbar.tourismguide.datamodel.PlaceDetails;
import com.edu.tdc.blackbar.tourismguide.datamodel.Review;
import com.edu.tdc.blackbar.tourismguide.myAdapter.ReviewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class PlaceDetailsActivity extends AppCompatActivity {

    private final String KEY_API_PLACES = "AIzaSyC4U9eZCroaixKpvMgHbUMyNO-Ekni_AuU";
    private final String BASIC_URL = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
    private final String TXT_KEY = "&key=";
    private final String LANGUAGE = "&language=en";

    private final String BASiC_URL_PHOTO = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";

    private String placeID = null;
    private String url = null;

    private Button btnBack;
    private LinearLayout lnHeader;
    private TextView txtNamePlace;
    private TextView txtAddress;
    private ImageView imvRating , imvOpenNow;

    private ListView lvReviews;

    TextView txtPhone, txtWeb, txtRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details_layout);
        //get place id from Main Activity
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getBundleExtra("dataID");
            if (bundle != null) {
                placeID = bundle.getString("placeID");
                buidURL();
            }
        }

        btnBack = (Button) findViewById(R.id.btn_back_details);
        lnHeader = (LinearLayout) findViewById(R.id.ln_photo_details);
        txtNamePlace = (TextView) findViewById(R.id.txt_name_place);
        txtAddress = (TextView) findViewById(R.id.txt_address);
        txtPhone = (TextView) findViewById(R.id.txt_phone);
        txtWeb = (TextView) findViewById(R.id.txt_web);
        txtRating = (TextView) findViewById(R.id.txt_rating);
        imvRating = (ImageView) findViewById(R.id.imv_rating);
        imvOpenNow =(ImageView)findViewById(R.id.ic_open_now);

        lvReviews = (ListView) findViewById(R.id.lvReviews);

        if (url != null) {
            PlaceTask placeDtails = new PlaceTask();
            placeDtails.execute(url);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });
    }   //xxx--xx----end onCreate-----xx-xxx

    private void buidURL() {
        url = BASIC_URL + placeID + LANGUAGE + TXT_KEY + KEY_API_PLACES;
        Log.d("test", url);
    }

    private class PlaceTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            JSONObject result = null;
            try {
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            ParseTask parseTask = new ParseTask();
            parseTask.execute(s);
        }
    }

    private class ParseTask extends AsyncTask<String, Integer, PlaceDetails> {

        @Override
        protected PlaceDetails doInBackground(String... strings) {
            PlaceDetails data = null;
            JSONParsePlaces parse = new JSONParsePlaces();
            data = parse.DetailsParse(strings[0]);
            return data;
        }


        @Override
        protected void onPostExecute(PlaceDetails placeDetails) {
            updateInfor(placeDetails);
            buidPhoto(placeDetails);
        }
    }

    // dowload and buid string json from webservice
    private String downloadUrl(String strUrl) throws IOException {

        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);


            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

    //update UI
    private void updateInfor(PlaceDetails place) {
        txtNamePlace.setText(place.getName());
        txtAddress.setText(place.getAddress());
        txtPhone.setText(place.getPhoneNumber());
        txtWeb.setText(place.getWebsite());
        if (place.getRating() == -1)
            txtRating.setText("empty");
        else
            txtRating.setText(String.valueOf(place.getRating()));

        // data list view Reviews
        if (place.getReview() != null) {
            ReviewAdapter reviewAdapter = new ReviewAdapter(this, R.layout.list_review_details_item, place.getReview());
            lvReviews.setAdapter(reviewAdapter);
        }
        //open now
        String openNow;
        openNow = place.getOpenNow();
        if(openNow.equals("true")){
            imvOpenNow.setImageResource(R.drawable.opened);
        }else{
            if(openNow.equals("false")){
                imvOpenNow.setImageResource(R.drawable.closed);
            }else{
                imvOpenNow.setImageResource(R.drawable.no_data_open_now);
            }
        }
        //update rating
        double rating = place.getRating();
        //Toast.makeText(getApplicationContext(),rating+"",Toast.LENGTH_LONG).show();
        if (rating == -1 ) {
            txtRating.setText("no data");
            imvRating.setImageResource(R.drawable.rating0);
        } else {
            if (rating == 1) {
                txtRating.setText(rating + "");
                imvRating.setImageResource(R.drawable.rating1);
            } else {
                if (rating > 1 && rating < 2) {
                    txtRating.setText(rating + "");
                    imvRating.setImageResource(R.drawable.rating15);
                } else {
                    if (rating == 2) {
                        txtRating.setText(rating + "");
                        imvRating.setImageResource(R.drawable.rating2);
                    } else {
                        if (rating > 2 && rating < 3) {
                            txtRating.setText(rating + "");
                            imvRating.setImageResource(R.drawable.rating25);
                        } else {
                            if (rating == 3) {
                                txtRating.setText(rating + "");
                                imvRating.setImageResource(R.drawable.rating3);
                            } else {
                                if (rating > 3 && rating < 4) {
                                    txtRating.setText(rating + "");
                                    imvRating.setImageResource(R.drawable.rating35);
                                } else {
                                    if (rating == 4) {
                                        txtRating.setText(rating + "");
                                        imvRating.setImageResource(R.drawable.rating4);
                                    } else {
                                        if (rating > 4 && rating < 5) {
                                            txtRating.setText(rating + "");
                                            imvRating.setImageResource(R.drawable.rating45);
                                        } else {
                                            if (rating == 5) {
                                                txtRating.setText(rating + "");
                                                imvRating.setImageResource(R.drawable.rating5);

                                            } else {
                                                if (rating > 0 && rating < 1) {
                                                    txtRating.setText(rating + "");
                                                    imvRating.setImageResource(R.drawable.rating05);
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

    // download image and add Bitmap to cache
    private class DownloadImgAsyncTask extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... f_url) {
            int count;
            Bitmap imgBitmap = null;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // Read the file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                imgBitmap = BitmapFactory.decodeStream(input);
                input.close();

            } catch (Exception e) {
                return null;
            }

            return imgBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bimap) {
            updatePhoto(bimap);
        }
    }

    //buid url to get place photo
    private void buidPhoto(PlaceDetails place) {
        ArrayList<String> photos = place.getPhoto();
        if (photos != null) {
            String urlPhoto = BASiC_URL_PHOTO + photos.get(0).trim() + TXT_KEY + KEY_API_PLACES;
            Log.d("test", urlPhoto);
            DownloadImgAsyncTask downPhoto = new DownloadImgAsyncTask();
            downPhoto.execute(urlPhoto);
        }

    }

    private void updatePhoto(Bitmap placePhoto) {

        if (placePhoto != null) {
            BitmapDrawable photo = new BitmapDrawable(placePhoto);
            lnHeader.setBackground(photo);
        }

    }


}
