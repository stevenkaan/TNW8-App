package nl.sightguide.sightguide.helpers;

import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AudioHelper {
    private static MediaPlayer audio;

    public static MediaPlayer getAudio(String audioName) {
        String audioPath = Environment.getExternalStorageDirectory()+"/SightGuide/Audio/"+audioName;

        audio = new MediaPlayer();
        try {
            Log.e("Path", audioPath);
            audio.setDataSource(audioPath);
            audio.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return audio;
    }

    public static String getDuration(){
        return formatTime("%d:%02d", audio.getDuration());
    }

    public static String getCurrentTime(int position) {
        return formatTime("%d:%02d", position);

    }
    private static String formatTime(String pattern, int location) {
        return String.format(pattern,
                TimeUnit.MILLISECONDS.toMinutes(location),
                TimeUnit.MILLISECONDS.toSeconds(location) % 60,
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(location)));
    }
}
