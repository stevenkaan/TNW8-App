package nl.sightguide.sightguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import io.realm.Realm;

public class Utils {

    public static Realm realm;
    public static SharedPreferences preferences;
    public static SharedPreferences.Editor editor;
    public static int city_id;
    public static int language;
    public static Float maxZoom = 8f;
    public static Float startingZoom = 11f;
    public static MediaPlayer currentAudio;


    public static String apiURL = "http://sightguide.eu:3000";
//    public static String apiURL = "http://stevenkaan.com/api/";

    public static final OkHttpClient client = new OkHttpClient();

    public static void toast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
    public static void toastL(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
    public static boolean checkNetwork(Context c) {
        ConnectivityManager conManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conManager.getActiveNetworkInfo();
        return ( netInfo != null && netInfo.isConnected() );
    }
    public static String run(Context c, String url) throws IOException {
        if(checkNetwork(c)) {

            Log.e("URL", Utils.apiURL +"/api/"+ url);
            Request request = new Request.Builder()
                    .url(Utils.apiURL + "/api/" + url)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        }
        return null;
    }
    public static boolean isConnected(final Context context) {
        try {
            final ConnectivityManager connMngr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            final NetworkInfo wifiNetwork = connMngr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiNetwork != null && wifiNetwork.isConnectedOrConnecting()) { return true; }

            final NetworkInfo mobileNetwork = connMngr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mobileNetwork != null && mobileNetwork.isConnectedOrConnecting()) { return true; }

            final NetworkInfo activeNetwork = connMngr.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) { return true; }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isGPSTurnOn(final Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
