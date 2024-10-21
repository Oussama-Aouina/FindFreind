package com.oussamaaouina.findfreind;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.telephony.SmsManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MyLocationService extends Service {
    public MyLocationService() {
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String phone=intent.getStringExtra("PHONE");
        //recuperer la position
        FusedLocationProviderClient mClient= LocationServices.getFusedLocationProviderClient(this);
        mClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    double longitude=location.getLongitude();
                    double latitude=location.getLatitude();
                    SmsManager manager=SmsManager.getDefault();
                    manager.sendTextMessage(phone,
                            null,
                            "FINDFRIENDS: Ma position est: #"+longitude+"#"+latitude,
                            null,
                            null);
                }
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}