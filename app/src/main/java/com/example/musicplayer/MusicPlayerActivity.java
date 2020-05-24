package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import static com.example.musicplayer.App.mediaPlayer;

public class MusicPlayerActivity extends AppCompatActivity {
    //these variable will alive until the App is destroied.
    private Button pause;
    private Button previous;
    private Button next;
    private TextView songLabel;
    private SeekBar seekBar;
    private BroadcastReceiver seekBarReceiver;//bridge between Service and Activity
    private final int updateButton=0;//use to set message.what
    private final int updateLabel=1;//use to set message.what
    ArrayList<File>mySongs;//store data which sent from MainActivity.java
    String[] songList;//store data which sent from MainActivity.java
    Uri u;
    Thread updateSeekBar;//user can drag the thumb to set the current progress of music
    int position;//use it to get the correct data from data structures which transfer from MainActivity.java
    int totalDuration;//it will be used in thread

    public void nextSong(){//be called in thread and onCLickListener
        mediaPlayer.stop();
        mediaPlayer.release();//detele the mediaPlayer
        position=(position+1)%songList.length;//the current position of ArrayList<File>
        Message msg = handler.obtainMessage();//UI can only changed in main thread so we use msg handle to change UI in other thread
        msg.what = updateButton;//set content of message so handle.handleMessage can determine which message has been received and do the particular action
        handler.sendMessage(msg);//send message to handle.handleMessage
        u=Uri.parse(mySongs.get(position).toString());//change (String type)path to (uri type)path
        msg = handler.obtainMessage();//message cannot be reused,so we instantize a new one
        msg.what = updateLabel;//set content of message so handle.handleMessage can determine which message has been received and do the particular action
        handler.sendMessage(msg);//send message to handle.handleMessage
        mediaPlayer=MediaPlayer.create(getApplicationContext(),u);//parameter:context,uri,create a new media player
        mediaPlayer.start();
        totalDuration=mediaPlayer.getDuration();//update totalDuration of the thread
        seekBar.setMax(mediaPlayer.getDuration());//the maximum of the seekbar
        startService(new Intent(this, NotificationService.class).putExtra("songName",songList[position]));//update the notification with songName,call onStartcommand() in MusicService
    }

    public void previousSong(){//be called in onClickListener
        mediaPlayer.stop();
        mediaPlayer.release();//delete the musicPlayer

        if(position>0){
            position-=1;
        }
        else{
            position=mySongs.size()-1;
        }//get the circular songList

        pause.setBackgroundResource(R.drawable.ic_pause);//background of pause button become pause
        u=Uri.parse(mySongs.get((position)%mySongs.size()).toString());//String path to uri
        songLabel.setText(songList[position]);//update songlabel UI
        mediaPlayer=MediaPlayer.create(getApplicationContext(),u);//parameter,context,uri
        mediaPlayer.start();
        totalDuration=mediaPlayer.getDuration();//update totalDuration of thread
        seekBar.setMax(mediaPlayer.getDuration());//the maximum of the seekbar
        startService(new Intent(this, NotificationService.class).putExtra("songName",songList[position]));//update the notification with songName,call onStartcommand() in MusicService

    }

