package nl.sightguide.sightguide.activities;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.broadcast.Proximity;
import nl.sightguide.sightguide.helpers.DatabaseHelper;
import io.realm.RealmResults;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.helpers.GPSHelper;
import nl.sightguide.sightguide.models.City;
import nl.sightguide.sightguide.models.Marker;
import nl.sightguide.sightguide.services.LocationService;
import nl.sightguide.sightguide.models.Type;


public class Home extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng cityLatLng;
    private LatLng cityLatLng2;
    private GPSHelper gps;

    private String[] mMenuItems;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private MenuItem item;

    private boolean menuOpen = false;
    private boolean itemSet = false;

    private LatLngBounds presetBounds;
    private LatLngBounds bounds;
    private CameraUpdate cu;
    private LatLng markerLatLng;

    private int SetIcon;

    private City city;
    private RealmResults<Marker> markers;
    private Intent intent;

    private String label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Utils.preferences = getSharedPreferences("SightGuide", 0);

        city = Utils.realm.where(City.class).equalTo("id", Utils.city_id).findFirst();

        if(city == null){
            Log.e("ERROR", "Doesn't exists");
            setTitle("Error");
        } else {
            setTitle(city.getName());
        }

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


        RealmResults<Marker> markers = Utils.realm.where(Marker.class).equalTo("city.id", Utils.city_id).findAll();

        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        registerReceiver(new Proximity(), new IntentFilter("nl.sightguide"));

        try {

            for(int i = 0; i < markers.size(); i++) {
                Marker marker = markers.get(i);

                Bundle bundle = new Bundle();
                bundle.putInt("marker_id", marker.getId());

                Intent intent = new Intent("nl.sightguide");
                intent.putExtra("extra", bundle);

                PendingIntent pi1 = PendingIntent.getBroadcast(getApplicationContext(), i, intent, 0);

                lm.addProximityAlert(marker.getLatitude(), marker.getLongitude(), 100, -1, pi1);
            }

        } catch (SecurityException e) {
            Log.e("Error", e.toString());
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

//    @Override
//    public void onDestroy(){
//        super.onDestroy();
//        Log.e("Action", "onDestroy");
//        stopService(intent);
//    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            SwitchView(position);
        }
    }

    // open/close drawer
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

    // Select menu item from drawer
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
                intent = new Intent(this, CityList.class);
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

    // Open filter activity
    public void setFilter (View v) {
        Intent intent = new Intent(this, FilterList.class);
        startActivity(intent);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        cityLatLng = new LatLng(52.31414585644944,4.798707962036133);
        cityLatLng2 = new LatLng(53.20392479751652,5.797390937805176);
        presetBounds  = new LatLngBounds(cityLatLng,cityLatLng2);
        bounds = presetBounds;

        gps = new GPSHelper(this);
        gps.getMyLocation();


        RealmResults<Marker> attractions = Utils.realm.where(Marker.class).equalTo("city.id", Utils.city_id).findAll();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        mMap.setMyLocationEnabled(true);

        for(int i = 0; i < attractions.size(); i++) {

            Marker attraction = attractions.get(i);

            // add marker to map
            markerLatLng = new LatLng(attraction.getLatitude(), attraction.getLongitude());
            builder.include(markerLatLng);

            // check if type is set to display
            Type type = Utils.realm.where(Type.class).equalTo("type",attraction.getType() ).findFirst();

            if(type.getDisplay()){
                //set Correct marker
                switch (attraction.getType()) {
                    case 0:  SetIcon = R.drawable.type_0;
                        break;
                    case 1:  SetIcon = R.drawable.type_1;
                        break;
                    case 2:  SetIcon = R.drawable.type_2;
                        break;
                    case 3:  SetIcon = R.drawable.type_3;
                        break;
                    case 4:  SetIcon = R.drawable.type_4;
                        break;
                    case 5:  SetIcon = R.drawable.type_5;
                        break;
                    case 6:  SetIcon = R.drawable.type_6;
                        break;
                    case 7:  SetIcon = R.drawable.type_7;
                        break;
                    case 8:  SetIcon = R.drawable.type_8;
                        break;
                    case 9:  SetIcon = R.drawable.type_9;
                        break;
                    case 10: SetIcon = R.drawable.type_10;
                        break;
                    case 11: SetIcon = R.drawable.type_11;
                        break;
                    case 12: SetIcon = R.drawable.type_12;
                        break;
                    case 13: SetIcon = R.drawable.type_13;
                        break;
                    case 14: SetIcon = R.drawable.type_14;
                        break;
                    case 15: SetIcon = R.drawable.type_15;
                        break;
                    case 16: SetIcon = R.drawable.type_16;
                        break;
                    case 17: SetIcon = R.drawable.type_17;
                        break;
                    case 18: SetIcon = R.drawable.type_18;
                        break;
                    case 19: SetIcon = R.drawable.type_19;
                        break;
                    case 20: SetIcon = R.drawable.type_20;
                        break;
                    case 21: SetIcon = R.drawable.type_21;
                        break;
                    case 22: SetIcon = R.drawable.type_22;
                        break;
                    case 23: SetIcon = R.drawable.type_23;
                        break;
                    case 24: SetIcon = R.drawable.type_24;
                        break;
                    case 25: SetIcon = R.drawable.type_25;
                        break;
                    default: SetIcon = R.drawable.type_0;
                        break;
                }

                if(attraction.getInformation() != null){
                    label = attraction.getName();
                }else{
                    int lang = Utils.preferences.getInt("language",0);
                    switch (lang) {
                        case 0:
                            label = type.getName_nl();
                            break;
                        case 2:
                            label = type.getName_en();
                            break;
                        case 3:
                            label = type.getName_es();
                            break;
                        default:
                            label = type.getName_en();
                            break;
                    }

                }

                mMap.addMarker(new MarkerOptions()
                        .position(markerLatLng)
                        .icon(BitmapDescriptorFactory.fromResource(SetIcon))
                        .title(label));

            }



        }

        bounds = builder.build();
        Log.e("build", "" + bounds);
        int padding = 50;
        cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(
                new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition position) {
                        LatLng myLatLng = new LatLng(gps.getLatitude(), gps.getLongitude());
                        mMap.addMarker(new MarkerOptions()
                                .position(myLatLng)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.me))
                                .title(getString (R.string.location)));
                        if (position.zoom < Utils.maxZoom)
                            mMap.moveCamera(cu);
                    }
                }
        );
    }

}
