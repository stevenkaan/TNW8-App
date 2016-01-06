package nl.sightguide.sightguide.activities;


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

import io.realm.RealmResults;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.helpers.GPSHelper;
import nl.sightguide.sightguide.models.City;
import nl.sightguide.sightguide.models.Marker;
import nl.sightguide.sightguide.models.Type;

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
import io.realm.RealmResults;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.helpers.GPSHelper;
import nl.sightguide.sightguide.models.City;
import nl.sightguide.sightguide.models.Marker;
import nl.sightguide.sightguide.models.Type;


public class Map extends AppCompatActivity implements OnMapReadyCallback {

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

    private String label;
    private int marker_id;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Get intent
        Intent intent = getIntent();
        marker_id = intent.getIntExtra("marker_id", 0);

        // get marker data
        marker = Utils.realm.where(Marker.class).equalTo("id", marker_id).findFirst();

        //set data
        setTitle(marker.getName());

        MapFragment mapFrag = (MapFragment)getFragmentManager().findFragmentById(R.id.map);

        if (savedInstanceState == null) {
            mapFrag.getMapAsync(this);
        }



    }





    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        gps = new GPSHelper(this);
        gps.getMyLocation();


        // add marker to map
        markerLatLng = new LatLng(marker.getLatitude(), marker.getLongitude());

        //set Correct marker
        switch (marker.getType()) {
            case 0:
                SetIcon = R.drawable.type_0;
                break;
            case 1:
                SetIcon = R.drawable.type_1;
                break;
            case 2:
                SetIcon = R.drawable.type_2;
                break;
            case 3:
                SetIcon = R.drawable.type_3;
                break;
            case 4:
                SetIcon = R.drawable.type_4;
                break;
            case 5:
                SetIcon = R.drawable.type_5;
                break;
            case 6:
                SetIcon = R.drawable.type_6;
                break;
            case 7:
                SetIcon = R.drawable.type_7;
                break;
            case 8:
                SetIcon = R.drawable.type_8;
                break;
            case 9:
                SetIcon = R.drawable.type_9;
                break;
            case 10:
                SetIcon = R.drawable.type_10;
                break;
            case 11:
                SetIcon = R.drawable.type_11;
                break;
            case 12:
                SetIcon = R.drawable.type_12;
                break;
            case 13:
                SetIcon = R.drawable.type_13;
                break;
            case 14:
                SetIcon = R.drawable.type_14;
                break;
            case 15:
                SetIcon = R.drawable.type_15;
                break;
            case 16:
                SetIcon = R.drawable.type_16;
                break;
            case 17:
                SetIcon = R.drawable.type_17;
                break;
            case 18:
                SetIcon = R.drawable.type_18;
                break;
            case 19:
                SetIcon = R.drawable.type_19;
                break;
            case 20:
                SetIcon = R.drawable.type_20;
                break;
            case 21:
                SetIcon = R.drawable.type_21;
                break;
            case 22:
                SetIcon = R.drawable.type_22;
                break;
            case 23:
                SetIcon = R.drawable.type_23;
                break;
            case 24:
                SetIcon = R.drawable.type_24;
                break;
            case 25:
                SetIcon = R.drawable.type_25;
                break;
            default:
                SetIcon = R.drawable.type_0;
                break;

        }

        mMap.addMarker(new MarkerOptions()
                .position(markerLatLng)
                .icon(BitmapDescriptorFactory.fromResource(SetIcon))
                .title(marker.getName()));

        cu = CameraUpdateFactory.newLatLngZoom(markerLatLng, 15);

        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(
                new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition position) {
                        LatLng myLatLng = new LatLng(gps.getLatitude(), gps.getLongitude());
                        mMap.addMarker(new MarkerOptions()
                                .position(myLatLng)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.me))
                                .title(getString(R.string.location)));
                        if (position.zoom < Utils.maxZoom)
                            mMap.moveCamera(cu);
                    }
                }
        );
    }

}
