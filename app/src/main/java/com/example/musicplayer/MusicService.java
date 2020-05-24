package com.example.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import static com.example.musicplayer.App.mediaPlayer;

public class MusicService extends Service {
    private TextView songLabel;
    Uri u;

    public MusicService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onCreate();


        if(mediaPlayer!=null){//initialize the media player
            mediaPlayer.pause();
            mediaPlayer.release();
        }
        u=Uri.parse(intent.getStringExtra("uri"));

        mediaPlayer= MediaPlayer.create(getApplicationContext(),u);//parameter,context,uri

        mediaPlayer.start();

        sendBroadcast(new Intent("START_SEEKBAR"));//sendBroadst to BroadcastRecevier of MusicPlayerActivty.java

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
