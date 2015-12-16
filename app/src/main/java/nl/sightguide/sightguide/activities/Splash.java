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

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
            int lastCity = Utils.preferences.getInt("lastCity", 0);
            Intent intent;
            if(lastCity > 0){
                intent = new Intent(Splash.this, Home.class);
                Utils.city_id = Utils.preferences.getInt("lastCity", 0);

                Splash.this.startActivity(intent);
                Splash.this.finish();
            }else{
                if(Utils.checkNetwork(Splash.this)){
                    Splash.this.startActivity(new Intent(Splash.this, Launcher.class));
                    Splash.this.finish();

                }else{
                    builder.setMessage("Unable to connect to the internet")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        if(Utils.checkNetwork(Splash.this)){
                            Splash.this.startActivity(new Intent(Splash.this, Launcher.class));
                        }else{
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
}