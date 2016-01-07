package nl.sightguide.sightguide.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.helpers.AudioHelper;
import nl.sightguide.sightguide.models.Marker;


public class Proximity extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String key = LocationManager.KEY_PROXIMITY_ENTERING;

        boolean state = intent.getBooleanExtra(key, false);
        int marker_id = intent.getBundleExtra("extra").getInt("id", 0);

        Bundle extras = intent.getExtras();

        Log.e("Extras", "Test: " + intent.getBundleExtra("extra").getInt("id"));
        Log.e("State", Boolean.toString(state));

        Log.e("Intent", intent.getExtras().toString());

        Marker marker = Utils.realm.where(Marker.class).equalTo("id", marker_id).findFirst();
        if(marker != null) {
            if (state) {
                Log.e("MyTag", "Welcome to my " + marker.getName());
                Toast.makeText(context, "Welcome to my " + marker.getName(), Toast.LENGTH_SHORT).show();

                MediaPlayer mp = AudioHelper.getAudio("Immortals-Centuries.mp3");

                mp.start();

            } else {
                //Other custom Notification
                Log.e("MyTag", "Thank you for visiting my " + marker.getName() + ", come back again !!");
                Toast.makeText(context, "Thank you for visiting my " + marker.getName() + ", come back again !!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}