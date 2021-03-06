package nl.sightguide.sightguide.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
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

public class DownloadHelper implements Response.Listener<Bitmap> {
    private String name;
    private String baseDir;

    public DownloadHelper(Context c, String name, String url, String baseDir) {
        try {

            String fileName = URLEncoder.encode(name, "UTF-8");


            RequestQueue rq = Volley.newRequestQueue(c);
            ImageRequest ir = new ImageRequest(url + "/" + fileName.replace("+", "%20"), this, 0, 0, null, null, null);
            this.name = name;
            this.baseDir = baseDir;
            rq.add(ir);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResponse(Bitmap response) {
        String root = Environment.getExternalStorageDirectory().toString();
        Log.e("BaseDir", baseDir);

        File rootDir = new File(root + "/SightGuide/"+baseDir);
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
