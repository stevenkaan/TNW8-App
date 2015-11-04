package nl.sightguide.sightguide;

import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Launcher extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    JSONObject json;
    JSONObject lang;
    String cityName;
    int cityID;
    private SwipeRefreshLayout swipeView;


    String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                Log.d("Swipe", "Refreshing Number");
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new DownloadTask().execute();
                    }
                }, 3000);
            }
        });

        new DownloadTask().execute();

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

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                s = Utils.run(Launcher.this, "http://www.stevenkaan.com/api/get_city.php");
                return s;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //Here you are done with the task
            JSONObject jsonObject;
            try {
                swipeView.setRefreshing(false);
                if(result  == null){
                    return;
                }
                JSONObject parent = new JSONObject(result);

                jsonObject = parent.getJSONObject("cities");
                Launcher.this.json = jsonObject;

                ArrayList<String> items = new ArrayList<String>();
                for(Iterator<String> keys = jsonObject.keys(); keys.hasNext(); ){
                    String name = keys.next();
                    items.add(name);
                }

                ListView monthsListView = (ListView) findViewById(R.id.view_cities);
                adapter = new ArrayAdapter(Launcher.this, android.R.layout.simple_list_item_1, items);
                monthsListView.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        try {
            int langID = this.lang.getInt(lang);

            Intent intent = new Intent(Launcher.this, Home.class);
            intent.putExtra("cityID", cityID);
            intent.putExtra("langID", langID);
            intent.putExtra("cityName", cityName);

            startActivity(intent);

        } catch (JSONException e) {
            Log.d("JSONException", e.toString());
        }
        return true;
    }
}