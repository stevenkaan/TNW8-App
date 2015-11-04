package nl.sightguide.sightguide;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "app.db";

    public static final String CITY_TABLE_NAME = "cities";
    public static final String CITY_COLUMN_ID = "_id";
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
    public static final String MARKERS_COLUMN_ID = "_id";
    public static final String MARKERS_COLUMN_CITY_ID = "city_id";
    public static final String MARKERS_COLUMN_TYPE_ID = "type_id";
    public static final String MARKERS_COLUMN_LATITUDE = "latitude";
    public static final String MARKERS_COLUMN_LONGITUDE = "longitude";
    public static final String MARKERS_COLUMN_NAME = "name";
    public static final String MARKERS_COLUMN_INFORMATION = "information";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
        //onUpgrade(db,1,2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+CITY_TABLE_NAME+" ("+CITY_COLUMN_ID+" integer primary key autoincrement, "+CITY_TABLE_NAME+" string, "+CITY_COLUMN_COUNTRY+" string, "+CITY_COLUMN_LATITUDE+" double, "+CITY_COLUMN_LONGITUDE+" double, "+CITY_COLUMN_POPULATION+" integer)");
        db.execSQL("create table "+MARKERS_TABLE_NAME+" ("+MARKERS_COLUMN_ID+" integer primary key autoincrement, "+MARKERS_COLUMN_CITY_ID+" integer, "+MARKERS_COLUMN_TYPE_ID+" integer, "+MARKERS_COLUMN_LATITUDE+" double, "+MARKERS_COLUMN_LONGITUDE+" double, "+MARKERS_COLUMN_NAME+" string, "+MARKERS_COLUMN_INFORMATION+" text)");

        db.execSQL("create table cities_info (_id integer primary key autoincrement, city_id integer,language_id integer,name string, information text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CITY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CITIESINFO_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MARKERS_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertCity(int id, String country, double latitude, double longitude, int population) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("_id", id);
        contentValues.put("country", country);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        contentValues.put("population", population);

        db.insert("cities", null, contentValues);
        return true;
    }
    public boolean checkCity(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from cities where _id="+id+"", null);

        if(res.getCount() > 0){
            return true;
        }
        return false;
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

    public boolean insertMarker(int id, int city_id, int type_id, String name, String information, double latitude,  double longitude) {


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("_id", id);
        contentValues.put("city_id", city_id);
        contentValues.put("type_id", type_id);
        contentValues.put("name", name);
        contentValues.put("information", information);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);


        db.insert("markers", null, contentValues);
        return true;
    }

    public boolean checkMarker(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from markers where _id=" + id + "", null);

        if(res.getCount() > 0){
            return true;
        }
        return false;
    }

    public ArrayList getCity(int id){

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = new String[]{CITY_COLUMN_ID, CITY_COLUMN_COUNTRY, CITY_COLUMN_LATITUDE, CITY_COLUMN_LONGITUDE, CITY_COLUMN_POPULATION};
        String search = CITY_COLUMN_ID + "=?";

        Cursor c = db.query(CITY_TABLE_NAME, columns, search, new String[]{String.format("%d", id)}, null, null, "1");
        if(c.getCount() > 0) {
            c.moveToFirst();
            ArrayList<String> res = new ArrayList<String>();

            int indexId = c.getColumnIndex(CITY_COLUMN_ID);
            int indexCountry = c.getColumnIndex(CITY_COLUMN_COUNTRY);
            int indexLatitude = c.getColumnIndex(CITY_COLUMN_LATITUDE);
            int indexLongitude = c.getColumnIndex(CITY_COLUMN_LONGITUDE);
            int indexPopulation = c.getColumnIndex(CITY_COLUMN_POPULATION);

            String city_id = c.getString(indexId);
            String country = c.getString(indexCountry);
            String latitude = c.getString(indexLatitude);
            String longitude = c.getString(indexLongitude);
            String population = c.getString(indexPopulation);

            res.add(city_id);
            res.add(country);
            res.add(latitude);
            res.add(longitude);
            res.add(population);

            return res;
        }else{
            Log.e("getCity", "Not found");
        }

        return null;
    }

    public Cursor getCityInfo(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from cities_info  where id="+id+"", null);
        return res;
    }

    public String[][] getAttractions(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = new String[]{MARKERS_COLUMN_NAME, MARKERS_COLUMN_ID, MARKERS_COLUMN_INFORMATION};
        Cursor c = db.query(MARKERS_TABLE_NAME, columns, null, null, null, null, null);

        int indexName = c.getColumnIndex(MARKERS_COLUMN_NAME);
        int indexID = c.getColumnIndex(MARKERS_COLUMN_ID);
        int indexInfo = c.getColumnIndex(MARKERS_COLUMN_INFORMATION);

        int resCount = c.getCount();
        String[][] attrList = new String[resCount][3];

        int i = 0;
        for( c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){

            String id =  c.getString(indexID);
            String name =  c.getString(indexName);
            String info = c.getString(indexInfo);

            attrList[i][0] = id;
            attrList[i][1] = name;
            attrList[i][2] = info;

            i++;
        }
        return attrList;
    }
    public ArrayList getAttraction(String attr){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = new String[]{MARKERS_COLUMN_NAME, MARKERS_COLUMN_INFORMATION,
                MARKERS_COLUMN_LATITUDE, MARKERS_COLUMN_LONGITUDE, MARKERS_COLUMN_TYPE_ID};
        String search = MARKERS_COLUMN_NAME + "=?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = attr;
        Cursor c = db.query(MARKERS_TABLE_NAME, columns, search, new String[]{attr}, null, null, "1");
        c.moveToFirst();
        ArrayList<String> res = new ArrayList<String>();

        int indexName = c.getColumnIndex(MARKERS_COLUMN_NAME);
        int indexInfo = c.getColumnIndex(MARKERS_COLUMN_INFORMATION);
        int indexLat = c.getColumnIndex(MARKERS_COLUMN_LATITUDE);
        int indexLong = c.getColumnIndex(MARKERS_COLUMN_LONGITUDE);
        int indexType = c.getColumnIndex(MARKERS_COLUMN_TYPE_ID);


        String name =  c.getString(indexName);
        String information =  c.getString(indexInfo);
        String latitude =  c.getString(indexLat);
        String longitude =  c.getString(indexLong);
        String type =  c.getString(indexType);


        res.add(name);
        res.add(information);
        res.add(latitude);
        res.add(longitude);
        res.add(type);

        return res;
    }


}