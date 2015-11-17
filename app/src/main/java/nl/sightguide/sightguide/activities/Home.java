package nl.sightguide.sightguide.activities;


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

import io.realm.Realm;
import io.realm.RealmResults;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.helpers.DatabaseHelper;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.models.City;


public class Home extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng cityLatLng;
    private Float maxZoom = 8f;
    private Float startingZoom = 11f;

    private String[] mPlanetTitles;
    private ListView mDrawerList;

    private City city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        int cityID = Utils.preferences.getInt("lastCity", 0);

        city = Utils.realm.where(City.class).equalTo("id", cityID).findFirst();

        setTitle(city.getName());
        cityLatLng = new LatLng(city.getLatitude(), city.getLongitude());

        MapFragment mapFrag = (MapFragment)getFragmentManager().findFragmentById(R.id.map);

        if (savedInstanceState == null) {
            mapFrag.getMapAsync(this);
        }

        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, mPlanetTitles));

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
        intent.putExtra("city_id", city.getId());
        startActivity(intent);
    }
    public void ShowRoutes(MenuItem item) {
        Intent intent = new Intent(this, RouteList.class);
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
