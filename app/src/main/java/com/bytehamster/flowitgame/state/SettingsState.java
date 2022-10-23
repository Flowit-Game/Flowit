package com.bytehamster.flowitgame.state;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.MotionEvent;

import com.bytehamster.flowitgame.GLRenderer;
import com.bytehamster.flowitgame.R;
import com.bytehamster.flowitgame.animation.Animation;
import com.bytehamster.flowitgame.animation.AnimationFactory;
import com.bytehamster.flowitgame.animation.ScaleAnimation;
import com.bytehamster.flowitgame.object.ObjectFactory;
import com.bytehamster.flowitgame.object.Plane;
import com.bytehamster.flowitgame.object.TextureCoordinates;

public class SettingsState extends State {
    @SuppressLint("StaticFieldLeak")
    private static SettingsState instance;
    private State nextState = this;

    private Plane volumeOff;
    private Plane volumeOn;
    private Plane tutorialButton;
    private Plane volumeButton;
    private Plane editorButton;
    private Plane colorsExample;
    private Plane colorsButton;

    private int numberOfColorschemes;
    private GLRenderer glRenderer;

    private SettingsState() {

    }

    public static SettingsState getInstance() {
        if (instance == null) {
            instance = new SettingsState();
        }
        return instance;
    }

    @Override
    protected void initialize(GLRenderer glRendererIn) {
        glRenderer = glRendererIn;

        numberOfColorschemes = glRenderer.numberOfColorschemes;

        float menuEntriesWidth = glRenderer.getWidth() * 0.75f;
        float menuEntriesHeight = menuEntriesWidth / 6;
        float menuEntriesAvailableSpace = getScreenHeight();
        float menuEntriesStartY = getScreenHeight() - (menuEntriesAvailableSpace - 4 * menuEntriesHeight) / 2;

        TextureCoordinates coordinatesVolume = TextureCoordinates.getFromBlocks(6, 13, 12, 14);
        volumeButton = new Plane(-menuEntriesWidth, menuEntriesStartY, menuEntriesWidth, menuEntriesHeight, coordinatesVolume);
        glRenderer.addDrawable(volumeButton);

        TextureCoordinates coordinatesColors = TextureCoordinates.getFromBlocks(0, 15, 6, 16);
        colorsButton = new Plane(-menuEntriesWidth, volumeButton.getY() - 2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesColors);
        glRenderer.addDrawable(colorsButton);

        TextureCoordinates coordinatesTutorial = TextureCoordinates.getFromBlocks(6, 12, 12, 13);
        tutorialButton = new Plane(-menuEntriesWidth, colorsButton.getY() - 2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesTutorial);
        glRenderer.addDrawable(tutorialButton);

        TextureCoordinates coordinatesEditor = TextureCoordinates.getFromBlocks(6, 15, 12, 16);
        editorButton = new Plane(-menuEntriesWidth, tutorialButton.getY() - 2 * menuEntriesHeight, menuEntriesWidth, menuEntriesHeight, coordinatesEditor);
        glRenderer.addDrawable(editorButton);

        volumeOn  = ObjectFactory.createSingleBox(12, 12, menuEntriesHeight);
        volumeOn.setVisible(false);
        volumeOn.setX(menuEntriesWidth);
        volumeOn.setY(volumeButton.getY());
        glRenderer.addDrawable(volumeOn);
        volumeOff = ObjectFactory.createSingleBox(13, 12, menuEntriesHeight);
        volumeOff.setVisible(false);
        volumeOff.setX(menuEntriesWidth);
        volumeOff.setY(volumeButton.getY());
        glRenderer.addDrawable(volumeOff);

        TextureCoordinates coordinatesColorsExample = TextureCoordinates.getFromBlocks(14, 1, 16, 2);
        colorsExample = new Plane(-menuEntriesWidth/2, menuEntriesStartY, menuEntriesHeight*2, menuEntriesHeight, coordinatesColorsExample);
        colorsExample.setVisible(false);
        colorsExample.setX(menuEntriesWidth);
        colorsExample.setY(colorsButton.getY());
        glRenderer.addDrawable(colorsExample);
    }

