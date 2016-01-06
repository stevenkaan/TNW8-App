package nl.sightguide.sightguide.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.adapters.FilterListAdapter;
import nl.sightguide.sightguide.models.Route;
import nl.sightguide.sightguide.models.Type;

public class FilterList extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_list);

        Utils.preferences = getSharedPreferences("SightGuide", 0);
        Utils.editor = Utils.preferences.edit();

        FilterListAdapter adapter = new FilterListAdapter(this, Utils.realm.where(Type.class).findAll(), false);

        ListView listView = (ListView) findViewById(R.id.filter_list);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Switch toggle = (Switch) view.findViewById(R.id.DisplayToggle);
                TextView filterType = (TextView) view.findViewById(R.id.FilterType);
                int type  = Integer.parseInt(filterType.getText().toString());

                Type selected = Utils.realm.where(Type.class).equalTo("type", type).findFirst();
                Utils.realm.beginTransaction();
                if(toggle.isChecked()){
                    toggle.setChecked(false);
                    selected.setDisplay(false);
                }
                else{
                    toggle.setChecked(true);
                    selected.setDisplay(true);
                }
                Utils.realm.commitTransaction();

            }
        });



    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Home.class));
    }
}
