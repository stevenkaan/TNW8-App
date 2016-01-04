package nl.sightguide.sightguide.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.models.Marker;


public class Proximity extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String key = LocationManager.KEY_PROXIMITY_ENTERING;

        boolean state = intent.getBooleanExtra(key, false);
        int marker_id = intent.getIntExtra("marker_id", 0);

        Bundle extras = intent.getExtras();

        Log.e("Test", "" + extras.getString("test"));
        Log.e("State", Boolean.toString(state));

//        Log.e("MarkerID", String.format("%d", ));

        Marker marker = Utils.realm.where(Marker.class).equalTo("id", marker_id).findFirst();
        if(marker != null) {
            if (state) {
                // Call the Notification Service or anything else that you would like to do here
                Log.e("MyTag", "Welcome to my " + marker.getName());
                Toast.makeText(context, "Welcome to my " + marker.getName(), Toast.LENGTH_SHORT).show();
            } else {
                //Other custom Notification
                Log.e("MyTag", "Thank you for visiting my " + marker.getName() + ", come back again !!");
                Toast.makeText(context, "Thank you for visiting my " + marker.getName() + ", come back again !!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}