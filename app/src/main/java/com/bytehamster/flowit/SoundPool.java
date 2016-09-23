package com.bytehamster.flowit;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.util.SparseIntArray;

public class SoundPool
{
	android.media.SoundPool pool;
	
	SparseIntArray items = new SparseIntArray();
	Context myContext;
	
	public SoundPool(Activity a){
		a.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		myContext = (Context) a;
        // Load the sound
        pool = new android.media.SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        pool.setOnLoadCompleteListener(new android.media.SoundPool.OnLoadCompleteListener() {
            @Override
			public void onLoadComplete(android.media.SoundPool soundPool, int sampleId, int status)
			{
			}
        });
        
	}
	public void loadSound(int ResID){
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
