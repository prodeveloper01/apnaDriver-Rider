package com.qboxus.gograbdriver.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.qboxus.gograbdriver.R;

public class BackgroundSoundService extends Service {
    MediaPlayer player;
    Vibrator vibrator;
    Handler handler;

    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler=new Handler(Looper.getMainLooper());
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        player = MediaPlayer.create(getApplicationContext(), R.raw.ring_tone);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(3500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(3500);
        }
        handler.postDelayed(runnable,15000);

        return START_NOT_STICKY;
    }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            onStop();
        }
    };


    public void onStop() {
        if(player!=null && player.isPlaying()){
            player.stop();
        }
        if (vibrator.hasVibrator() || vibrator!=null)
        {
            vibrator.cancel();
        }
        if (handler!=null)
        {
            handler.removeCallbacks(runnable);
        }
    }
    @Override
    public void onDestroy() {
        onStop();
    }

    @Override
    public void onLowMemory() {
    }
}