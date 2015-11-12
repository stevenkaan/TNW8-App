package nl.sightguide.sightguide.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;

import nl.sightguide.sightguide.R;
import nl.sightguide.sightguide.Utils;

public class Splash extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;
    private AlertDialog.Builder builder;

    @Override
    public void onCreate(Bundle icicle) {
        builder = new AlertDialog.Builder(Splash.this);

        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                SharedPreferences settings = getSharedPreferences("SightGuide", 0);
                int lastCity = settings.getInt("lastCity", 0);
                Intent intent;

                if(lastCity > 0){
                    String lastLang = settings.getString("lastLang", null);

                    intent = new Intent(Splash.this, Home.class);
                    intent.putExtra("cityID", lastCity);
                    intent.putExtra("langID", lastLang);

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