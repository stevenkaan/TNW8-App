package nl.sightguide.sightguide.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.helpers.AudioHelper;

public class AudioServices extends Service {

    private MediaPlayer mp;

    public IBinder onBind(Intent arg0) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mp = AudioHelper.getAudio("Immortals-Centuries.mp3");

        //mp = MediaPlayer.create(this, R.raw.frogsound);
        mp.setLooping(true); // Set looping
        mp.setVolume(100,100);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        mp.start();
        return 1;
    }

    public void onStart(Intent intent, int startId) {
        // TO DO
    }
    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    public void onStop() {

    }
    public void onPause() {

    }
    @Override
    public void onDestroy() {
        mp.stop();
        mp.release();
    }

    @Override
    public void onLowMemory() {

    }
}
