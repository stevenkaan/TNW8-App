package nl.sightguide.sightguide.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import io.realm.RealmList;
import io.realm.RealmResults;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.activities.Home;
import nl.sightguide.sightguide.activities.Launcher;
import nl.sightguide.sightguide.broadcast.Proximity;
import nl.sightguide.sightguide.models.City;
import nl.sightguide.sightguide.models.Marker;

public class LocationService extends Service implements LocationListener {
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private Proximity proximity;
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    //Intent Action
    String ACTION_FILTER = "nl.sightguide.sightguide";


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int id = intent.getIntExtra("city_id", 0);

        Log.e("CityID", String.format("%d", id));

        RealmResults<Marker> markers = Utils.realm.where(Marker.class).equalTo("city.id", intent.getIntExtra("city_id", 0)).findAll();

        try {
            for(int i = 0; i < markers.size(); i++) {
                Marker marker = markers.get(i);

                Bundle extras = new Bundle();
                extras.putInt("marker_id", marker.getId());

                Intent intent1 = new Intent(ACTION_FILTER);
                intent1.putExtra(ACTION_FILTER, extras);

                PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, intent1, PendingIntent.FLAG_CANCEL_CURRENT);

                //mLocationManager.addProximityAlert(marker.getLatitude(), marker.getLongitude(), 230, -1, pi);
            }
        } catch (SecurityException e) {
            Log.e("Error", e.toString());
        }

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
    @Override
    public void onCreate() {
        proximity = new Proximity();
        registerReceiver(proximity, new IntentFilter(ACTION_FILTER));
        initializeLocationManager();
    }
    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();

        unregisterReceiver(proximity);
    }
    private void initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            //mLocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, false);
            //mLocationManager.setTestProviderEnabled(LocationManager.NETWORK_PROVIDER, true);

            try {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, this);
            } catch (SecurityException e) {
                Log.e("Error", e.toString());
            }
        }
    }




    // DEBUGGING ONLY (I HOPE)
    public void onLocationChanged(Location newLocation) {

        Location old = new Location("OLD");

        /* NHL Bushalte */
//        old.setLatitude(53.21240659153594);
//        old.setLongitude(5.79753041267395);

        /* Stenden Bushalte */
        old.setLatitude(53.21040838064042);
        old.setLongitude(5.795411467552185);

        double distance = newLocation.distanceTo(old);

        Log.i("MyTag", "Distance: " + distance);
    }

    @Override
    public void onProviderDisabled(String arg0) {}

    @Override
    public void onProviderEnabled(String arg0) {}

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}

}