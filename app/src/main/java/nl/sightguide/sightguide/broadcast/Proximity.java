package nl.sightguide.sightguide.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.Map;
import java.util.Set;

import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.helpers.AudioHelper;
import nl.sightguide.sightguide.models.Marker;


public class Proximity extends BroadcastReceiver {

    private Boolean playing = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String key = LocationManager.KEY_PROXIMITY_ENTERING;

        boolean state = intent.getBooleanExtra(key, false);
        int marker_id = intent.getBundleExtra("extra").getInt("marker_id", 0);

        Marker marker = Utils.realm.where(Marker.class).equalTo("id", marker_id).findFirst();
        if(marker != null && !playing) {
            if(marker.getAudio() == null){
                return;
            }
            if (state) {
                // if proximity sensor is set: play audio
                if(Utils.preferences.getBoolean("proximitySensor", true)) {
                    Log.e("Test", "Audio: " + marker.getAudio());
                    if (Utils.currentAudio != null) {
                        Utils.currentAudio.stop();
                    }
                    Utils.currentAudio = AudioHelper.getAudio(marker.getAudio());
                    Utils.currentAudio.start();
                    playing = true;
                }

            } else {
                if(Utils.currentAudio != null) {
                    Utils.currentAudio.stop();
                    Utils.currentAudio = null;
                }
            }
        }
        Utils.currentAudio.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                playing = false;
            }

        });
    }
}