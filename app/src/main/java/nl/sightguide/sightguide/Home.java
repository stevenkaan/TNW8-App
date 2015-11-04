package nl.sightguide.sightguide;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class Home extends AppCompatActivity implements OnMapReadyCallback {

    private ArrayList city;
    private GoogleMap mMap;
    private LatLng cityLatLng;
    private Float maxZoom = 8f;
    private Float startingZoom = 11f;

    private String[] mPlanetTitles;
    private ListView mDrawerList;
    private SharedPreferences settings;

    private DatabaseHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mydb = new DatabaseHelper(this);

        settings = getSharedPreferences("SightGuide", 0);
        int cityID = settings.getInt("lastCity", 0);

        city = mydb.getCity(cityID);

        cityLatLng = new LatLng(Float.parseFloat(city.get(2).toString()), Float.parseFloat(city.get(3).toString()));

        MapFragment mapFrag =
                (MapFragment)getFragmentManager().findFragmentById(R.id.map);

        if (savedInstanceState == null) {
            mapFrag.getMapAsync(this);
        }


        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);


        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    public void GoToLauncher(MenuItem item){
        Intent intent = new Intent(this, Launcher.class);
        startActivity(intent);
    }
    public void ShowTypes(MenuItem item){
        Intent intent = new Intent(this, AttractionList.class);
        startActivity(intent);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(
                new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition position) {
                        if (position.zoom < maxZoom)
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng, maxZoom));
                    }
                }
        );

        mMap.addMarker(new MarkerOptions()
                .position(cityLatLng)
                .title("Hello world"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng, 17f));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng, startingZoom));
    }

}
