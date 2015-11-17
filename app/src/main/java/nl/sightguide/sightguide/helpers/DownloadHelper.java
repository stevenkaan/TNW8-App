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

public class DownloadHelper implements Response.Listener<Bitmap> {
    private String imgName;

    public DownloadHelper(Context c, String imgName, String url, String baseDir) {
        RequestQueue rq = Volley.newRequestQueue(c);
        ImageRequest ir = new ImageRequest(url, this, 0, 0, null, null, null);
        this.imgName = imgName;
        rq.add(ir);
    }

    @Override
    public void onResponse(Bitmap response) {
        String root = Environment.getExternalStorageDirectory().toString();
        File rootDir = new File(root + "/sightguide_images");
        rootDir.mkdirs();

        File file = new File (rootDir, this.imgName);

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
