package nl.sightguide.sightguide.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.helpers.DatabaseHelper;
import nl.sightguide.sightguide.helpers.SwipeDetector;


public class Route extends AppCompatActivity implements View.OnClickListener{

    private String routeName;

    static final String logTag = "SwipeDetector";
    private Activity activity;
    static final int MIN_DISTANCE = 100;
    private float downX, downY, upX, upY;

    private int cityID;
    private int langID;
    private DatabaseHelper mydb ;
    static MediaPlayer audio;
    private boolean playing = false;
    Thread updateSeekBar;
    SeekBar seekBar;
    ImageView toggle;
    RelativeLayout panel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        audio = MediaPlayer.create(this, R.raw.frogsound);
        seekBar.setMax(audio.getDuration());
        toggle = (ImageView) findViewById(R.id.toggle);
        toggle.setOnClickListener(this);


        mydb = new DatabaseHelper(this);

        Intent intent = getIntent();

        routeName = intent.getStringExtra("name");
        setTitle(routeName);

        SwipeDetector s = new SwipeDetector(this);
        panel = (RelativeLayout)this.findViewById(R.id.panel);
        panel.setOnTouchListener(s);

        updateSeekBar = new Thread() {
            @Override
            public void run(){
                int totalDuration = audio.getDuration();
                int currentPosition = 0;
                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = audio.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }

        };

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                audio.seekTo(seekBar.getProgress());
            }
        });
    }
    @Override
    protected void onPause(){
        super.onPause();
        audio.pause();
    }

    @Override
    public void onClick(View view){
        if(view == toggle) {

            if (playing == false) {
                audio.start();
                toggle.setImageResource(R.drawable.pause);
                if (updateSeekBar.getState() == Thread.State.NEW) {
                    updateSeekBar.start();
                }
                playing = true;
            } else {
                audio.pause();
                toggle.setImageResource(R.drawable.play);
                playing = false;
            }
        }else{
            Log.e("click","true");
        }
    };

    public static void swipeRight(View v) {
    }
    public static void swipeLeft(View v) {
    }
    public static void swipeUp(View v) {
        v.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
    }
    public static void swipeDown(View v) {
        v.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 200));
    }
}
