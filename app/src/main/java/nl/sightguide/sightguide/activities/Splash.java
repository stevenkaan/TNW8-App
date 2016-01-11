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

        setContentView(R.layout.activity_splash);

        Utils.preferences = getSharedPreferences("SightGuide", 0);
        Utils.editor = Utils.preferences.edit();

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();

        if(Utils.preferences.getBoolean("firstRun", true)){
            Utils.editor.putBoolean("firstRun", false);
            Utils.editor.putInt("language", 0);
            Utils.editor.putBoolean("autoPlay", false);
            Utils.editor.putBoolean("proximitySensor", false);
            Utils.editor.putBoolean("proximitySensorActive", false);
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

        this.newType(0, "Museum", "Museum", "Museo", "type_0", true);
        this.newType(1, "Monument", "Monument", "Monumento", "type_1", true);
        this.newType(2, "Markt", "Market", "Mercado", "type_2", true);
        this.newType(3, "Park", "Park", "Parqué", "type_3", false);
        this.newType(4, "Dierentuin", "Zoo", "Jardín zoológico", "type_4", false);
        this.newType(5, "Uitzichtpunt", "Sightseeing point", "Mirador", "type_5", false);
        this.newType(6, "Winkelstraat", "Shopping district", "Distrito comercial", "type_6", false);
        this.newType(7, "Restaurant", "Restaurant", "Restaurante", "type_7", false);
        this.newType(8, "Night life", "Night life", "Vida nocturna", "type_8", false);
        this.newType(9, "Casino", "Casino", "Casino", "type_9", false);
        this.newType(10, "Rondvaart", "Boat trip", "Vuelta en barco", "type_10", false);
        this.newType(11, "Bus tour", "Bus tour", "Viaje en autobús", "type_11", false);
        this.newType(12, "Ziekenhuiz", "Hospital", "Hospital", "type_12", false);
        this.newType(13, "Apotheek", "Pharmacy", "Farmacia", "type_13", false);
        this.newType(14, "Openbare toilet", "Public restroom", "Baño publico", "type_14", false);
        this.newType(15, "Pinautomaat", "ATM machine", "Cajero automático", "type_15", false);
        this.newType(16, "Parkeerplaats", "Parking lot", "Plaza de aparcamiento", "type_16", false);
        this.newType(17, "Toeristeninformatie", "Tourist information", "Información turística", "type_17", true);
        this.newType(18, "Luchthaven", "Airport", "Aeropuerto", "type_18", false);
        this.newType(19, "Haven", "Harbour", "Puerto", "type_19", false);
        this.newType(20, "Openbaar vervoer", "Public transportation", "Transporte público", "type_20", false);
        this.newType(21, "Trein station", "Train station", "Estacion de trenes", "type_21", false);
        this.newType(22, "Metro station", "Metro station", "Parada de metro", "type_22", false);
        this.newType(23, "Tramhalte", "Tram stop", "Parada de tranvía", "type_23", false);
        this.newType(24, "Bushalte", "Bus stop", "Parada de autobús", "type_24", false);
        this.newType(25, "Veerboot", "Ferry", "Ferry", "type_25", false);

        Utils.realm.commitTransaction();
    }

}