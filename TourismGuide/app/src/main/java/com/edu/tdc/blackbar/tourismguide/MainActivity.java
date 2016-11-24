package com.edu.tdc.blackbar.tourismguide;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.WindowDecorActionBar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fabPlus, fabNearBy, fabSchedule, fabLocateMe;
    private boolean flagMenuOpen = false;
    private Animation fabMoveUpRotate, plusOpenRotate, plusCloseRotate, fabMoveDown, textMoveUp, textMoveOut;
    private TextView txtLocate, txtNearBy, txtSchedule;
    private DrawerLayout drawerNearBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        final String a = "thom boe";

        fabSchedule = (FloatingActionButton) findViewById(R.id.fab_setSchedule);
        fabNearBy = (FloatingActionButton) findViewById(R.id.fab_nearBy);
        fabLocateMe = (FloatingActionButton) findViewById(R.id.fab_locate_me);

        txtLocate = (TextView) findViewById(R.id.txt_decript_lme);
        txtNearBy = (TextView) findViewById(R.id.txt_decript_nearb);
        txtSchedule = (TextView) findViewById(R.id.txt_decript_schedule);

        //load amination from anim
        fabMoveUpRotate = AnimationUtils.loadAnimation(this, R.anim.fab_move_up_rotate);
        plusOpenRotate = AnimationUtils.loadAnimation(this, R.anim.fab_plus_open_rotate);
        plusCloseRotate = AnimationUtils.loadAnimation(this, R.anim.fab_plus_close_rotate);
        fabMoveDown = AnimationUtils.loadAnimation(this, R.anim.fab_move_down);
        textMoveUp = AnimationUtils.loadAnimation(this,R.anim.text_move_up);
        textMoveOut = AnimationUtils.loadAnimation(this,R.anim.text_move_out);

        //load drawer near by
        drawerNearBy = (DrawerLayout)findViewById(R.id.drawer_layout);


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

        //btn locate me by on click
        fabLocateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //xu ly vao day

                hideChildMenu();
                flagMenuOpen = false;
            }
        });

        //btn near by on click
        fabNearBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //xu ly vao day
                drawerNearBy.openDrawer(GravityCompat.START);
                hideChildMenu();
                flagMenuOpen = false;
            }
        });

        //btn setschedule by on click
        fabSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //xu ly vao day

                hideChildMenu();
                flagMenuOpen = false;
            }
        });
    }

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

}