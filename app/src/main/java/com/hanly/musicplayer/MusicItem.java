package com.hanly.musicplayer;

import android.graphics.Bitmap;

class MusicItem{
    private final String name;
    private final String path;
    private final String singer;
    private final Bitmap image;
    private final int id;

    public MusicItem(String path, String name, String singer, Bitmap image, int id){
        this.path = path;
        this.name = name;
        this.singer = singer;
        this.image = image;
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public String getPath(){
        return path;
    }

    public String getSinger(){
        return singer;
    }

    public Bitmap getImage(){
        return image;
    }

    public int getId(){
        return id;
    }
}