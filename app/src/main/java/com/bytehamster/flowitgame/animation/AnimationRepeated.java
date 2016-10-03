package com.bytehamster.flowitgame.animation;

public class AnimationRepeated extends Animation {
    private final AnimationSingle animationForward;
    private final AnimationSingle animationBackward;
    private boolean isRunning = true;

    public AnimationRepeated(AnimationSingle animation) {
        super(null, 0);
        this.animationForward = animation;
        this.animationBackward = animation.reverse();
    }

    public final void start() {
        isRunning = true;
        new Thread() {
            public void run() {
                try {
                    while (isRunning) {
                        for (int i = 0; i < animationForward.getSteps(); i++) {
                            animationForward.doStep(i);
                            Thread.sleep(STEP_DELAY);
                        }
                        animationForward.finalStep();

                        for (int i = 0; i < animationBackward.getSteps(); i++) {
                            animationBackward.doStep(i);
                            Thread.sleep(STEP_DELAY);
                        }
                        animationBackward.finalStep();
                    }
                } catch (InterruptedException e) {
                    animationBackward.finalStep();
                }
            }
        }.start();
    }

    public final void stop() {
        isRunning = false;
    }

}
