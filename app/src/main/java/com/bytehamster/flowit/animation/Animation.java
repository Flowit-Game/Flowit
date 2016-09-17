package com.bytehamster.flowit.animation;

import com.bytehamster.flowit.object.Drawable;

public abstract class Animation {
    public static final int STEP_DELAY = 30;
    public static final int DURATION_LONG = 400;
    public static final int DURATION_SHORT = 200;

    private Drawable subject;
    private int steps = 0;
    private final int startIn;

    Animation(Drawable subject, int steps, int startIn) {
        this.subject = subject;
        this.steps = steps;
        this.startIn = startIn;
    }

    Drawable getSubject() {
        return subject;
    }

    abstract void doStep(int step);
    abstract void finalStep();

    public void start () {
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(startIn);
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
