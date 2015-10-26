package nl.sightguide.sightguide;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.DatabaseUtils;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "app.db";

    public static final String CITY_TABLE_NAME = "cities";
    public static final String CITY_COLUMN_ID = "id";
    public static final String CITY_COLUMN_COUNTRY = "country";
    public static final String CITY_COLUMN_LATITUDE = "latitude";
    public static final String CITY_COLUMN_LONGITUDE = "longitude";
    public static final String CITY_COLUMN_POPULATION = "population";

    public static final String CITIESINFO_TABLE_NAME = "cities_info";
    public static final String CITIESINFO_COLUMN_ID = "cities_info_id";
    public static final String CITIESINFO_COLUMN_CITY_ID = "city_id";
    public static final String CITIESINFO_COLUMN_LANGUAGE_ID = "language_id";
    public static final String CITIESINFO_COLUMN_NAME = "name";
    public static final String CITIESINFO_COLUMN_INFORMATION = "information";

    public static final String MARKERS_TABLE_NAME = "markers";
    public static final String MARKERS_COLUMN_ID = "markers_id";
    public static final String MARKERS_COLUMN_CITY_ID = "marker_city_id";
    public static final String MARKERS_COLUMN_TYPE_ID = "type_id";
    public static final String MARKERS_COLUMN_LATITUDE = "marker_latitude";
    public static final String MARKERS_COLUMN_LONGITUDE = "marker_longitude";
    public static final String MARKERS_COLUMN_NAME = "marker_name";
    public static final String MARKERS_COLUMN_INFORMATION = "marker_information";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table cities (id integer primary key autoincrement, country string,latitude double,longitude double, population integer)");
        db.execSQL("create table cities_info (cities_info_id integer primary key autoincrement, city_id integer,language_id integer,name string, information text)");
        db.execSQL("create table markers (markers_id integer primary key autoincrement, marker_city_id integer,type_id integer, marker_latitude double,marker_longitude double, marker_name string, marker_information text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CITY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CITIESINFO_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MARKERS_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertCity(String country, double latitude, double longitude, int population) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("country", country);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        contentValues.put("population", population);

        db.insert("cities", null, contentValues);
        return true;
    }
    public boolean insertCityInfo(int city_id, int language_id, String name, String information) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("city_id", city_id);
        contentValues.put("language_id", language_id);
        contentValues.put("name", name);
        contentValues.put("information", information);


        db.insert("cities_info", null, contentValues);
        return true;
    }
    public boolean insertMarker(String name, String information) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put("marker_name", name);
        contentValues.put("marker_information", information);


        db.insert("markers", null, contentValues);
        return true;
    }
//    public boolean insertMarker(int city_id, int type_id, double latitude, double longitude, String name, String information) {
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//
//        contentValues.put("city_id", city_id);
//        contentValues.put("type_id", type_id);
//        contentValues.put("latitude", latitude);
//        contentValues.put("longitude", longitude);
//        contentValues.put("name", name);
//        contentValues.put("information", information);
//
//
//        db.insert("markers", null, contentValues);
//        return true;
//    }

    public Cursor getCity(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from cities where id="+id+"", null);
        return res;
    }

    public Cursor getCityInfo(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from cities_info  where id="+id+"", null);
        return res;
    }
    public Cursor getAttractions(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from markers", null);
        return res;
    }


}
