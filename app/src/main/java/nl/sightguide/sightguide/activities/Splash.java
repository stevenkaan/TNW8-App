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
import nl.sightguide.sightguide.models.City;
import nl.sightguide.sightguide.models.Marker;
import nl.sightguide.sightguide.services.LocationService;
import nl.sightguide.sightguide.models.Type;

public class Splash extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;
    private AlertDialog.Builder builder;
    private static SharedPreferences.Editor editor;

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
            Utils.editor.putBoolean("autoPlay", false);
            Utils.editor.putBoolean("proximitySensor", false);
            Utils.editor.commit();

            Realm.deleteRealm(realmConfiguration);


        }
        Realm.setDefaultConfiguration(realmConfiguration);


        Utils.realm = Realm.getDefaultInstance();

        this.addTypes();

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
        Utils.realm.beginTransaction();
        this.newType(0, "Apotheken", "Pharmacies", "Farmacias", "icon_pharmacy", false);
        this.newType(1, "Bus tours", "Bus tours", "Viajes en autobús", "icon_bushalte", false);
        this.newType(2, "Bushaltes", "Bus stops", "Paradas de autobús", "icon_bushalte", false);
        this.newType(3, "Casino's", "casinos", "casinos", "icon_casino", false);
        this.newType(4, "Dierentuinen", "Zoos", "Jardínes zoológico", "icon_dierentuin", false);
        this.newType(5, "Havens", "Harbours", "Puertos", "icon_ferry", false);
        this.newType(6, "Luchthavens", "Airports", "Aeropuertos", "icon_airport", false);
        this.newType(7, "Markten", "Markets", "Mercados", "icon_shopping", false);
        this.newType(8, "Metro stations", "Metro stations", "Estaciones de metro", "icon_metro", false);
        this.newType(9, "Monumenten", "Monuments", "Monumentos", "icon_monuments", true);
        this.newType(10, "Musea", "Museums", "Museos", "icon_museums", true);
        this.newType(11, "Night life", "Night life", "Vida nocturna", "icon_nightlife", false);
        this.newType(12, "Openbaar vervoer", "Public transportation", "Transporte público", "icon_bushalte", false);
        this.newType(13, "Openbare toiletten", "Public restrooms", "Baños publicos", "icon_toilets", false);
        this.newType(14, "Parken", "Parks", "Parqués", "icon_park", false);
        this.newType(15, "Parkeerplaatsen", "Parking lots", "Plazas de aparcamiento", "icon_parking", false);
        this.newType(16, "Pinautomaten", "ATM machines", "Cajeros automáticos", "icon_atm", false);
        this.newType(17, "Restaurants", "Pharmacies", "Farmacias", "icon_restaurants", false);
        this.newType(18, "Rondvaarten", "Boat trips", "Vueltas en barco", "icon_boattour", false);
        this.newType(19, "Toeristeninformatie", "Tourist information", "Información turística", "icon_information", false);
        this.newType(20, "Tramhaltes", "Tram stops", "Paradas de tranvía", "icon_tram", false);
        this.newType(21, "Trein stations", "Train stations", "Estaciones de trenes", "icon_trainstation", false);
        this.newType(22, "Uitzichtpunten", "Sightseeing points", "Miradores", "icon_viewpoint", false);
        this.newType(23, "Veerboten", "Ferries", "Ferries", "icon_ferry", false);
        this.newType(24, "Winkelstraten", "Shopping districts", "Tiendas", "icon_shopping", false);
        this.newType(25, "Ziekenhuizen", "Hospitals", "Hospitales", "icon_ehbo", false);
        Utils.realm.commitTransaction();
    }

}