package nl.sightguide.sightguide.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.helpers.SwipeDetector;
import nl.sightguide.sightguide.models.Route;


public class RouteInfo extends AppCompatActivity implements View.OnClickListener{

    private String routeName;

    static final String logTag = "SwipeDetector";
    private Activity activity;
    static final int MIN_DISTANCE = 100;
    private float downX, downY, upX, upY;

    static MediaPlayer audio;
    private boolean playing = false;
    private TextView routeInfo;
    private TextView progress;
    private TextView duration;
    Thread updateSeekBar;
    SeekBar seekBar;
    ImageView toggle;
    RelativeLayout panel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        progress =  (TextView) findViewById(R.id.progress);
        duration =  (TextView) findViewById(R.id.duration);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        audio = MediaPlayer.create(this, R.raw.frogsound);
        seekBar.setMax(audio.getDuration());
        toggle = (ImageView) findViewById(R.id.toggle);

        toggle.setOnClickListener(this);
        Intent intent = getIntent();

        routeName = intent.getStringExtra("name");
        setTitle(routeName);

        SwipeDetector s = new SwipeDetector(this);
        panel = (RelativeLayout)this.findViewById(R.id.panel);
        panel.setOnTouchListener(s);

        final String totalDuration = String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(audio.getDuration()),
                TimeUnit.MILLISECONDS.toSeconds(audio.getDuration()),
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(audio.getDuration())));
        duration.setText(" / "+totalDuration);

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

                        final String time = String.format("%d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(currentPosition),
                                TimeUnit.MILLISECONDS.toSeconds(currentPosition),
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentPosition)));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.setText(time);

                            }
                        });

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

        RelativeLayout extraInfo =(RelativeLayout)v.findViewById(R.id.extraInfo);
        extraInfo.setVisibility(LinearLayout.VISIBLE);

    }

    public static void swipeDown(View v) {

        RelativeLayout extraInfo =(RelativeLayout)v.findViewById(R.id.extraInfo);
        extraInfo.setVisibility(LinearLayout.GONE);
    }
}
