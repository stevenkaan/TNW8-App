package nl.sightguide.sightguide.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import io.realm.RealmList;
import io.realm.RealmResults;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.broadcast.Proximity;
import nl.sightguide.sightguide.models.City;
import nl.sightguide.sightguide.models.Marker;

public class LocationService extends Service
{
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private Proximity proximity;

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

                Intent intent1 = new Intent();

                Log.e("ID", String.format("%d", marker.getId()));

                intent1.putExtra("marker_id", marker.getId());
                intent1.putExtra("test", "test");
                PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, intent1, 0);

                mLocationManager.addProximityAlert(marker.getLatitude(), marker.getLongitude(), 100, -1, pi);
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

        registerReceiver(proximity, new IntentFilter("nl.sightguide.sightguide"));

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
        }
    }
}