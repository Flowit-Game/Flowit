package com.bytehamster.flowit.animation;

import com.bytehamster.flowit.object.Plane;

public class AnimationFactory {
    public static void startMenuAnimationOutPressed(Plane plane) {
        TranslateAnimation exitButtonAnimation = new TranslateAnimation(plane, Animation.DURATION_LONG, Animation.DURATION_SHORT);
        exitButtonAnimation.setFrom(0, plane.getY());
        exitButtonAnimation.setTo(-plane.getWidth(), plane.getY());
        exitButtonAnimation.setHideAfter(true);
        exitButtonAnimation.start();
    }

    public static void startMenuAnimationOut(Plane plane) {
        TranslateAnimation exitButtonAnimation = new TranslateAnimation(plane, Animation.DURATION_SHORT, 0);
        exitButtonAnimation.setFrom(0, plane.getY());
        exitButtonAnimation.setTo(-plane.getWidth(), plane.getY());
        exitButtonAnimation.setHideAfter(true);
        exitButtonAnimation.start();
    }

    public static void startMenuAnimationEnter(Plane plane, int delay) {
        plane.setVisible(true);
        plane.setX(-plane.getWidth());
        TranslateAnimation exitButtonAnimation = new TranslateAnimation(plane, Animation.DURATION_LONG, delay);
        exitButtonAnimation.setFrom(-plane.getWidth(), plane.getY());
        exitButtonAnimation.setTo(0, plane.getY());
        exitButtonAnimation.start();
    }

    public static void startScaleShow(Plane plane) {
        startScaleShow(plane, Animation.DURATION_LONG);
    }

    public static void startScaleShow(Plane plane, int delay) {
        plane.setScale(0);
        plane.setVisible(true);
        ScaleAnimation leftAnimation = new ScaleAnimation(plane, Animation.DURATION_LONG, delay);
        leftAnimation.setFrom(0);
        leftAnimation.setTo(1);
        leftAnimation.start();
    }

    public static void startScaleHide(Plane plane) {
        startScaleHide(plane, Animation.DURATION_LONG);
    }

    public static void startScaleHide(Plane plane, int delay) {
        ScaleAnimation leftAnimation = new ScaleAnimation(plane, Animation.DURATION_LONG, delay);
        leftAnimation.setFrom(1);
        leftAnimation.setTo(0);
        leftAnimation.setHideAfter(true);
        leftAnimation.start();
    }
}
