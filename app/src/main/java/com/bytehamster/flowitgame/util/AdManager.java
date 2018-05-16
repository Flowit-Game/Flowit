package com.bytehamster.flowitgame.util;

import android.content.Context;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class AdManager {
    private final Context context;
    private final AdView adView;

    public AdManager(Context context, AdView adView) {
        this.context = context;
        this.adView = adView;
        MobileAds.initialize(context.getApplicationContext(), "ca-app-pub-8233037560237995~7887041460");
    }

    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("4413FE813D2B5516E668ABC78B99A1BF")
                .build();
        adView.loadAd(adRequest);
    }
}
