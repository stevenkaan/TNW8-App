package nl.sightguide.sightguide.helpers;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Environment;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.requests.AudioRequest;

public class AudioDownloader implements Response.Listener<byte[]> {
    private String name;
    private String url;
    private String baseDir = "Audio";

    public AudioDownloader(String name, String url) {
        this.url = url;
        this.name = name;


    }

    public AudioRequest execute() {
        try {
            String fileName = URLEncoder.encode(name, "UTF-8");

            AudioRequest ir = new AudioRequest(Utils.apiURL + url + "/" + fileName.replace("+", "%20"), this, null, null);

            return ir;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onResponse(byte[] response) {
        String root = Environment.getExternalStorageDirectory().toString();

        File rootDir = new File(root + "/SightGuide/"+baseDir);
        rootDir.mkdirs();

        File file = new File (rootDir, this.name);


        if (file.exists ()) {
            file.delete ();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(response);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
