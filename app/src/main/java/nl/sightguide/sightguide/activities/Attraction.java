package nl.sightguide.sightguide.activities;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.helpers.AudioHelper;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.helpers.ImageHelper;
import nl.sightguide.sightguide.models.Marker;

public class Attraction extends AppCompatActivity implements View.OnClickListener {


    private String attractionName;
    private String audioFile = "";
    private TextView progress;
    private TextView duration;
    static MediaPlayer audio;
    private boolean playing = false;
    Thread updateSeekBar;
    SeekBar seekBar;
    ImageView toggle;
    private ImageView imageView;
    private int img = 1;
    int totalImages = 0;
    private Marker marker;
    int totalDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        progress =  (TextView) findViewById(R.id.progress);
        duration =  (TextView) findViewById(R.id.duration);
        toggle = (ImageView) findViewById(R.id.toggle);
        toggle.setOnClickListener(this);

        // Get intent
        Intent intent = getIntent();
        attractionName = intent.getStringExtra("name");


        // get marker data
        marker = Utils.realm.where(Marker.class).equalTo("name", attractionName).findFirst();

        //set data
        setTitle(marker.getName());

        TextView informationView = (TextView)findViewById(R.id.attrInfo);
        TextView attrName = (TextView)findViewById(R.id.attractionName);
        informationView.setText(marker.getInformation());
        attrName.setText(marker.getName());

        imageView = (ImageView) findViewById(R.id.mainImage);

        // set first image and count nr of images
        if (marker.getImage_1() != null && !marker.getImage_1().isEmpty()) {
            imageView.setImageBitmap(ImageHelper.getImage(marker.getImage_1(), "marker"));
            totalImages++;
            if (marker.getImage_2() != null && !marker.getImage_2().isEmpty()) {
                totalImages++;
                if (marker.getImage_3() != null && !marker.getImage_3().isEmpty()) {
                    totalImages++;
                    if (marker.getImage_4() != null && !marker.getImage_4().isEmpty()) {
                        totalImages++;
                    }
                }
            }
        }

        // check if there's audio
        audioFile = marker.getAudio();
        Log.e("audiofile","path: "+ audioFile);

        if(audioFile == null) {
            RelativeLayout audio = (RelativeLayout) findViewById(R.id.audio);
            audio.setVisibility(View.GONE);
            RelativeLayout noAudio = (RelativeLayout) findViewById(R.id.no_audio);
            noAudio.setVisibility(View.VISIBLE);
        }

        audio = AudioHelper.getAudio(marker.getAudio());
        audio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e("finished","true");
                audio.seekTo(0);
                toggle.setImageResource(R.drawable.play);
                playing = false;
            }

        });

        //audio.setScreenOnWhilePlaying(true);
        audio.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        seekBar.setMax(audio.getDuration());


        duration.setText(" / " + AudioHelper.getDuration());

        updateSeekBar = new Thread() {
            @Override
            public void run() {
                totalDuration = audio.getDuration();
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
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        };




        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // hoooi
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                audio.seekTo(seekBar.getProgress());
            }
        });
        // show nav btns if more than 1 img
        if(totalImages >  1) {
            ImageView left = (ImageView) findViewById(R.id.left);
            ImageView right = (ImageView) findViewById(R.id.right);
            right.setVisibility(View.VISIBLE);
            left.setVisibility(View.VISIBLE);
        }
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
            if (updateSeekBar.getState() == Thread.State.NEW) {
                updateSeekBar.start();
            }
            playing = true;
        }else{
            audio.pause();
            toggle.setImageResource(R.drawable.play);
            playing = false;
        }

    }
    // show next img
    public void nextImg(View v){
        img++;
        if(img > totalImages){
            img = 1;
        }
        setImage(img);
    }
    // show previous img
    public void prevImg(View v){
        img--;
        if(img < 1){
            img = totalImages;
        }
        setImage(img);
    }
    public void setImage (int img){
        switch (img) {
            case 1:
                imageView.setImageBitmap(ImageHelper.getImage(marker.getImage_1(), "marker"));
                break;
            case 2:
                imageView.setImageBitmap(ImageHelper.getImage(marker.getImage_2(), "marker"));
                break;
            case 3:
                imageView.setImageBitmap(ImageHelper.getImage(marker.getImage_3(), "marker"));
                break;
            case 4:
                imageView.setImageBitmap(ImageHelper.getImage(marker.getImage_4(), "marker"));
                break;
            default:
                imageView.setImageBitmap(ImageHelper.getImage(marker.getImage_1(), "marker"));
                break;
        }
    }
    // view attraction on map
    public void viewMap(View v){
        Intent intent = new Intent(this, Map.class);
        intent.putExtra("marker_id", marker.getId());
        startActivity(intent);
    }

}
