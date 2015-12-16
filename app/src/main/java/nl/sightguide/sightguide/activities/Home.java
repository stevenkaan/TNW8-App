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

    private String[] mMenuItems;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private MenuItem item;

    private boolean menuOpen = false;
    private boolean itemSet = false;

    private City city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        city = Utils.realm.where(City.class).equalTo("id", Utils.city_id).findFirst();

        setTitle(city.getName());

        MapFragment mapFrag = (MapFragment)getFragmentManager().findFragmentById(R.id.map);

        if (savedInstanceState == null) {
            mapFrag.getMapAsync(this);
        }

        mMenuItems = getResources().getStringArray(R.array.menu_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, mMenuItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, null, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                menuOpen = false;
                if(itemSet){
                    item.setIcon(R.drawable.ic_menu_white_24dp);
                }
            }

            /** Called when a drawer has settled in a completely open state. */
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


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
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

        cityLatLng = new LatLng(city.getLatitude(), city.getLongitude());


        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(
                new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition position) {
                        if (position.zoom < Utils.maxZoom)
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng, Utils.maxZoom));
                    }
                }
        );

        mMap.addMarker(new MarkerOptions()
                .position(cityLatLng)
                .title("Hello world"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng, 17f));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng, Utils.startingZoom));
    }

}
