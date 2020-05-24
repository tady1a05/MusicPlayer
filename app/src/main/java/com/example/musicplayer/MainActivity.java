package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String PERMISSION="android.permission.WRITE_EXTERNAL_STORAGE";
    private ListView mySongListView;
    String[] Items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mySongListView=(ListView)findViewById(R.id.mySongListView);
        runtimePermission();//ask whether a user grant the permission
    }

    public void runtimePermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {//if user granted the permission
                        display();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {//if user denied permission

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {//if user denied permission and click in application again
                        token.continuePermissionRequest();
                    }
                }).check();
    }

   /* public void runtimePermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            display();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    display();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
*/
    public ArrayList<File> findsong(File dir){
       ArrayList<File> songList=new ArrayList<>();

       File[] files=dir.listFiles();//all files of directory

           for (File a : files) {
               if (a.isDirectory() && !a.isHidden()) {//if it is folder of the root and it is not hidden and go to findsong function
                   songList.addAll(findsong(a));//use to add arrayList to other arrayList
               } else {
                   if (a.getName().endsWith(".mp3")) {//if the name of folder is end with mp4
                       songList.add(a);//add to arraylist
                   }
               }

       }

       return songList;
    }
    void display(){
        final ArrayList <File> songList=findsong(Environment.getExternalStorageDirectory());//go to SD card loction

        Items=new String[songList.size()];

        for(int i=0;i<songList.size();i++){
            Items[i]=songList.get(i).getName().replace(".mp3","");//put song name in the item array without ".mp4"
        }

        ArrayAdapter<String> myArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,Items);//context,layout,item array
        mySongListView.setAdapter(myArrayAdapter);//listView need ArrayAdapter

        //if a item of listview has been selected
        mySongListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songName=mySongListView.getItemAtPosition(i).toString();//when the item of listView has been selected,get the name of item
                startActivity(new Intent(getApplicationContext(),MusicPlayerActivity.class).putExtra("songs",songList).putExtra("songName",songName).putExtra("position",i).putExtra("songList",Items));//start MusicPlayerActivity and transfer data to there
                //ArrayList<File>,String,integer,StringArray
            }
        });
    }
}