    public void pauseSong(){
        startForegroundService(new Intent(this, NotificationService.class).putExtra("songName",songList[position]));//change the icon of notification
        if(mediaPlayer.isPlaying()){//if music player is playing,we need to pause it and change the icon
            pause.setBackgroundResource(R.drawable.ic_play);
            mediaPlayer.pause();
        }
        else{
            pause.setBackgroundResource(R.drawable.ic_pause);//if music player is stopped,we need to change icon and start it again
            mediaPlayer.start();
        }

    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){//instantize the interface,be called in handle.sendMessage
            super.handleMessage(msg);
            switch (msg.what){
                case updateButton:
                    pause.setBackgroundResource(R.drawable.ic_pause);//set button to play state
                    break;
                case updateLabel:
                    songLabel.setText(songList[position]);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updateSeekBar=new Thread(){
            public void run(){
                int currentPosition=0;

                while(true){//ensure the thread keep running
                    totalDuration=mediaPlayer.getDuration();
                    while(currentPosition<totalDuration-150){//when this for loop is ended,it will goto while(true)
                        try{
                            sleep(750);
                         // Log.d("currentPosition",Integer.toString(currentPosition));
                         // Log.d("totalDuration",Integer.toString(totalDuration));

                            currentPosition=mediaPlayer.getCurrentPosition();
                            seekBar.setProgress(currentPosition);//set position of thumb of the seekBar
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    //if i clicked Button next and loop(currentPosition<totalDuration-150) still not be ended,line 138 will not be executed which means totalDuration will not be updated so i need update totalDuration in nextSong(),previousSong()
                    currentPosition=0;
                    totalDuration=mediaPlayer.getDuration();
                    nextSong();
                }

            }
        };

        setContentView(R.layout.activity_music_player);

        pause=(Button)findViewById(R.id.pause);
        previous=(Button)findViewById(R.id.previous);
        next=(Button)findViewById(R.id.next);
        songLabel=(TextView)findViewById(R.id.songLabel);
        seekBar=(SeekBar)findViewById(R.id.seekbar);

        getSupportActionBar().setTitle("Now Playing");//set title on the action bar which in the top of the screen
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);//this two statment make left right corner of back button,the back button controled by onOptionsItemSelected() function


        Intent i=getIntent();
        Bundle bundle=((Intent) i).getExtras();//get the data from MainActivity.java

        mySongs=(ArrayList)i.getParcelableArrayListExtra("songs");//get ArrayList<file>

        songList=i.getStringArrayExtra("songList");//get String[]

        songLabel.setText(((Intent) i).getStringExtra("songName"));//set the label of music player
        songLabel.setSelected(true);//showed that the item has been selected

        position=((Intent) i).getIntExtra("position",0);//position start from 0

        u= Uri.parse(mySongs.get(position).toString());//String of path convert to Uri

        startService(new Intent(this,MusicService.class).putExtra("uri",u.toString()));//start service and transfer the song path to MusicService.java
        startForegroundService(new Intent(this, NotificationService.class).putExtra("songName",songList[position]));//start service and transfer the song name to NotificationService.java

        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);//set the coloe of drawable line
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC);//set the token on drawable line

     /*   seekBar.setMax(mediaPlayer.getDuration());//the maximum of the seekbar
        updateSeekBar.start();//start thread after mediaPlayer is not null*///these two lines of code will lead to error,becuase mediaPlayer is noy loaded in from MusicSerivce

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {//when the progress of seekBar is changing

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {//when drag action is started

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {//when drag action is ended
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {//when use click pause button in UI
            @Override
            public void onClick(View view) {
                pauseSong();
            }
        });//when use click pause button in UI

        next.setOnClickListener(new View.OnClickListener() {//when use click next button in UI
            @Override//local variable cannot be assigned in anynomous class,but global variable is ok
            public void onClick(View view) {
              nextSong();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               previousSong();
            }
        });//when use click previous button in UI

        seekBarReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {//receive the message from MusicService and Notification Service
                final String action=intent.getAction().toString();
                switch (action){
                    case "START_SEEKBAR":
                        seekBar.setMax(mediaPlayer.getDuration());//the maximum of the seekbar
                        updateSeekBar.start();//start thread after mediaPlayer is not null
                        break;
                    case "NEXT_SONG":
                        nextSong();
                        break;
                    case "PREVIOUS_SONG":
                        previousSong();
                        break;
                    case "PAUSE_SONG":
                        pauseSong();
                        break;
                }

            }
        };

        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("START_SEEKBAR");
        intentFilter.addAction("NEXT_SONG");
        intentFilter.addAction("PREVIOUS_SONG");
        intentFilter.addAction("PAUSE_SONG");

        registerReceiver(seekBarReceiver,intentFilter);//register a dynamic broadcast receiver
    }
    /*1.seekBar=new seekBar()//tag1
    * 2.seekBar=new seekBar()//tag2
    * 3.tag1 and tag2 is not the same*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this,MusicService.class));
        stopService(new Intent(this,NotificationService.class));
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();//delete the mediaPlayer
        mediaPlayer=null;
        unregisterReceiver(seekBarReceiver);//unregister the dynamic broadcast receiver,if we assign seekBarReceiver in onStart
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//detect the action of button in actionBar
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();//user pressed the back button
                break;
        }
        return super.onOptionsItemSelected(item);
    }



}
