package com.bytehamster.flowitgame.animation;

import com.bytehamster.flowitgame.object.Drawable;

abstract class AnimationSingle extends Animation {
    private final int duration;
    private boolean hadFirstTick = false;

    AnimationSingle(Drawable subject, int duration, int startIn) {
        super(subject, startIn);
        this.duration = duration;
    }
    abstract AnimationSingle reverse();

    int getDuration() {
        return duration;
    }

    abstract void tick(double percentage);
    abstract void finalTick();

    void firstTick() {
        // To be overridden
    }

    @Override
    final void tick(long durationRunning) {
        if (!hadFirstTick) {
            hadFirstTick = true;
            firstTick();
        }
        if (durationRunning > getDuration()) {
            finalTick();
            this.destroy();
            return;
        }
        tick((double) durationRunning / (double) getDuration());
    }


}
