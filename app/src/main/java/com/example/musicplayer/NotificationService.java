package com.example.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.example.musicplayer.App.CHANNEL_1_ID;
import static com.example.musicplayer.App.mediaPlayer;

public class NotificationService extends Service {
    private static NotificationManagerCompat notificationManager;
    private static Notification notification_play;
    private static Notification notification_pause;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        notificationManager=NotificationManagerCompat.from(this);

        Intent serviceIntent=new Intent(this,MusicPlayerActivity.class);
        PendingIntent contentIntent=PendingIntent.getActivity(this,0,serviceIntent,0);

        Intent Intent_Next=new Intent("NEXT_SONG");//satisfy the intent filter of broadcastReceiver to do the particular action
        PendingIntent broadcastIntent_Next=PendingIntent.getBroadcast(this,0,Intent_Next,0);

        Intent Intent_Previous=new Intent("PREVIOUS_SONG");//satisfy the intent filter of broadcastReceiver to do the particular action
        PendingIntent broadcastIntent_Previous=PendingIntent.getBroadcast(this,0,Intent_Previous,0);

        Intent Intent_Pause=new Intent("PAUSE_SONG");//satisfy the intent filter of broadcastReceiver to do the particular action
        PendingIntent broadcastIntent_PAUSE=PendingIntent.getBroadcast(this,0,Intent_Pause,0);

        Bitmap artwork= BitmapFactory.decodeResource(getResources(),R.drawable.music_icon);//R.drawable.xx convert ti Bitmap

        notification_play = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                // Notification notification=new NotificationCompat.Builder(this,"CHANNEL_1_ID")
                .setContentTitle("Music Player")
                .setContentText(intent.getStringExtra("songName").toString())
                .setCategory(Notification.EXTRA_MEDIA_SESSION)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(artwork)
                .setContentIntent(contentIntent)
                .addAction(R.drawable.ic_skip_previous,"previous",broadcastIntent_Previous)
                .addAction(R.drawable.ic_pause,"pause",broadcastIntent_PAUSE)
                .addAction(R.drawable.ic_next,"next",broadcastIntent_Next)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0,1,2))
                .build();

        notification_pause = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                // Notification notification=new NotificationCompat.Builder(this,"CHANNEL_1_ID")
                .setContentTitle("Music Player")
                .setContentText(intent.getStringExtra("songName").toString())
                .setCategory(Notification.EXTRA_MEDIA_SESSION)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(artwork)
                .setContentIntent(contentIntent)
                .addAction(R.drawable.ic_skip_previous,"previous",broadcastIntent_Previous)
                .addAction(R.drawable.ic_play,"pause",broadcastIntent_PAUSE)
                .addAction(R.drawable.ic_next,"next",broadcastIntent_Next)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0,1,2))
                .build();
//set two notification layout for change
        if(mediaPlayer.isPlaying())
        startForeground(1,notification_play);
        else
        startForeground(1,notification_pause);

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(1);
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
