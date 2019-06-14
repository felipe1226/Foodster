package com.app.foodster;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import java.util.Calendar;

public class NotificationHandler extends ContextWrapper{

    private NotificationManager manager;
    GlobalState gs = (GlobalState)getApplicationContext();

    public static final String CHANNEL_HIGH_ID = "1";
    private final String CHANNER_HIGH_NAME = "General";

    public static final String CHANNEL_LOW_ID = "2";
    private final String CHANNER_LOW_NAME = "Silencio";

    public NotificationHandler(Context context) {
        super(context);
        createChannels();
    }

    public NotificationManager getManager() {
        if(manager == null){
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    private void createChannels(){
        if(Build.VERSION.SDK_INT >= 26){
            NotificationChannel highChannel = new NotificationChannel(
                    CHANNEL_HIGH_ID, CHANNER_HIGH_NAME, NotificationManager.IMPORTANCE_HIGH);
            highChannel.enableLights(true);
            highChannel.setLightColor(Color.WHITE);
            highChannel.setShowBadge(true);
            highChannel.enableVibration(true);
            highChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationChannel lowChannel = new NotificationChannel(
                    CHANNEL_LOW_ID, CHANNER_LOW_NAME, NotificationManager.IMPORTANCE_LOW);

            getManager().createNotificationChannel(highChannel);
            getManager().createNotificationChannel(lowChannel);
        }
    }

    public Notification.Builder createNotification(String titulo, String mensaje, boolean isHighImportant){

        if(Build.VERSION.SDK_INT >= 26){
            if (isHighImportant){
                return this.createNotificationWithChannel(titulo, mensaje, CHANNEL_HIGH_ID);
            }
            return this.createNotificationWithChannel(titulo, mensaje, CHANNEL_LOW_ID);

        }
        return this.createNotificationWithoutChannel(titulo, mensaje);
    }

    private Notification.Builder createNotificationWithChannel(String titulo, String mensaje, String idChannel){


        if(Build.VERSION.SDK_INT >= 26){

            Principal principal = gs.getPrincipal();
            Intent intent = new Intent(this, principal.getClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) Calendar.getInstance().getTimeInMillis(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

            return new Notification.Builder(getApplicationContext(), idChannel)
                    .setContentTitle(titulo)
                    .setContentText(mensaje)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
        }
        return null;
    }

    private Notification.Builder createNotificationWithoutChannel(String titulo, String mensaje){
        return new Notification.Builder(getApplicationContext())
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true);
    }
}
