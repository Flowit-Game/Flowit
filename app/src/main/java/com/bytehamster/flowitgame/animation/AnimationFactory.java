package com.bytehamster.flowitgame.animation;

import com.bytehamster.flowitgame.object.Drawable;
import com.bytehamster.flowitgame.object.Plane;

public class AnimationFactory {
    public static void startMenuAnimationOutPressed(Plane plane) {
        plane.cancelAnimations();
        TranslateAnimation menuAnimation = new TranslateAnimation(plane, Animation.DURATION_LONG, Animation.DURATION_SHORT);
        menuAnimation.setTo(-plane.getWidth(), plane.getY());
        menuAnimation.setHideAfter(true);
        menuAnimation.start();
    }

    public static void startMenuAnimationOut(Plane plane) {
        plane.cancelAnimations();
        TranslateAnimation menuAnimation = new TranslateAnimation(plane, Animation.DURATION_SHORT, 0);
        menuAnimation.setTo(-plane.getWidth(), plane.getY());
        menuAnimation.setHideAfter(true);
        menuAnimation.start();
    }

    public static void startMenuAnimationEnter(Plane plane, int delay) {
        plane.cancelAnimations();
        plane.setVisible(true);
        TranslateAnimation menuAnimation = new TranslateAnimation(plane, Animation.DURATION_LONG, delay);
        menuAnimation.setTo(0, plane.getY());
        menuAnimation.start();
    }

    public static void startScaleShow(Plane plane) {
        startScaleShow(plane, Animation.DURATION_LONG);
    }

    public static void startScaleShow(Plane plane, int delay) {
        plane.cancelAnimations();
        plane.setVisible(true);
        ScaleAnimation leftAnimation = new ScaleAnimation(plane, Animation.DURATION_LONG, delay);
        leftAnimation.setTo(1);
        leftAnimation.start();
    }

    public static void startScaleHide(Plane plane) {
        startScaleHide(plane, Animation.DURATION_LONG);
    }

    public static void startScaleHide(Plane plane, int delay) {
        plane.cancelAnimations();
        ScaleAnimation leftAnimation = new ScaleAnimation(plane, Animation.DURATION_LONG, delay);
        leftAnimation.setTo(0);
        leftAnimation.setHideAfter(true);
        leftAnimation.start();
    }

    public static void startMoveYTo(Drawable plane, float toY) {
        plane.cancelAnimations();
        plane.setVisible(true);
        TranslateAnimation leftAnimation = new TranslateAnimation(plane, Animation.DURATION_LONG, Animation.DURATION_LONG);
        leftAnimation.setTo(plane.getX(), toY);
        leftAnimation.start();
    }
}
