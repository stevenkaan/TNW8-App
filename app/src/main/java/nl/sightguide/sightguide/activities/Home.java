package nl.sightguide.sightguide.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.helpers.DatabaseHelper;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.models.City;
import nl.sightguide.sightguide.models.Marker;
import nl.sightguide.sightguide.services.LocationService;


public class Home extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng cityLatLng;

    private String[] mMenuItems;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private MenuItem item;

    private boolean menuOpen = false;
    private boolean itemSet = false;

    private City city;
    private RealmResults<Marker> markers;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        city = Utils.realm.where(City.class).equalTo("id", Utils.city_id).findFirst();
        if(city == null){
            Log.e("ERROR", "Doesn't exists");
            setTitle("Error");
        } else {
            setTitle(city.getName());
        }

        intent = new Intent(this, LocationService.class);
        intent.putExtra("city_id", Utils.city_id);
        startService(intent);

        mMenuItems = getResources().getStringArray(R.array.menu_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, mMenuItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, null, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                menuOpen = false;
                if(itemSet){
                    item.setIcon(R.drawable.ic_menu_white_24dp);
                }
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                menuOpen = true;
                if(itemSet) {
                    item.setIcon(R.drawable.abc_ic_clear_mtrl_alpha);
                }
            }
        };


        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        MapFragment mapFrag = (MapFragment)getFragmentManager().findFragmentById(R.id.map);

        if (savedInstanceState == null) {
            mapFrag.getMapAsync(this);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e("Action", "onDestroy");
        stopService(intent);
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            SwitchView(position);
        }
    }
    public void ToggleDrawer(MenuItem item){
        this.item = item;
        itemSet = true;
        if(menuOpen){
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            menuOpen = false;
            item.setIcon(R.drawable.ic_menu_white_24dp);
        } else {
            mDrawerLayout.openDrawer(Gravity.LEFT);
            item.setIcon(R.drawable.abc_ic_clear_mtrl_alpha);
            menuOpen = true;
        }
    }
    public void SwitchView(int item){

        Intent intent;
        switch (item) {
            case 0:
                intent = new Intent(this, AttractionList.class);
                intent.putExtra("city_id", city.getId());
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(this, RouteList.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(this, CityInfo.class);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(this, Launcher.class);
                startActivity(intent);
                break;
            case 4:
                intent = new Intent(this, SettingList.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);

        RealmResults<Marker> markers = Utils.realm.where(Marker.class).equalTo("city.id", Utils.city_id).findAll();
        if(markers.size() == 0) {
            Log.e("onMapReady", "0 markers found");
            Log.e("CityID", String.format("%d", Utils.city_id));
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for(int i = 0; i < markers.size(); i++) {
            Marker marker = markers.get(i);

            LatLng markerLatLng = new LatLng(marker.getLatitude(), marker.getLongitude());

            mMap.addMarker(new MarkerOptions()
                    .position(markerLatLng)
                    .title(marker.getName()));

            builder.include(markerLatLng);
        }

        LatLngBounds bounds = builder.build();

        int padding = 50; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 500, 500, padding);
        mMap.animateCamera(cu);
    }

}
