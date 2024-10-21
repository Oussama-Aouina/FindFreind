package com.oussamaaouina.findfreind;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MySmsReceiver extends BroadcastReceiver {

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String msgBody,phoneNumber;
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle= intent.getExtras();
            if (bundle != null){
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i=0;i<pdus.length;i++){
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                if(messages.length>-1){
                    msgBody = messages[0].getMessageBody();
                    phoneNumber = messages[0].getOriginatingAddress();
                    if (msgBody.contains("FINDFREINDS: Envoyer moi votre position")){
                        // lancer un service pour recuperer la position et repondre
                        Intent serviceIntent = new Intent(context, MyLocationService.class);
                        serviceIntent.putExtra("phoneNumber", phoneNumber);
                        context.startService(serviceIntent);
                    }
                    if(msgBody.contains("FINDFREINDS: ma position est: ")){
                        String[] t=msgBody.split("#");
                        String longitude=t[1];
                        String latitude=t[2];

                        //lancement de notification
                        NotificationCompat.Builder mynotif= new NotificationCompat.Builder(context,"canal_freinds");
                        mynotif.setContentTitle("position");
                        mynotif.setContentText("Appuyez sur la notification pour voir votre position");
                        mynotif.setSmallIcon(R.drawable.ic_launcher_background);
                        mynotif.setAutoCancel(true);
                        mynotif.setPriority(NotificationCompat.PRIORITY_HIGH);
                        NotificationManagerCompat managerCompat= NotificationManagerCompat.from(context);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            NotificationChannel canal= new NotificationChannel("canal_freinds","canal_freinds", NotificationManager.IMPORTANCE_HIGH);
                        }
                        managerCompat.notify(1,mynotif.build());
                    }

                }
            }
        }
    }
}
