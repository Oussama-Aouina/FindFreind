package com.oussamaaouina.findfreind;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

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
                        Log.d("SMSReceiver", "Processing location message...");

                        // lancer un service pour recuperer la position et repondre
                        Intent serviceIntent = new Intent(context, MyLocationService.class);
                        serviceIntent.putExtra("phoneNumber", phoneNumber);
                        context.startService(serviceIntent);
                    }
                    if (msgBody.contains("FINDFREINDS: Ma position est:")) {

                        String[] t = msgBody.split("#");
                        String longitude = t[1];
                        String latitude = t[2];

                        // Create notification builder
                        NotificationCompat.Builder mynotif = new NotificationCompat.Builder(context, "canal_freinds")
                                .setContentTitle("Position")
                                .setContentText("Appuyez sur la notification pour voir votre position")
                                .setSmallIcon(android.R.drawable.ic_dialog_map)
                                .setAutoCancel(true)
                                .setPriority(NotificationCompat.PRIORITY_HIGH);

                        // Intent for MapsActivity
                        Intent i = new Intent(context, MapsActivity.class);
                        i.putExtra("longitude", longitude);
                        i.putExtra("latitude", latitude);

                        // Pending intent to launch MapsActivity
                        PendingIntent pi = PendingIntent.getActivity(context, 1, i, PendingIntent.FLAG_MUTABLE);
                        mynotif.setContentIntent(pi);

                        // NotificationManager for displaying the notification
                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);

                        // For Android O and above, create a notification channel
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            NotificationChannel canal = new NotificationChannel(
                                    "canal_freinds",
                                    "Find Friends Notifications",
                                    NotificationManager.IMPORTANCE_HIGH
                            );
                            canal.setDescription("Channel for FindFriends notifications");

                            // Register the channel with the system
                            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                            notificationManager.createNotificationChannel(canal);
                        }

                        // Display the notification
                        managerCompat.notify(1, mynotif.build());
                    }

                }
            }
        }
    }
}
