package com.hanly.musicplayer;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    private static  final  int NUM_PAGES=2;
    private ViewPager2 viewPager;
    protected static int playingID = 0;
    protected static MediaPlayer mPlayer;
    protected static boolean isPaused = false;
    protected static ArrayList<MusicItem> musicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPlayer = new MediaPlayer();
        verifyStoragePermissions(this);
        FragmentStateAdapter pagerAdapter = new ScreenSlidePageAdapter(this);
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0){
            super.onBackPressed();
        }
        else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    protected static void playMusic(MusicItem music) throws IOException {
        try {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = new MediaPlayer();
        }
        catch (Exception ignored){}
        SlidePageFragment.nowPlaying = music;
        playingID = music.getId();
        mPlayer.setDataSource(music.getPath());
        mPlayer.prepare();
        mPlayer.start();
    }

    protected static void pauseMusic(){
        isPaused = true;
        mPlayer.pause();
    }

    protected static void resumeMusic() throws IOException {
        if (isPaused){
            mPlayer.start();
        }
        else{
            playMusic(musicList.get(0));
        }
    }

    protected static void lastMusic() throws IOException {
        MusicItem music;
        if (playingID == 0){
            music = musicList.get(playingID);
        }
        else {
            music = musicList.get(playingID - 1);
        }
        playMusic(music);
    }

    protected static void nextMusic() throws IOException {
        MusicItem music;
        if (playingID == musicList.size() - 1){
            music = musicList.get(playingID);
        }
        else {
            music = musicList.get(playingID + 1);
        }
        playMusic(music);
    }

    protected static void seekTo(float x){

    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ScreenSlidePageAdapter extends FragmentStateAdapter{
        public ScreenSlidePageAdapter(FragmentActivity fa){
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return new SlidePageFragment(position, musicList);
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}

