package nl.sightguide.sightguide.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import io.realm.Realm;
import nl.sightguide.sightguide.helpers.DatabaseHelper;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.helpers.DownloadHelper;
import nl.sightguide.sightguide.models.City;
import nl.sightguide.sightguide.models.Marker;

public class Launcher extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    JSONObject json;
    JSONObject lang;
    String cityName;
    int cityID;
    private SwipeRefreshLayout swipeView;

    private DatabaseHelper mydb ;
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);

        settings = getSharedPreferences("SightGuide", 0);
        editor = settings.edit();

        mydb = new DatabaseHelper(this);

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);

                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new DownloadCityTask().execute();
                    }
                }, 3000);
            }
        });

        new DownloadCityTask().execute();


        final ListView listView = (ListView) findViewById(R.id.view_cities);

        EditText inputSearch = (EditText) findViewById(R.id.itemSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                Launcher.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });


        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                String name = o.toString();
                try {
                    JSONObject obj = Launcher.this.json.getJSONObject(name);
                    Launcher.this.lang = obj.getJSONObject("lang");
                    Launcher.this.cityID = obj.getInt("id");
                    openContextMenu(view);
                } catch (JSONException e) {
                    Log.d("JSONException", e.toString());
                }
            }
        });
    }

    private class DownloadMarkers extends AsyncTask<String, Void, String> {
        private String city_id;
        private String lang;

        private RequestQueue rq;

        @Override
        protected String doInBackground(String... params) {
            try {
                this.city_id = "20";

                if(params[0].equals("10")) {
                    this.city_id = "10";
                }
                this.lang = params[1];

                String url = String.format("http://www.stevenkaan.com/api/get_markers.php?city_id=%s&lang=%s", this.city_id, this.lang);
                Log.e("URL", url);
                return Utils.run(Launcher.this, url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();

                JSONObject parent = new JSONObject(result).getJSONObject("city");
                String name = parent.getString("name");
                String information = parent.getString("information");
                String country = parent.getString("country");
                double latitude = parent.getDouble("latitude");
                double longitude = parent.getDouble("longitude");
                int population = parent.getInt("population");
                int city_id = parent.getInt("id");

                City city = realm.where(City.class).equalTo("id", city_id).findFirst();
                if(city == null){
                    city = new City();
                    city.setId(city_id);
                }

                city.setName(name);
                city.setInformation(information);
                city.setCountry(country);
                city.setLatitude(latitude);
                city.setLongitude(longitude);
                city.setPopulation(population);

                realm.copyToRealmOrUpdate(city);


                JSONArray markers = parent.getJSONArray("markers");
                for(int i = 0; i < markers.length(); i++) {
                    JSONObject obj = markers.getJSONObject(i);


                    String imgUrl = obj.getString("image");
                    String imgName = imgUrl.substring(imgUrl.lastIndexOf('/') + 1);

                    new DownloadHelper(Launcher.this, imgName, imgUrl, "marker");

                    int id = obj.getInt("id");
                    int type_id = obj.getInt("type_id");
                    String marker_name = obj.getString("name");
                    String info = obj.getString("information");
                    double markerLat = obj.getDouble("latitude");
                    double markerLong = obj.getDouble("longitude");

                    Marker marker = realm.where(Marker.class).equalTo("id", id).findFirst();
                    if(marker == null){
                        marker = new Marker();
                        marker.setId(id);
                    }
                    marker.setCity(city);
                    marker.setType_id(type_id);
                    marker.setName(marker_name);
                    marker.setInformation(info);
                    marker.setLatitude(markerLat);
                    marker.setLongitude(markerLong);
                    marker.setImage(imgName);

                    realm.copyToRealmOrUpdate(marker);
                }
                editor.putInt("lastCity", Integer.parseInt(this.city_id));
                editor.putString("lastLang", this.lang);
                editor.commit();

                realm.commitTransaction();
                Intent intent = new Intent(Launcher.this, Home.class);
                startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
    private class DownloadCityTask extends AsyncTask<String, Void, String> {

        private ArrayAdapter adapter;

        @Override
        protected String doInBackground(String... params) {
            try {
                String result = Utils.run(Launcher.this, "http://www.stevenkaan.com/api/get_city.php");

                JSONObject jsonObject;
                try {
                    JSONObject parent = new JSONObject(result);

                    jsonObject = parent.getJSONObject("cities");
                    Launcher.this.json = jsonObject;

                    ArrayList<String> items = new ArrayList<String>();
                    for(Iterator<String> keys = jsonObject.keys(); keys.hasNext(); ){
                        String name = keys.next();
                        items.add(name);
                    }

                    adapter = new ArrayAdapter(Launcher.this, android.R.layout.simple_list_item_1, items);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            swipeView.setRefreshing(false);
            ListView monthsListView = (ListView) findViewById(R.id.view_cities);
            monthsListView.setAdapter(adapter);
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.view_cities) {
            ListView lv = (ListView) v;
            cityName = lv.getItemAtPosition(((AdapterView.AdapterContextMenuInfo) menuInfo).position).toString();
            menu.setHeaderTitle(cityName);

            for(Iterator<String> keys = this.lang.keys(); keys.hasNext(); ){
                String lang = keys.next();
                menu.add(lang);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String lang = item.getTitle().toString();
        new DownloadMarkers().execute(String.format("%d", cityID), lang);
        return true;
    }
}