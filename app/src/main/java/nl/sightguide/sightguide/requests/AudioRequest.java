package nl.sightguide.sightguide.requests;

import android.media.MediaPlayer;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

public class AudioRequest extends Request<byte[]> {
    private final Response.Listener<byte[]> mListener;
    private Map<String, String> mParams;
    public Map<String, String> responseHeaders ;

    public AudioRequest(String mUrl,Response.Listener<byte[]> listener,
            Response.ErrorListener errorListener, HashMap<String, String> params) {

        super(1, mUrl, errorListener);
        // this request would never use cache.
        setShouldCache(false);
        mListener = listener;
        mParams=params;
    }

    @Override
    protected Map<String, String> getParams()
    throws com.android.volley.AuthFailureError {
        return mParams;
    };

    @Override
    protected void deliverResponse(byte[] response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {

        //Initialise local responseHeaders map with response headers received
        responseHeaders = response.headers;

        byte[] data = response.data;
        //Pass the response data here
        Log.e("Download", "Complete?");

        //return null;
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));

    }
}