package nl.sightguide.sightguide.helpers;

import android.os.Environment;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.requests.AudioRequest;

public class AudioDownloader implements Response.Listener<byte[]> {
    private String name;
    private String url;
    private String baseDir = "Audio";
    private AudioRequest ar;

    public AudioDownloader(String name, String url) {
        this.url = url;
        this.name = name;
        Log.e("audio url","this: "+ url);

    }

    public AudioRequest execute() {
        try {
            String fileName = URLEncoder.encode(name, "UTF-8");

            String mUrl = Utils.apiURL + url + "/" + fileName.replace("+", "%20");

            ar = new AudioRequest(Request.Method.GET, mUrl, AudioDownloader.this, null, null);

            Log.e("file","gelukt"+ ar);
            return ar;
        } catch (UnsupportedEncodingException e) {
            Log.e("file","niet gelukt");
            e.printStackTrace();
        }

        return null;
    }
    @Override
    public void onResponse(byte[] response) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        try {
            if (response!=null) {

                String filename = this.name;
                Log.d("DEBUG::FILE NAME", filename);

                try{
                    long lenghtOfFile = response.length;

                    InputStream input = new ByteArrayInputStream(response);

                    String root = Environment.getExternalStorageDirectory().toString();
                    File audioFolder = new File(root+ "/SightGuide/"+baseDir);
                    audioFolder.mkdirs();
                    File file = new File(root+ "/SightGuide/"+baseDir, filename);

                    map.put("resume_path", file.toString());
                    BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
                    byte data[] = new byte[1024];

                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        output.write(data, 0, count);
                    }

                    output.flush();

                    output.close();
                    input.close();
                }catch(IOException e){
                    e.printStackTrace();

                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
            e.printStackTrace();
        }
    }
}
