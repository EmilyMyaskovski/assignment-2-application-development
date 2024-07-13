package com.example.assignment2.Utilities;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SoundCrash {

    private Context context;
    private int soundResId;
    private Executor executor;
    private MediaPlayer mediaPlayer;

    public SoundCrash(Context context) {
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void playSound(int resID) {
        if (mediaPlayer != null) {
            executor.execute(() -> {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            });
        }
        executor.execute(() -> {
            mediaPlayer = MediaPlayer.create(context, resID);
            mediaPlayer.setVolume(1.0f, 1.0f);
            mediaPlayer.setOnCompletionListener(mp -> {
                mediaPlayer.release();
                mediaPlayer = null;
            });
            mediaPlayer.start();
        });
    }

}
