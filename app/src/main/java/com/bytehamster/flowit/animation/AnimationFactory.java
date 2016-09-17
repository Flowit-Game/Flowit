package com.bytehamster.flowit.animation;

import com.bytehamster.flowit.object.Mesh;

public class AnimationFactory {
    public static void startMenuAnimationOutPressed(Mesh mesh, float width, float yPos) {
        TranslateAnimation exitButtonAnimation = new TranslateAnimation(mesh, Animation.DURATION_LONG, Animation.DURATION_SHORT);
        exitButtonAnimation.setFrom(0, yPos);
        exitButtonAnimation.setTo(-width, yPos);
        exitButtonAnimation.setHideAfter(true);
        exitButtonAnimation.start();
    }

    public static void startMenuAnimationOut(Mesh mesh, float width, float yPos) {
        TranslateAnimation exitButtonAnimation = new TranslateAnimation(mesh, Animation.DURATION_SHORT, 0);
        exitButtonAnimation.setFrom(0, yPos);
        exitButtonAnimation.setTo(-width, yPos);
        exitButtonAnimation.setHideAfter(true);
        exitButtonAnimation.start();
    }

    public static void startMenuAnimationEnter(Mesh mesh, float width, float yPos, int delay) {
        mesh.setVisible(true);
        mesh.setX(-width);
        TranslateAnimation exitButtonAnimation = new TranslateAnimation(mesh, Animation.DURATION_LONG, delay);
        exitButtonAnimation.setFrom(-width, yPos);
        exitButtonAnimation.setTo(0, yPos);
        exitButtonAnimation.start();
    }
}
