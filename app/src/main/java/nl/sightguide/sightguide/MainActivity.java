package nl.sightguide.sightguide;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private String[] monthsArray = { "Amsterdam", "Groningen", "Leeuwarden", "Steenwijk", "Hoogeveen", "Emmen", "Zwolle", "Utrecht", "Maastricht", "Alkmaar", "Arnhem" };
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        EditText inputSearch = (EditText) findViewById(R.id.itemSearch);
        ListView monthsListView = (ListView) findViewById(R.id.view_cities);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, monthsArray);
        monthsListView.setAdapter(adapter);

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                MainActivity.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
    }
}