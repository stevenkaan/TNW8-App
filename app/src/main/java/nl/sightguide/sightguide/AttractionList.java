package nl.sightguide.sightguide;


import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class AttractionList extends Activity {

    private String attractionName;
    private int typeID;
    private int langID;
    private DatabaseHelper mydb ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_list);
        mydb = new DatabaseHelper(this);

        Intent intent = getIntent();

        attractionName = intent.getStringExtra("attractionName");
        typeID = intent.getIntExtra("typeID", 0);

        setTitle(attractionName);

        ListView listContent = (ListView)findViewById(R.id.contentlist);

        Cursor cursor = mydb.getAttractions();

        if(cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    String[] from = new String[]{mydb.MARKERS_COLUMN_NAME};
                    int[] to = new int[]{R.id.text};
                    SimpleCursorAdapter cursorAdapter =
                            new SimpleCursorAdapter(this, R.layout.activity_select_markers, cursor, from, to);

                    listContent.setAdapter(cursorAdapter);


                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (mydb != null) {
                mydb.close();
            }
        }else{
            Log.d("db", "Database is leeg");
        }


    }
}
