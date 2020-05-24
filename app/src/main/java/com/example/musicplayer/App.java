package com.example.musicplayer;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;

public class App extends Application {//it will be activated first
    public static MediaPlayer mediaPlayer;
    public static final String CHANNEL_1_ID="channel1";//channelID,it will be used in creating notifiaction channel in NotificationService.java.
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    public void  createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){//if this api version is greater than api 26
            NotificationChannel channel1=new NotificationChannel(//parameter:channelId,channelName,importance
                    CHANNEL_1_ID,
                    "channel1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setSound(null,null);
            channel1.setDescription("This is channel1");

            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);//use NotifiactionManget create a channel
        }
    }


}
