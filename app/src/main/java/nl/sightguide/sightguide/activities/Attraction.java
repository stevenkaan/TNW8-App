package nl.sightguide.sightguide.activities;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.helpers.DatabaseHelper;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.models.Marker;

public class Attraction extends AppCompatActivity implements View.OnClickListener {


    private String attractionName;
    private int cityID;
    private int langID;
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
        audio = MediaPlayer.create(this, R.raw.frogsound);
        seekBar.setMax(audio.getDuration());
        toggle = (ImageView) findViewById(R.id.toggle);
        toggle.setOnClickListener(this);

        Intent intent = getIntent();

        attractionName = intent.getStringExtra("name");

        TextView informationView = (TextView)findViewById(R.id.attrInfo);

        Marker marker = Utils.realm.where(Marker.class).equalTo("name", attractionName).findFirst();

        setTitle(marker.getName());

        String photoPath = Environment.getExternalStorageDirectory()+"/sightguide_images/"+marker.getImage();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
        ImageView imageView = (ImageView) findViewById(R.id.mainImage);

        imageView.setImageBitmap(bitmap);
        informationView.setText(marker.getInformation());

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

        if(playing == false){
            audio.start();
            toggle.setImageResource(R.drawable.pause);
            if (updateSeekBar.getState() == Thread.State.NEW)
            {
                updateSeekBar.start();
            }
            playing = true;
        }else{
            audio.pause();
            toggle.setImageResource(R.drawable.play);
            playing = false;
        }

    };

}
