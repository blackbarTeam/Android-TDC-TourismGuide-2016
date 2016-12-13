package com.edu.tdc.blackbar.tourismguide;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.Settings;

import com.wang.avi.AVLoadingIndicatorView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_layout);
        AVLoadingIndicatorView loading = (AVLoadingIndicatorView) findViewById(R.id.aviSplashLoading) ;
        loading.show();
        if(isOnline()) {
           goNextActivity();
        }else{
            showAlerDialog();
        }

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void showAlerDialog(){
        AlertDialog.Builder warning = new AlertDialog.Builder(this);
        warning.setCancelable(true);
        warning.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent sitting = new Intent(Settings.ACTION_SETTINGS);
                        startActivityForResult(sitting,1);
                        dialogInterface.cancel();
                    }
                });

        warning.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        finish();
                    }
                });

        warning.setTitle("NetWork Connection Fail").setMessage("Do you want to go network setting?");
        warning.setIcon(R.drawable.warning);
        AlertDialog connectionFail = warning.create();
        connectionFail.show();
    }

    public void goNextActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 4000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(isOnline())
                goNextActivity();
            else
                showAlerDialog();
        }
    }
}

