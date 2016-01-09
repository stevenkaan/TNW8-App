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
        int marker_id = intent.getBundleExtra("extra").getInt("marker_id", 0);

        Marker marker = Utils.realm.where(Marker.class).equalTo("id", marker_id).findFirst();
        if(marker != null) {
            if(marker.getAudio() == null){
                return;
            }
            if (state) {
                Log.e("Test", "Audio: " + marker.getAudio());
                if(Utils.currentAudio != null){
                    Utils.currentAudio.stop();
                }
                Utils.currentAudio = AudioHelper.getAudio(marker.getAudio());


                Utils.currentAudio.start();

            } else {
                if(Utils.currentAudio != null) {
                    Utils.currentAudio.stop();
                    Utils.currentAudio = null;
                }
            }
        }
    }
}