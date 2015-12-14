package nl.sightguide.sightguide.activities;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.helpers.AudioHelper;
import nl.sightguide.sightguide.helpers.DatabaseHelper;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.helpers.ImageHelper;
import nl.sightguide.sightguide.models.Marker;
import nl.sightguide.sightguide.services.AudioServices;

public class Attraction extends AppCompatActivity implements View.OnClickListener {


    private String attractionName;
    private int cityID;
    private int langID;
    private TextView progress;
    private TextView duration;
    private DatabaseHelper mydb ;
    static MediaPlayer audio;
    private boolean playing = false;
    Thread updateSeekBar;
    SeekBar seekBar;
    ImageView toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        progress =  (TextView) findViewById(R.id.progress);
        duration =  (TextView) findViewById(R.id.duration);
        toggle = (ImageView) findViewById(R.id.toggle);
        toggle.setOnClickListener(this);

        Intent intent = getIntent();

        attractionName = intent.getStringExtra("name");

        TextView informationView = (TextView)findViewById(R.id.attrInfo);

        Marker marker = Utils.realm.where(Marker.class).equalTo("name", attractionName).findFirst();

        setTitle(marker.getName());

        ImageView imageView = (ImageView) findViewById(R.id.mainImage);

        imageView.setImageBitmap(ImageHelper.getImage(marker.getImage()));
        informationView.setText(marker.getInformation());

        audio = AudioHelper.getAudio(marker.getAudio());

        //audio.setScreenOnWhilePlaying(true);
        audio.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        seekBar.setMax(audio.getDuration());


        duration.setText(" / "+AudioHelper.getDuration());

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

                        final String time = AudioHelper.getCurrentTime(currentPosition);

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

//        if(playing == false){
//            audio.start();
//            toggle.setImageResource(R.drawable.pause);
//            if (updateSeekBar.getState() == Thread.State.NEW)
//            {
//                updateSeekBar.start();
//            }
//            playing = true;
//        }else{
//            audio.pause();
//            toggle.setImageResource(R.drawable.play);
//            playing = false;
//        }

    }

}
