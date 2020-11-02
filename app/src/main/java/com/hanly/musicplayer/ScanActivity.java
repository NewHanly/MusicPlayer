package com.hanly.musicplayer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

public class ScanActivity extends Activity{
    private ArrayList<MusicItem> musicList;
    private Handler handler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_view);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                ((TextView)findViewById(R.id.scan_name)).setText(message.obj.toString());
                return false;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                musicList = scanMusic();
                onEnded();
            }
        }).start();

    }

    private void onEnded(){
        MainActivity.musicList = this.musicList;
        Intent intent = new Intent();
        intent.setClass(ScanActivity.this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    private ArrayList<MusicItem> scanMusic(){
        ArrayList<MusicItem> musicList = new ArrayList<>();
        File musicFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TestMusic");
        File[] list = musicFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".mp3");
            }
        });
        if (list != null){
            for (File f : list){
                MediaMetadataRetriever mmr=new MediaMetadataRetriever();
                mmr.setDataSource(f.getAbsolutePath());
                byte[] picture;
                Bitmap bitmap = null;
                try {
                    picture = mmr.getEmbeddedPicture();
                    bitmap= BitmapFactory.decodeByteArray(picture,0,picture.length);
                }
                catch (Exception ignored){}
                String name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String singer = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                musicList.add(new MusicItem(f.getAbsolutePath(), name, singer, bitmap, musicList.size()));
                Message message = new Message();
                message.obj = f.getName();
                handler.sendMessage(message);
            }
        }
        return musicList;
    }
}