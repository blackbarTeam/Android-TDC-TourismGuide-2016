package com.edu.tdc.blackbar.tourismguide;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fabPlus, fabNearBy, fabChedule, fabLocateMe;
    private boolean flagMenuOpen = false;
    private Animation fabMoveUpRotate, plusOpenRotate, plusCloseRotate, fabMoveDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        final String a = "thom boe";

        fabChedule = (FloatingActionButton) findViewById(R.id.fab_setSchedule);
        fabNearBy = (FloatingActionButton) findViewById(R.id.fab_nearBy);
        fabLocateMe = (FloatingActionButton) findViewById(R.id.fab_locate_me);

        //load amination from anim
        fabMoveUpRotate = AnimationUtils.loadAnimation(this, R.anim.fab_move_up_rotate);
        plusOpenRotate = AnimationUtils.loadAnimation(this, R.anim.fab_plus_open_rotate);
        plusCloseRotate = AnimationUtils.loadAnimation(this, R.anim.fab_plus_close_rotate);
        fabMoveDown = AnimationUtils.loadAnimation(this, R.anim.fab_move_down);

        fabPlus = (FloatingActionButton) findViewById(R.id.fab_plus);
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

        //btn near by on click
        fabNearBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void showChildMenu() {
        fabChedule.show();
        fabNearBy.show();
        fabLocateMe.show();

        fabPlus.startAnimation(plusOpenRotate);
        fabLocateMe.startAnimation(fabMoveUpRotate);
        fabNearBy.startAnimation(fabMoveUpRotate);
        fabChedule.startAnimation(fabMoveUpRotate);

        fabChedule.setClickable(true);
        fabNearBy.setClickable(true);
        fabLocateMe.setClickable(true);
    }

    private void hideChildMenu() {
        fabPlus.startAnimation(plusCloseRotate);
        fabLocateMe.startAnimation(fabMoveDown);
        fabNearBy.startAnimation(fabMoveDown);
        fabChedule.startAnimation(fabMoveDown);

        fabChedule.hide();
        fabNearBy.hide();
        fabLocateMe.hide();

        fabChedule.setClickable(false);
        fabNearBy.setClickable(false);
        fabLocateMe.setClickable(false);
    }

}