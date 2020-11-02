package com.hanly.musicplayer;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class SlidePageFragment extends Fragment {
    private int position = 0;
    private final ArrayList<MusicItem> musicList;
    protected static MusicItem nowPlaying;
    protected static Handler handler;

    public SlidePageFragment(int position, ArrayList<MusicItem> musicList){
        this.position = position;
        this.musicList = musicList;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v;
        if (position == 1) {
            v = inflater.inflate(R.layout.music_list, container, false);
            MusicArrayAdapter arrayAdapter = new MusicArrayAdapter(Objects.requireNonNull(getContext()), R.layout.list_item, musicList);
            ListView listView = v.findViewById(R.id.list1);
            if (musicList.size() != 0) {
                listView.setAdapter(arrayAdapter);
            }
        }
        else {
            v = inflater.inflate(R.layout.player, container, false);
            handler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(@NonNull Message message) {
                    ((ImageView)v.findViewById(R.id.pause)).setImageResource(R.drawable.pause);
                    setNowPlaying(v);
                    return false;
                }
            });
            setButton(v);
        }
        return v;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    protected void setButton(final View v){
         v.findViewById(R.id.pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (MainActivity.mPlayer.isPlaying()) {
                        ((ImageView)view).setImageResource(R.drawable.play);
                        MainActivity.pauseMusic();
                    }
                    else {
                        ((ImageView)view).setImageResource(R.drawable.pause);
                        MainActivity.resumeMusic();
                        setNowPlaying(v);
                    }
                }
                catch (IOException ignored){}

            }
         });

         v.findViewById(R.id.lastButton).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 try {
                     ((ImageView) view.getRootView().findViewById(R.id.pause)).setImageResource(R.drawable.pause);
                     MainActivity.lastMusic();
                     setNowPlaying(v);
                 } catch (IOException ignored) {}
             }
         });

         v.findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 try {
                     ((ImageView) view.getRootView().findViewById(R.id.pause)).setImageResource(R.drawable.pause);
                     MainActivity.nextMusic();
                     setNowPlaying(v);
                 } catch (IOException ignored) {}
             }
         });

        final boolean[] timerPause = {false};
         Timer timer = new Timer();                                                                       //更新进度条
         timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (!timerPause[0] && MainActivity.mPlayer.isPlaying()){
                        int curPosition = (int) (MainActivity.mPlayer.getCurrentPosition()*1.0f/MainActivity.mPlayer.getDuration()*100);
                        ((ProgressBar)v.findViewById(R.id.play_bar)).setProgress(curPosition);
                        if (curPosition == 100) {
                            try {
                                MainActivity.nextMusic();
                                handler.sendMessage(new Message());
                            } catch (IOException ignored) {}
                        }
                    }
                }
                catch (IllegalStateException ignored){}
            }
            }, 0, 100);


        ((SeekBar)v.findViewById(R.id.play_bar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {             //拖动进度
            public int seek = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seek = seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                timerPause[0] = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MainActivity.mPlayer.seekTo((int)(seek/100f*MainActivity.mPlayer.getDuration()));
                timerPause[0] = false;
            }
        });


        final AudioManager mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);          //音量控制
        final int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
        int current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        ((SeekBar)v.findViewById(R.id.vol)).setProgress((int)(current*1f/max*100));
        ((SeekBar)v.findViewById(R.id.vol)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int seek;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seek = seekBar.getProgress();
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(seek/100f*max), AudioManager.FLAG_PLAY_SOUND);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    protected void setNowPlaying(View v){
        ((ImageView)v.findViewById(R.id.musicImage)).setImageBitmap(nowPlaying.getImage());
        ((TextView)v.findViewById(R.id.musicTitle)).setText(nowPlaying.getName());
    }
}
