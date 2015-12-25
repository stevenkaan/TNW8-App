package nl.sightguide.sightguide.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.Utils;
import nl.sightguide.sightguide.services.LocationService;
import nl.sightguide.sightguide.models.Type;

public class Splash extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;
    private AlertDialog.Builder builder;
    private static SharedPreferences.Editor editor;
    private boolean firstRun = false;

    @Override
    public void onCreate(Bundle bundle) {
        builder = new AlertDialog.Builder(Splash.this);

        super.onCreate(bundle);

        startService(new Intent(this, LocationService.class));

        setContentView(R.layout.activity_splash);

        Utils.preferences = getSharedPreferences("SightGuide", 0);
        Utils.editor = Utils.preferences.edit();

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();

        if(Utils.preferences.getBoolean("firstRun", true)){
            Utils.editor.putBoolean("firstRun", false);
            Utils.editor.putInt("language", 0);
            Utils.editor.putBoolean("autoPlay", false);
            Utils.editor.putBoolean("proximitySensor", false);
            Utils.editor.commit();

            Realm.deleteRealm(realmConfiguration);
            firstRun = true;

        }
        Realm.setDefaultConfiguration(realmConfiguration);


        Utils.realm = Realm.getDefaultInstance();

        if(firstRun) {
            this.addTypes();
            firstRun = false;
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int lastCity = Utils.preferences.getInt("lastCity", 0);
                Intent intent;
                if (lastCity > 0) {
                    intent = new Intent(Splash.this, Home.class);
                    Utils.city_id = Utils.preferences.getInt("lastCity", 0);

                    Splash.this.startActivity(intent);
                    Splash.this.finish();
                } else {
                    if (Utils.checkNetwork(Splash.this)) {
                        Splash.this.startActivity(new Intent(Splash.this, Launcher.class));
                        Splash.this.finish();

                    } else {
                        builder.setMessage("Unable to connect to the internet")
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (Utils.checkNetwork(Splash.this)) {
                                            Splash.this.startActivity(new Intent(Splash.this, Launcher.class));
                                        } else {
                                            builder.show();
                                        }
                                    }
                                }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Splash.this.finish();
                                System.exit(0);
                            }
                        });
                        builder.create().show();
                    }
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
    public void newType(int type_id, String name_nl, String name_en, String name_es, String image, Boolean display) {

        try {



            Type markerType = Utils.realm.where(Type.class).equalTo("type", type_id).findFirst();
            if(markerType == null){
                markerType = new Type();
                markerType.setType(type_id);
                markerType.setName_nl(name_nl);
                markerType.setName_en(name_en);
                markerType.setName_es(name_es);
                markerType.setImage(image);
                markerType.setDisplay(display);

                Utils.realm.copyToRealm(markerType);
            }


            Log.e("succes", "succesfully added new type");

        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }
    public void addTypes () {
        /// Add marker types to db >> id,nl,en,es,img,display
        Utils.realm.beginTransaction();

        this.newType(0, "Musea", "Museums", "Museos", "type_0", true);
        this.newType(1, "Monumenten", "Monuments", "Monumentos", "type_1", true);
        this.newType(2, "Markten", "Markets", "Mercados", "type_2", false);
        this.newType(3, "Parken", "Parks", "Parqués", "type_3", false);
        this.newType(4, "Dierentuinen", "Zoos", "Jardínes zoológico", "type_4", false);
        this.newType(5, "Uitzichtpunten", "Sightseeing points", "Miradores", "type_5", false);
        this.newType(6, "Winkelstraten", "Shopping districts", "Tiendas", "type_6", false);
        this.newType(7, "Restaurants", "Restaurants", "Restaurantes", "type_7", false);
        this.newType(8, "Night life", "Night life", "Vida nocturna", "type_8", false);
        this.newType(9, "Casino's", "Casinos", "Casinos", "type_9", false);
        this.newType(10, "Rondvaarten", "Boat trips", "Vueltas en barco", "type_10", false);
        this.newType(11, "Bus tours", "Bus tours", "Viajes en autobús", "type_11", false);
        this.newType(12, "Ziekenhuizen", "Hospitals", "Hospitales", "type_12", false);
        this.newType(13, "Apotheken", "Pharmacies", "Farmacias", "type_13", false);
        this.newType(14, "Openbare toiletten", "Public restrooms", "Baños publicos", "type_14", false);
        this.newType(15, "Pinautomaten", "ATM machines", "Cajeros automáticos", "type_15", false);
        this.newType(16, "Parkeerplaatsen", "Parking lots", "Plazas de aparcamiento", "type_16", false);
        this.newType(17, "Toeristeninformatie", "Tourist information", "Información turística", "type_17", true);
        this.newType(18, "Luchthavens", "Airports", "Aeropuertos", "type_18", false);
        this.newType(19, "Havens", "Harbours", "Puertos", "type_19", false);
        this.newType(20, "Openbaar vervoer", "Public transportation", "Transporte público", "type_20", false);
        this.newType(21, "Trein stations", "Train stations", "Estaciones de trenes", "type_21", false);
        this.newType(22, "Metro stations", "Metro stations", "Estaciones de metro", "type_22", false);
        this.newType(23, "Tramhaltes", "Tram stops", "Paradas de tranvía", "type_23", false);
        this.newType(24, "Bushaltes", "Bus stops", "Paradas de autobús", "type_24", false);
        this.newType(25, "Veerboten", "Ferries", "Ferries", "type_25", false);

        Utils.realm.commitTransaction();
    }

}