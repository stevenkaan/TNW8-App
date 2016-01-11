package nl.sightguide.sightguide.services;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import java.util.List;

import io.realm.RealmResults;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.helpers.AudioHelper;
import nl.sightguide.sightguide.helpers.GPSHelper;
import nl.sightguide.sightguide.models.Marker;

public class ProximitySensor extends Service {

    private Boolean loop = false;
    private RealmResults<Marker> attractions;
    static MediaPlayer audio;
    private boolean playing = false;
    private LocationManager locationManager;
    private double latitude;
    private double longitude;
    private Context context;


    @Override
    public IBinder onBind(Intent arg0){


        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        loop = true;
        //set sensor.active to true
        SharedPreferences settings = getSharedPreferences("SightGuide", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("proximitySensorActive", true);
        editor.apply();

        attractions = Utils.realm.where(Marker.class).equalTo("city.id", Utils.city_id).contains("audio", ".mp3").findAll();

        checkInterval();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {

        loop = false;
        SharedPreferences settings = getSharedPreferences("SightGuide", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("proximitySensorActive", false);
        editor.apply();

        super.onDestroy();
    }

    public void checkInterval() {
        Log.e("checkInterval", "running");

        // check nearby markers every x seconds
        final Handler h = new Handler();
        final int delay = 4500;
        h.postDelayed(new Runnable() {
            public void run() {
                checkDistance();
                if(loop) {
                    h.postDelayed(this, delay);
                }
             }
        }, delay);
    }
    public void checkDistance(){

        GPSHelper gps = new GPSHelper(this);
        gps.getMyLocation();
        Double lat = gps.getLatitude();
        Double lng = gps.getLongitude();

        int markerId;
        String markerAudio = "";
        if(!playing){
            for(int i = 0; i < attractions.size(); i++) {
                Marker thisMarker = attractions.get(i);

                Location loc1 = new Location("");
                loc1.setLatitude(thisMarker.getLatitude());
                loc1.setLongitude(thisMarker.getLongitude());

                Location loc2 = new Location("");
                loc2.setLatitude(lat);
                loc2.setLongitude(lng);

                // set data for closest marker
                float distanceInMeters = loc1.distanceTo(loc2);
                if(distanceInMeters < 20){
                    SharedPreferences settings = getSharedPreferences("SightGuide", 0);

                    markerId = thisMarker.getId();
                    markerAudio = thisMarker.getAudio();

                    String idToString = Integer.toString(markerId);
                    int listened = settings.getInt(idToString, 0);
                    if(listened == 0){
                        audio = AudioHelper.getAudio(markerAudio);
                        audio.start();
                        playing = true;

                        // mark marker as 'listened'
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt(idToString, markerId);
                        editor.apply();

                        audio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                audio.seekTo(0);
                                playing = false;
                            }

                        });
                    }
                }
            }
        }


    }
}