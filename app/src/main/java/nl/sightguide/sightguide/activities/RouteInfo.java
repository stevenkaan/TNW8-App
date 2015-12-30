package nl.sightguide.sightguide.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import nl.sightguide.sightguide.helpers.GPSHelper;
import nl.sightguide.sightguide.helpers.SwipeDetectorRoute;
import nl.sightguide.sightguide.models.Marker;
import nl.sightguide.sightguide.models.Route;


public class RouteInfo extends AppCompatActivity implements OnMapReadyCallback {

    private String routeName;
    private int routeId;
    private int markerId;
    private int markerIcon;
    private int currentIcon;

    private GoogleMap mMap;
    private LatLng cityLatLng;
    private GPSHelper gps;

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
    private String audioFile = "";
    private Marker marker;
    private Marker nav;
    private Route route;
    private LatLng startingPoint;

    private LatLng lastMarker;
    private boolean firstMarker = true;
    private boolean pageSet = false;
    private LatLngBounds presetBounds;
    private LatLngBounds bounds;
    private CameraUpdate cu;

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

//        toggle.setOnClickListener(this);

        Intent intent = getIntent();
        markerId = intent.getIntExtra("marker", 0);
        marker = Utils.realm.where(Marker.class).equalTo("id", markerId).findFirst();

        routeId = intent.getIntExtra("id", 0);
        route = Utils.realm.where(Route.class).equalTo("id", routeId).findFirst();

        markerIcon = intent.getIntExtra("markerIcon", 0);
        currentIcon = intent.getIntExtra("currentIcon", 0);
        firstMarker = intent.getBooleanExtra("first", true);


        setTitle(marker.getName());



        TextView informationView = (TextView)findViewById(R.id.routeInfo);
        informationView.setText(marker.getInformation());

        TextView markerName = (TextView)findViewById(R.id.markerName);
        markerName.setText(marker.getName());

//        ImageView imageView = (ImageView) findViewById(R.id.mainImage);
//        imageView.setImageBitmap(ImageHelper.getImage(marker.getImage_1(), "marker"));

//        audioFile = marker.getAudio();
//
//        if(audioFile == null) {
//            ImageView gradient = (ImageView) findViewById(R.id.gradient);
//            gradient.setVisibility(View.GONE);
//            RelativeLayout audioContent = (RelativeLayout) findViewById(R.id.audioContent);
//            audioContent.setVisibility(View.GONE);
//        }
//
//        audio = AudioHelper.getAudio(marker.getAudio());
//
//        //audio.setScreenOnWhilePlaying(true);
//        audio.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
//        seekBar.setMax(audio.getDuration());
//
//
//        duration.setText(" / " + AudioHelper.getDuration());

        MapFragment mapFrag = (MapFragment)getFragmentManager().findFragmentById(R.id.map);

        if (savedInstanceState == null) {
            mapFrag.getMapAsync(this);
        }

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

        // set swipe detector
        SwipeDetectorRoute s = new SwipeDetectorRoute(this);
        panel = (RelativeLayout)this.findViewById(R.id.panel);
        panel.setOnTouchListener(s);
    }
    @Override
    protected void onPause(){
        super.onPause();
        audio.pause();
    }

//    @Override
//    public void onClick(View view){
//        if(view == toggle) {
//
//            if (playing == false) {
//                audio.start();
//                toggle.setImageResource(R.drawable.pause);
//                if (updateSeekBar.getState() == Thread.State.NEW) {
//                    updateSeekBar.start();
//                }
//                playing = true;
//            } else {
//                audio.pause();
//                toggle.setImageResource(R.drawable.play);
//                playing = false;
//            }
//        }else{
//            Log.e("click","true");
//        }
//    };

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
    // navigate between markers

    public void next(View v){
        Log.i("action","loading next marker");
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
        startActivity(intent);
    }
    public void previous(View v){

        Log.i("action","loading previous marker");
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
        startActivity(intent);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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


            // add last marker to creat loop in route begin- is also end point
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
        mMap.addPolyline((new PolylineOptions()).addAll(locList)
                .width(7)
                .color(0xFFe96745)
                .geodesic(false));

        ArrayList<LatLng> toStart = new ArrayList<LatLng>();

        // add my location
        gps = new GPSHelper(this);
        gps.getMyLocation();
        LatLng myLatLng = new LatLng(gps.getLatitude(), gps.getLongitude());
        builder.include(myLatLng);

        if(firstMarker) {
            toStart.add(myLatLng);
            toStart.add(startingPoint);
        }else{
            Marker lastMarker = markers.get((currentIcon));
            LatLng last = new LatLng(lastMarker.getLatitude(), lastMarker.getLongitude());
            Marker destMarker = markers.get(markerIcon);
            LatLng dest = new LatLng(destMarker.getLatitude(), destMarker.getLongitude());
            toStart.add(last);
            toStart.add(dest);
        }
        mMap.addPolyline((new PolylineOptions()).addAll(toStart)
                .width(7)
                .color(0xFF3abdbd)
                .geodesic(false));

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






    }


}
