package nl.sightguide.sightguide.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.realm.RealmList;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.helpers.AudioHelper;
import nl.sightguide.sightguide.helpers.GPSHelper;
import nl.sightguide.sightguide.helpers.ImageHelper;
import nl.sightguide.sightguide.helpers.MapHelper;
import nl.sightguide.sightguide.models.Marker;
import nl.sightguide.sightguide.models.Route;


public class RouteInfo extends AppCompatActivity {

    private int routeId;
    private int markerId;
    private int markerIcon;
    private int currentIcon;

    private GoogleMap mMap;
    private ScrollView mScrollView;
    private LatLng cityLatLng;
    private GPSHelper gps;

    static MediaPlayer audio;
    private boolean playing = false;
    private TextView progress;
    private TextView duration;
    Thread updateSeekBar;
    SeekBar seekBar;
    ImageView toggle;
    private String audioFile = "";
    private Marker marker;
    private Route route;
    private LatLng startingPoint;

    private LatLng lastMarker;
    private boolean firstMarker = true;
    private LatLngBounds presetBounds;
    private LatLngBounds bounds;
    private CameraUpdate cu;
    private boolean play = false;
    private ImageView imageView;
    private int img = 1;
    int totalImages = 0;

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


        Intent intent = getIntent();
        markerId = intent.getIntExtra("marker", 0);
        marker = Utils.realm.where(Marker.class).equalTo("id", markerId).findFirst();

        routeId = intent.getIntExtra("id", 0);
        route = Utils.realm.where(Route.class).equalTo("id", routeId).findFirst();

        markerIcon = intent.getIntExtra("markerIcon", 0);
        currentIcon = intent.getIntExtra("currentIcon", 0);
        firstMarker = intent.getBooleanExtra("first", true);
        play = intent.getBooleanExtra("play", false);

        String number = Integer.toString(markerIcon + 1)+". ";
        setTitle(number + marker.getName());


        TextView informationView = (TextView)findViewById(R.id.routeInfo);
        informationView.setText(marker.getInformation());

        TextView markerName = (TextView)findViewById(R.id.markerName);
        markerName.setText(number + marker.getName());

        TextView attractionName = (TextView)findViewById(R.id.attractionName);
        attractionName.setText(number + marker.getName());

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
        // show nav btns if more than 1 img
        if(totalImages >  0) {
            imageView.setVisibility(View.VISIBLE);
            if(totalImages >  1) {
                RelativeLayout nav = (RelativeLayout) findViewById(R.id.navButtons);
                nav.setVisibility(View.VISIBLE);
            }
        }

        // set audio
        audioFile = marker.getAudio();

        audio = AudioHelper.getAudio(marker.getAudio());
        audio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                audio.seekTo(0);
                toggle.setImageResource(R.drawable.play);
                playing = false;

