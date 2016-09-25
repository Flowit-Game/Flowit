package com.bytehamster.flowit.animation;

import com.bytehamster.flowit.object.Drawable;

abstract class AnimationSingle extends Animation {
    final int steps;

    AnimationSingle(Drawable subject, int steps, int startIn) {
        super(subject, startIn);
        this.steps = steps;
    }

    abstract void doStep(int step);
    abstract void finalStep();
    abstract AnimationSingle reverse();

    int getSteps() {
        return steps;
    }

    public final void start () {
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(getStartIn());
                } catch (InterruptedException e) {
                    return;
                }

                for (int i = 0; i < steps; i++) {

                    doStep(i);

                    try {
                        Thread.sleep(STEP_DELAY);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                finalStep();
            }
        }.start();
    }

}
