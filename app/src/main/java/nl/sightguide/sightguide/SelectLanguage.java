package nl.sightguide.sightguide;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SelectLanguage extends AppCompatActivity {
    ArrayAdapter<String> adapter;
    private JSONObject langObject;
    private String name;

    private int ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        Intent intent = getIntent();

        this.ID = intent.getIntExtra("ID", 0);
        name = intent.getStringExtra("Name");

        setTitle("Kies een taal voor: " + name);


        try {
            langObject = new JSONObject(intent.getStringExtra("LanguageData"));
            ArrayList<String> items = new ArrayList<String>();

            for(Iterator<String> keys = langObject.keys(); keys.hasNext(); ){
                String lang = keys.next();
                items.add(lang);
            }

            ListView languageSelect = (ListView) findViewById(R.id.languageSelect);
            adapter = new ArrayAdapter(SelectLanguage.this, android.R.layout.simple_list_item_1, items);
            languageSelect.setAdapter(adapter);

        } catch (JSONException e) {
            Log.d("JSONException", e.toString());
        }

        final ListView listView = (ListView) findViewById(R.id.languageSelect);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                String name = o.toString();
                try {
                    int langID = langObject.getInt(name);


                    String url = String.format("http://www.stevenkaan.com/api/get_markers.php?city_id=%d&lang_id=%d", 10, langID);
                    new DownloadCityTask().execute(url);

                } catch (JSONException e) {
                    Log.d("JSONException", e.toString());
                }
            }
        });

    }

    private class DownloadCityTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                return MainActivity.downloadContent(params[0]);
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //Here you are done with the task
            JSONObject jsonObject;
            try {
                JSONObject parent = new JSONObject(result).getJSONObject("city");
                JSONArray markers = parent.getJSONArray("markers");


//                ArrayList<String> items = new ArrayList<String>();
//                for(int i = 0; i < markers.length(); i++) {
//                    JSONObject obj = markers.getJSONObject(i);
//                    items.add(obj.getString("name"));
//                }
//
//                ListView languageSelect = (ListView) findViewById(R.id.languageSelect);
//                adapter = new ArrayAdapter(SelectLanguage.this, android.R.layout.simple_list_item_1, items);
//                languageSelect.setAdapter(adapter);

                List<Map<String, String>> data = new ArrayList<Map<String, String>>();
                for(int i = 0; i < markers.length(); i++) {
                    JSONObject obj = markers.getJSONObject(i);
                    Map<String, String> datum = new HashMap<String, String>(2);
                    datum.put("name", obj.getString("name"));
                    datum.put("desc", obj.getString("desc"));
                    data.add(datum);
                }
                SimpleAdapter adapter = new SimpleAdapter(SelectLanguage.this, data,
                        android.R.layout.simple_list_item_1,
                        new String[] {"name", "desc"},
                        new int[] {android.R.id.text1,
                                android.R.id.text2});
                ListView languageSelect = (ListView) findViewById(R.id.languageSelect);
                languageSelect.setAdapter(adapter);

                //Intent intent = new Intent(SelectLanguage.this, SelectMarkers.class);
                //startActivity(intent);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }









    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_language, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
