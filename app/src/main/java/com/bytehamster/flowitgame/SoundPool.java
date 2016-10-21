package com.bytehamster.flowitgame;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.Build;
import android.util.SparseIntArray;

public class SoundPool {
	private final android.media.SoundPool pool;
	private final SparseIntArray items = new SparseIntArray();
	private final Context myContext;
	
	SoundPool(Activity a) {
		a.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		myContext = a;

		if (Build.VERSION.SDK_INT < 21) {
			//noinspection deprecation
			pool = new android.media.SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		} else {
			android.media.SoundPool.Builder builder = new android.media.SoundPool.Builder();
			builder.setAudioAttributes(new AudioAttributes.Builder()
					.setUsage(AudioAttributes.USAGE_GAME)
					.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
					.build());
			builder.setMaxStreams(3);
			pool = builder.build();
		}

        pool.setOnLoadCompleteListener(new android.media.SoundPool.OnLoadCompleteListener() {
            @Override
			public void onLoadComplete(android.media.SoundPool soundPool, int sampleId, int status) {
			}
        });
        
	}
	void loadSound(int ResID){
		items.put(ResID, pool.load(myContext, ResID, 1));
	}

	public void playSound(int ResID){
		AudioManager audioManager = (AudioManager) myContext.getSystemService(Context.AUDIO_SERVICE);
        float actualVolume = (float) audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = actualVolume / maxVolume;
        
        pool.play(items.get(ResID), volume, volume, 1, 0, 1f);
	}
}
