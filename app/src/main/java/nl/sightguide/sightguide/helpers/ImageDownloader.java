package nl.sightguide.sightguide.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import nl.sightguide.sightguide.Utils;

public class ImageDownloader implements Response.Listener<Bitmap> {
    private String name;
    private String url;
    private String baseDir = "Images";
    private String subDir;

    public ImageDownloader(String name, String url, String subDir) {
        this.url = url;
        this.name = name;
        this.subDir = subDir;

    }

    public ImageRequest execute() {
        try {
            String fileName = URLEncoder.encode(name, "UTF-8");

            ImageRequest ir = new ImageRequest(Utils.apiURL + url + "/" + fileName.replace("+", "%20"), this, 0, 0, null, null, null);

            return ir;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onResponse(Bitmap response) {
        String root = Environment.getExternalStorageDirectory().toString();

        File rootDir = new File(root + "/SightGuide/"+baseDir+"/"+subDir);
        rootDir.mkdirs();

        File file = new File (rootDir, this.name);

        if (file.exists ()) {
            file.delete ();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            response.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
