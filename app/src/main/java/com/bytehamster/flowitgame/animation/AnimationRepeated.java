package com.bytehamster.flowitgame.animation;

public class AnimationRepeated extends Animation {
    private final AnimationSingle animationForward;
    private final AnimationSingle animationBackward;
    private boolean isRunningForward = true;
    private boolean shouldBeStopped = false;

    public AnimationRepeated(AnimationSingle animation) {
        super(animation.getSubject(), 0);
        animation.destroy(); // Removes from subject array. Managed using this wrapper now.
        animation.firstTick(); // To initialize "from"
        this.animationForward = animation;
        this.animationBackward = animation.reverse();
    }

    @Override
    void tick(long durationRunning) {
        if (durationRunning > animationForward.getDuration()) {
            if (shouldBeStopped && !isRunningForward) {
                this.destroy();
                return;
            }

            isRunningForward = !isRunningForward;
            animationForward.restart();
            animationBackward.restart();
            super.restart();
        }

        if (isRunningForward) {
            animationForward.tick();
        } else {
            animationBackward.tick();
        }
    }

    @Override
    public void start() {
        super.start();
        animationForward.restart();
        isRunningForward = true;
        shouldBeStopped = false;
    }

    @Override
    void restart() {
        animationForward.restart();
        isRunningForward = true;
    }

    public void stopWhenFinished () {
        shouldBeStopped = true;
    }
}