                // if autoplay is set, go to next marker and play audio
                if(Utils.preferences.getBoolean("autoPlay", true)) {
                    play = true;
                    if(audioFile != null) {
                        nextMarker();
                    }
                }
            }

        });

        if(audioFile == null) {
            RelativeLayout audio = (RelativeLayout) findViewById(R.id.audio);
            audio.setVisibility(View.GONE);
            RelativeLayout noAudio = (RelativeLayout) findViewById(R.id.no_audio);
            noAudio.setVisibility(View.VISIBLE);
        }

        audio.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        seekBar.setMax(audio.getDuration());

        duration.setText(" / " + AudioHelper.getDuration());


        mMap = ((MapHelper) getSupportFragmentManager().findFragmentById(R.id.fragment_map)).getMap();
        mScrollView = (ScrollView) findViewById(R.id.sv_container);

        ((MapHelper) getSupportFragmentManager().findFragmentById(R.id.fragment_map)).setListener(new MapHelper.OnTouchListener() {
            @Override
            public void onTouch() {
                mScrollView.requestDisallowInterceptTouchEvent(true);
                }
            });


        cityLatLng = new LatLng(3.14, 3.14);
        presetBounds  = new LatLngBounds(cityLatLng,cityLatLng);
        bounds = presetBounds;

        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(
                new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition position) {
                        if (position.zoom < Utils.maxZoom)
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng, Utils.maxZoom));
                    }
                }
        );

        RealmList<Marker> markers = route.getMarkers();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        ArrayList<LatLng> locList = new ArrayList<LatLng>();
        for(int i = 0; i < markers.size(); i++) {
            Marker marker = markers.get(i);

            LatLng markerLatLng = new LatLng(marker.getLatitude(), marker.getLongitude());
            locList.add(markerLatLng);
            int SetIcon;
            if(i == markerIcon){

                startingPoint = new LatLng(marker.getLatitude(), marker.getLongitude());

                switch (i+1) {
                    case 1:  SetIcon = R.drawable.active_marker_1;
                        break;
                    case 2:  SetIcon = R.drawable.active_marker_2;
                        break;
                    case 3:  SetIcon = R.drawable.active_marker_3;
                        break;
                    case 4:  SetIcon = R.drawable.active_marker_4;
                        break;
                    case 5:  SetIcon = R.drawable.active_marker_5;
                        break;
                    case 6:  SetIcon = R.drawable.active_marker_6;
                        break;
                    case 7:  SetIcon = R.drawable.active_marker_7;
                        break;
                    case 8:  SetIcon = R.drawable.active_marker_8;
                        break;
                    case 9:  SetIcon = R.drawable.active_marker_9;
                        break;
                    case 10: SetIcon = R.drawable.active_marker_10;
                        break;
                    case 11: SetIcon = R.drawable.active_marker_11;
                        break;
                    case 12: SetIcon = R.drawable.active_marker_12;
                        break;
                    case 13: SetIcon = R.drawable.active_marker_13;
                        break;
                    case 14: SetIcon = R.drawable.active_marker_14;
                        break;
                    case 15: SetIcon = R.drawable.active_marker_15;
                        break;
                    case 16: SetIcon = R.drawable.active_marker_16;
                        break;
                    case 17: SetIcon = R.drawable.active_marker_17;
                        break;
                    case 18: SetIcon = R.drawable.active_marker_18;
                        break;
                    case 19: SetIcon = R.drawable.active_marker_19;
                        break;
                    case 20: SetIcon = R.drawable.active_marker_20;
                        break;
                    default: SetIcon = R.drawable.active_marker_0;
                        break;
                }

            }else{
                switch (i+1) {
                    case 1:  SetIcon = R.drawable.marker_1;
                        break;
                    case 2:  SetIcon = R.drawable.marker_2;
                        break;
                    case 3:  SetIcon = R.drawable.marker_3;
                        break;
                    case 4:  SetIcon = R.drawable.marker_4;
                        break;
                    case 5:  SetIcon = R.drawable.marker_5;
                        break;
                    case 6:  SetIcon = R.drawable.marker_6;
                        break;
                    case 7:  SetIcon = R.drawable.marker_7;
                        break;
                    case 8:  SetIcon = R.drawable.marker_8;
                        break;
                    case 9:  SetIcon = R.drawable.marker_9;
                        break;
                    case 10: SetIcon = R.drawable.marker_10;
                        break;
                    case 11: SetIcon = R.drawable.marker_11;
                        break;
                    case 12: SetIcon = R.drawable.marker_12;
                        break;
                    case 13: SetIcon = R.drawable.marker_13;
                        break;
                    case 14: SetIcon = R.drawable.marker_14;
                        break;
                    case 15: SetIcon = R.drawable.marker_15;
                        break;
                    case 16: SetIcon = R.drawable.marker_16;
                        break;
                    case 17: SetIcon = R.drawable.marker_17;
                        break;
                    case 18: SetIcon = R.drawable.marker_18;
                        break;
                    case 19: SetIcon = R.drawable.marker_19;
                        break;
                    case 20: SetIcon = R.drawable.marker_20;
                        break;
                    default: SetIcon = R.drawable.marker_0;
                        break;
                }
            }


            // add last marker to create loop in route begin- is also endpoint
            Marker firstMarker = markers.get(0);
            lastMarker = new LatLng(firstMarker.getLatitude(), firstMarker.getLongitude());

            mMap.addMarker(new MarkerOptions()
                    .position(markerLatLng)
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(SetIcon))
                    .title(marker.getName()));

            builder.include(markerLatLng);
        }
        locList.add(lastMarker);
        // get path
        ArrayList<LatLng> pathList = new ArrayList<LatLng>();
        String path = route.getPath();
        String delimiters = "\\]\\,\\[|\\[|\\]";
        String[] tokensVal = path.split(delimiters);
        LatLng startingPoint = new LatLng(0.0,0.0);
        for(int i = 1; i < tokensVal.length; i++) {
            String fullLatLong = tokensVal[i];
            String delimiter = "\\,";
            String[] coordinates = fullLatLong.split(delimiter);
            Double thisLat = Double.valueOf(coordinates[0]);
            Double thisLng = Double.valueOf(coordinates[coordinates.length - 1]);
            if(i == 1){
                startingPoint = new LatLng(thisLat,thisLng);
            }
            LatLng pathPoint = new LatLng(thisLat,thisLng);
            pathList.add(pathPoint);
        }
        pathList.add(startingPoint);
        mMap.addPolyline((new PolylineOptions()).addAll(pathList)
                .width(7)
                .color(0xFFe96745)
                .geodesic(false));

        ArrayList<LatLng> toStart = new ArrayList<LatLng>();

        // add my location
        gps = new GPSHelper(this);
        gps.getMyLocation();
        LatLng myLatLng = new LatLng(gps.getLatitude(), gps.getLongitude());


        if(firstMarker) {
            builder.include(myLatLng);
        }

        mMap.addMarker(new MarkerOptions()
                .position(myLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.me))
                .title(getString (R.string.location)));


        bounds = builder.build();

        Log.e("bounds", "" + bounds);
        int padding = 50;
        cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(
                new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition position) {
                        if (position.zoom < Utils.maxZoom)
                            mMap.moveCamera(cu);
                    }
                }
        );

        updateSeekBar = new Thread() {
            @Override
            public void run(){
                int totalDuration = audio.getDuration();
                int currentPosition = 0;
                while (currentPosition < totalDuration) {
                    try {
                        sleep(1000);
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
        if(play){
            audio.start();
            toggle.setImageResource(R.drawable.pause);
            if (updateSeekBar.getState() == Thread.State.NEW) {
                updateSeekBar.start();
            }
            playing = true;
        }

    }
    @Override
    protected void onPause(){
        super.onPause();
        audio.pause();
    }

    public void toggle(View view){
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


    // navigate between markers
    public void next(View v){
        nextMarker();
    }
    public void nextMarker(){
        RealmList<Marker> markers = route.getMarkers();
        int current = markerIcon;
        int next = (markerIcon + 1);
        if((markers.size() -2 ) < markerIcon){
            next = 0;
        }

        int nextId = markers.get(0).getId();
        for(int i = 0; i < markers.size(); i++) {
            Marker thisMarker = markers.get(i);
            if(i == next){
                nextId = thisMarker.getId();
            }
        }
        Intent intent = new Intent(this, RouteInfo.class);
        intent.putExtra("id", routeId);
        intent.putExtra("marker", nextId);
        intent.putExtra("markerIcon", next);
        intent.putExtra("currentIcon", current);
        intent.putExtra("first", false);
        intent.putExtra("play", play);
        startActivity(intent);
        finish();
    }
    public void previous(View v){
        RealmList<Marker> markers = route.getMarkers();
        int current = markerIcon;
        int next = (markerIcon - 1);
        if( markerIcon == 0){
            next = markers.size() - 1;
        }

        int nextId = markers.get(0).getId();
        for(int i = 0; i < markers.size(); i++) {
            Marker thisMarker = markers.get(i);
            if(i == next){
                nextId = thisMarker.getId();
            }
        }
        Intent intent = new Intent(this, RouteInfo.class);
        intent.putExtra("id", routeId);
        intent.putExtra("marker", nextId);
        intent.putExtra("markerIcon", next);
        intent.putExtra("currentIcon", current);
        intent.putExtra("first", false);
        intent.putExtra("play", false);
        startActivity(intent);
        finish();
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
}
