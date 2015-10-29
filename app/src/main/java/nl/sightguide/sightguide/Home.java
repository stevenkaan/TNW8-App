package nl.sightguide.sightguide;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Home extends AppCompatActivity implements OnMapReadyCallback {

    private String cityName;
    private int cityID;
    private int langID;
    private DatabaseHelper mydb ;

    private GoogleMap mMap;
    private LatLng city;
    private Float maxZoom = 8f;
    private Float startingZoom = 12.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mydb = new DatabaseHelper(this);

        Intent intent = getIntent();

        cityName = intent.getStringExtra("cityName");
        cityID = intent.getIntExtra("cityID", 0);
        langID = intent.getIntExtra("langID", 0);

        MapFragment mapFrag =
                (MapFragment)getFragmentManager().findFragmentById(R.id.map);

        if (savedInstanceState == null) {
            mapFrag.getMapAsync(this);
        }

        new AsyncTask<String, Integer, String> (){

            @Override
            protected String doInBackground(String... params) {
                try {
                    return Utils.run(Home.this, String.format("http://www.stevenkaan.com/api/get_markers.php?city_id=%d&lang_id=%d", 10, langID));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONObject parent = new JSONObject(result).getJSONObject("city");
                    String country = parent.getString("country");
                    double latitude = parent.getDouble("latitude");
                    double longitude = parent.getDouble("longitude");
                    String name = parent.getString("name");
                    int population = parent.getInt("population");
                    int city_id = parent.getInt("id");

                    if(mydb.checkCity(city_id) == false) {
                        if (mydb.insertCity(city_id, name, country, latitude, longitude, population)) {
                            Log.e("db", "success");
                        } else {
                            Log.e("db", "failed");
                        }
                    }else{
                        Log.e("db", "already exists");
                    }

                    JSONArray markers = parent.getJSONArray("markers");

                List<Map<String, String>> data = new ArrayList<Map<String, String>>();
                for(int i = 0; i < markers.length(); i++) {
                    JSONObject obj = markers.getJSONObject(i);

                        int id = obj.getInt("id");
                        int type_id = obj.getInt("type_id");
                        String marker_name = obj.getString("name");
                        String info = obj.getString("information");
                        double markerLat = obj.getDouble("latitude");
                        double markerLong = obj.getDouble("longitude");

                        if(mydb.checkMarker(id) == false) {
                            if (mydb.insertMarker(id, city_id, type_id, marker_name, info, latitude, longitude)) {
                                Log.d("db", "successfully inserted marker");
                            } else {
                                Log.d("db", "failed to insert marker");
                            }

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    public void ShowTypes(MenuItem item){
        Intent intent = new Intent(this, AttractionList.class);

        startActivity(intent);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        Location location = locationManager.getLastKnownLocation(provider);
        city = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(
                new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition position) {
                        if (position.zoom < maxZoom)
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(city, maxZoom));
                    }
                }
        );

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(city, 17f));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(city, startingZoom));
    }
}
