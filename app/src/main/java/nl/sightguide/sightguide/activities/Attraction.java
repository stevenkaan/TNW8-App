package nl.sightguide.sightguide.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;

import nl.sightguide.sightguide.helpers.DatabaseHelper;
import nl.sightguide.sightguide.R;

public class Attraction extends AppCompatActivity {

    private String attractionName;
    private int cityID;
    private int langID;
    private DatabaseHelper mydb ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction);

        mydb = new DatabaseHelper(this);

        Intent intent = getIntent();

        attractionName = intent.getStringExtra("name");
        setTitle(attractionName);

        TextView attrNameText = new TextView(this);
        attrNameText = (TextView)findViewById(R.id.attrName);
        TextView attrInfoText = new TextView(this);
        attrInfoText = (TextView)findViewById(R.id.attrInfo);


        ArrayList values = mydb.getAttraction(attractionName);
        Object attrName = values.get(0);
        Object attrInfo = values.get(1);

        attrNameText.setText(attrName.toString());
        attrInfoText.setText(attrInfo.toString());
    }

}
