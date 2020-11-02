package com.hanly.musicplayer;

import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;

public class MusicArrayAdapter extends BaseAdapter {
    private final Context mContext;
    private final int mResource;
    private final ArrayList<MusicItem> musicItems;
    public MusicArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<MusicItem> items) {
        this.mContext = context;
        this.mResource = resource;
        this.musicItems = items;
    }

    @Override
    public int getCount() {
        return musicItems.size();
    }

    @Override
    public MusicItem getItem(int i) {
        return this.musicItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v;
        final MusicItem item = this.getItem(position);
        if (convertView == null){
            v = LayoutInflater.from(mContext).inflate(mResource, null);
        } else {
            v = convertView;
        }
        ((TextView)v.findViewById(R.id.name)).setText(item.getName());
        ((TextView)v.findViewById(R.id.singer)).setText(item.getSinger());
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MainActivity.playMusic(item);
                    SlidePageFragment.handler.sendMessage(new Message());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return v;
    }
}
