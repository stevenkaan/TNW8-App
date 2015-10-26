package nl.sightguide.sightguide;

import android.content.Intent;
import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class Attractions extends ListActivity {

        String[] attraction_type = {"All", "Museums", "Parks",
                "Restaurants", "Shops", "Restrooms"
        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setListAdapter(new ArrayAdapter<String>(this,
                    R.layout.activity_attractions, R.id.attraction_type, attraction_type));
        }

        @Override
        protected void onListItemClick(ListView l, View v, int position, long id) {
            // TODO Auto-generated method stub
//            super.onListItemClick(l, v, position, id);
            String selection = l.getItemAtPosition(position).toString();
            Intent intent = new Intent(this, AttractionList.class);
            intent.putExtra("typeID", position);
            intent.putExtra("attractionName", selection);

            startActivity(intent);
        }

    }

