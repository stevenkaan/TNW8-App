package nl.sightguide.sightguide;

import android.content.Intent;
import android.os.AsyncTask;
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
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    JSONObject json;
    JSONObject lang;
    String cityName;
    int cityID;

    public static final OkHttpClient client = new OkHttpClient();
    String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        new AsyncTask<String, Integer, String> (){

            @Override
            protected String doInBackground(String... params) {
                try {
                    s = run("http://www.stevenkaan.com/api/get_city.php");
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
                    JSONObject parent = new JSONObject(result);

                    jsonObject = parent.getJSONObject("cities");
                    MainActivity.this.json = jsonObject;

                    ArrayList<String> items = new ArrayList<String>();
                    for(Iterator<String> keys = jsonObject.keys(); keys.hasNext(); ){
                        String name = keys.next();
                        items.add(name);
                    }

                    ListView monthsListView = (ListView) findViewById(R.id.view_cities);
                    adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, items);
                    monthsListView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();



        EditText inputSearch = (EditText) findViewById(R.id.itemSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                MainActivity.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        final ListView listView = (ListView) findViewById(R.id.view_cities);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                String name = o.toString();
                try {
                    JSONObject obj = MainActivity.this.json.getJSONObject(name);
                    MainActivity.this.lang = obj.getJSONObject("lang");
                    MainActivity.this.cityID = obj.getInt("id");
                    openContextMenu(view);
                } catch (JSONException e) {
                    Log.d("JSONException", e.toString());
                }
            }
        });
    }


    public static String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.view_cities) {
            ListView lv = (ListView) v;
            cityName = lv.getItemAtPosition(((AdapterContextMenuInfo) menuInfo).position).toString();
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

            Intent intent = new Intent(MainActivity.this, Home.class);
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