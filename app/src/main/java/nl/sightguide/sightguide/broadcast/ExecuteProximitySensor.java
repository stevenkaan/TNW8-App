package nl.sightguide.sightguide.broadcast;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.GregorianCalendar;

public class ExecuteProximitySensor extends BroadcastReceiver  {

    @Override
    public void onReceive(Context context, Intent intent) {

        checkDistance();
    }
    public void checkDistance(){

        Log.e("alarm","5 secs have passed brah");

    }

}
