package nl.sightguide.sightguide.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import io.realm.RealmResults;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.adapters.AttractionAdapter;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.models.Marker;

public class AttractionList extends AppCompatActivity {

    ListView listView;
    ImageView ul1;
    ImageView ul2;
    ImageView ul3;
    TextView tab1;
    TextView tab2;
    TextView tab3;
    TextView nores;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_list);

        setTitle(R.string.title_activity_attractions);

        listView = (ListView) findViewById(android.R.id.list);
        ul1 = (ImageView) findViewById(R.id.ul1);
        ul2 = (ImageView) findViewById(R.id.ul2);
        ul3 = (ImageView) findViewById(R.id.ul3);

        tab1 = (TextView) findViewById(R.id.tab1);
        tab2 = (TextView) findViewById(R.id.tab2);
        tab3 = (TextView) findViewById(R.id.tab3);

        nores = (TextView) findViewById(R.id.noResult);

        setAll();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView val = (TextView) view.findViewById(R.id.Itemname);
                String title = val.getText().toString();

                Intent intent = new Intent(AttractionList.this, Attraction.class);
                intent.putExtra("name", title);
                startActivity(intent);
            }
        });
    }
    public void setAll(){
        tab1.setTextColor(Color.parseColor("#FFFFFF"));
        RealmResults query = Utils.realm.where(Marker.class).equalTo("city.id", Utils.city_id).findAll();

        if(query.size() == 0){
            noResult();
        }else{
            AttractionAdapter adapter = new AttractionAdapter(this, query, true);
            listView.setAdapter(adapter);
        }

    }
    public void showAll(View v){
        clearUnderlines();
        ul1.setVisibility(View.VISIBLE);
        tab1.setTextColor(Color.parseColor("#FFFFFF"));
        RealmResults query = Utils.realm.where(Marker.class).equalTo("city.id", Utils.city_id).findAll();
        if(query.size() == 0){
            noResult();
        }else{
            AttractionAdapter adapter = new AttractionAdapter(this, query, true);
            listView.setAdapter(adapter);
        }
    }
    public void showMuseum(View v){
        clearUnderlines();
        ul2.setVisibility(View.VISIBLE);
        tab2.setTextColor(Color.parseColor("#FFFFFF"));
        RealmResults query = Utils.realm.where(Marker.class).equalTo("city.id", Utils.city_id).equalTo("type",0).findAll();
        if(query.size() == 0){
            noResult();
        }else{
            AttractionAdapter adapter = new AttractionAdapter(this, query, true);
            listView.setAdapter(adapter);
        }

    }
    public void showMonument(View v){
        clearUnderlines();
        ul3.setVisibility(View.VISIBLE);
        tab3.setTextColor(Color.parseColor("#FFFFFF"));
        RealmResults query = Utils.realm.where(Marker.class).equalTo("city.id", Utils.city_id).equalTo("type",1).findAll();
        if(query.size() == 0){
            noResult();
        }else{
            AttractionAdapter adapter = new AttractionAdapter(this, query, true);
            listView.setAdapter(adapter);
        }
    }
    public void clearUnderlines (){
        tab1.setTextColor(Color.parseColor("#F0BFB3"));
        tab2.setTextColor(Color.parseColor("#F0BFB3"));
        tab3.setTextColor(Color.parseColor("#F0BFB3"));

        ul1.setVisibility(View.INVISIBLE);
        ul2.setVisibility(View.INVISIBLE);
        ul3.setVisibility(View.INVISIBLE);

        listView.setVisibility(View.VISIBLE);
        nores.setVisibility(View.GONE);
    }
    public void noResult(){
        listView.setVisibility(View.GONE);
        nores.setVisibility(View.VISIBLE);
    }
}
