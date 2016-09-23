package com.bytehamster.flowit;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.bytehamster.flowit.state.ExitState;
import com.bytehamster.flowit.state.GameState;
import com.bytehamster.flowit.state.LevelPackSelectState;
import com.bytehamster.flowit.state.LevelSelectState;
import com.bytehamster.flowit.state.MainMenuState;
import com.bytehamster.flowit.state.SettingsState;
import com.bytehamster.flowit.state.State;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class Main extends Activity {
    private MyGLSurfaceView glSurfaceView;
    private SoundPool soundPool;
    private State currentState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        glSurfaceView = (MyGLSurfaceView) findViewById(R.id.gl_surface_view);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("BD77EB0FBCAB78BD7BD95E32C84D1F73")
                .build();
        mAdView.loadAd(adRequest);

        createViews();
    }

    private void createViews() {
        glSurfaceView.getRenderer().setOnReady(new Runnable() {
            @Override
            public void run() {
                soundPool = new SoundPool(Main.this);
                soundPool.loadSound(R.raw.click);
                soundPool.loadSound(R.raw.fill);
                soundPool.loadSound(R.raw.won);

                State[] states = new State[] {
                        MainMenuState.getInstance(),
                        ExitState.getInstance(),
                        SettingsState.getInstance(),
                        LevelPackSelectState.getInstance(),
                        LevelSelectState.getInstance(),
                        GameState.getInstance()
                };

                int adHeight = AdSize.SMART_BANNER.getHeightInPixels(Main.this);
                for (State state : states) {
                    state.initialize(glSurfaceView.getRenderer(), soundPool, Main.this, adHeight);
                }

                currentState = MainMenuState.getInstance();
                currentState.entry();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(currentState != null && glSurfaceView.getRenderer().isReady()) {
            currentState.onKeyDown(keyCode, event);
            switchState();
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if(currentState != null && glSurfaceView.getRenderer().isReady()) {
            currentState.onTouchEvent(event);
            switchState();
        }
        return false;
    }

    private void switchState() {
        State newState = currentState.next();
        if (currentState != newState) {
            currentState.exit();
            currentState = newState;
            currentState.entry();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                glSurfaceView.onResume();
                glSurfaceView.getRenderer().onResume();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }
}