    @Override
    public void entry() {
        nextState = this;

        Plane objShown = getPreferences().getBoolean("volumeOn", true) ? volumeOn : volumeOff;
        objShown.setVisible(true);
        objShown.setScale(0);
        ScaleAnimation scaleAnimation = new ScaleAnimation(objShown,
                Animation.DURATION_LONG, Animation.DURATION_LONG);
        scaleAnimation.setTo(1);
        scaleAnimation.start();

        colorsExample.setVisible(true);
        colorsExample.setScale(0);
        ScaleAnimation colorsScaleAnimation = new ScaleAnimation(colorsExample,
                Animation.DURATION_LONG, Animation.DURATION_LONG + (int) 1.0f * Animation.DURATION_SHORT);
        colorsScaleAnimation.setTo(1);
        colorsScaleAnimation.start();

        AnimationFactory.startMenuAnimationEnter(volumeButton, (int) (2.0f * Animation.DURATION_SHORT));
        AnimationFactory.startMenuAnimationEnter(colorsButton, (int) (2.5f * Animation.DURATION_SHORT));
        AnimationFactory.startMenuAnimationEnter(tutorialButton, (int) (3.0f * Animation.DURATION_SHORT));
        AnimationFactory.startMenuAnimationEnter(editorButton, (int) (3.5f * Animation.DURATION_SHORT));
    }

    @Override
    public void exit() {
        Plane objShown = getPreferences().getBoolean("volumeOn", true) ? volumeOn : volumeOff;
        ScaleAnimation scaleAnimation = new ScaleAnimation(objShown,
                Animation.DURATION_LONG, 0);
        scaleAnimation.setTo(0);
        scaleAnimation.setHideAfter(true);
        scaleAnimation.start();

        ScaleAnimation colorsScaleAnimation = new ScaleAnimation(colorsExample,
                Animation.DURATION_LONG, 0);
        colorsScaleAnimation.setTo(0);
        colorsScaleAnimation.setHideAfter(true);
        colorsScaleAnimation.start();

        if (nextState == TutorialState.getInstance()) {
            AnimationFactory.startMenuAnimationOutPressed(tutorialButton);
        } else {
            AnimationFactory.startMenuAnimationOut(tutorialButton);
        }

        AnimationFactory.startMenuAnimationOut(volumeButton);
        AnimationFactory.startMenuAnimationOut(colorsButton);
        AnimationFactory.startMenuAnimationOut(editorButton);
    }

    @Override
    public State next() {
        return nextState;
    }

    @Override
    public void onBackPressed() {
        nextState = MainMenuState.getInstance();
        playSound(R.raw.click);
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (volumeOn.collides(event, getScreenHeight()) || volumeButton.collides(event, getScreenHeight())) {
                playSound(R.raw.click);
                boolean newVolume = ! getPreferences().getBoolean("volumeOn", true);
                getPreferences().edit().putBoolean("volumeOn", newVolume).apply();
                volumeOff.setVisible(!newVolume);
                volumeOn.setVisible(newVolume);
            } else if (colorsExample.collides(event, getScreenHeight()) || colorsButton.collides(event, getScreenHeight())) {
                playSound(R.raw.click);
                int newColorschemeIndex = (getPreferences().getInt("colorschemeIndex", 0) + 1) % numberOfColorschemes;
                getPreferences().edit().putInt("colorschemeIndex", newColorschemeIndex).apply();
                glRenderer.setColorscheme(newColorschemeIndex);
            } else if (tutorialButton.collides(event, getScreenHeight())) {
                nextState = TutorialState.getInstance();
                playSound(R.raw.click);
            } else if (editorButton.collides(event, getScreenHeight())) {
                nextState = MainMenuState.getInstance();
                playSound(R.raw.click);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://flowit.bytehamster.com/"));
                getActivity().startActivity(browserIntent);
            }
        }
    }
}
