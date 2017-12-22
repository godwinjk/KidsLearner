package com.godwin.drawview.ui.common;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.godwin.drawview.KidsApp;
import com.godwin.drawview.R;

/**
 * Created by Godwin on 12/19/2017 6:39 PM for DrawView.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class AudioManager {
    private static AudioManager sInstance = null;
    private static final Object MUTEX = new Object();
    private static  MediaPlayer mPlayer;
    private static final String BUTTON_SOUND_1 = "bubble_click1.mp3";
    private static final String BUTTON_SOUND_2 = "click.mp3";

    public static AudioManager getInstance() {
        if (null == sInstance) {
            synchronized (MUTEX) {
                sInstance = new AudioManager();
                mPlayer = MediaPlayer.create(KidsApp.Companion.getContext(), R.raw.bubble_click1);
            }
        }
        return sInstance;
    }

    private void play() {
        try {
            mPlayer.start();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), e.toString());
        }
    }

    public static void playButtonClick() {
        getInstance().play();
    }
}
