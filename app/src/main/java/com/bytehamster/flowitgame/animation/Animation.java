package com.bytehamster.flowitgame.animation;

import com.bytehamster.flowitgame.object.Drawable;

public abstract class Animation {
    public static final int DURATION_LONG = 400;
    public static final int DURATION_SHORT = 200;

    private final Drawable subject;
    private final int delay;
    private boolean isRunning = false;
    private boolean shouldBeDeleted = false;
    private long timeStarted;

    Animation(Drawable subject, int delay) {
        this.subject = subject;
        this.delay = delay;
        subject.addAnimation(this);
    }

    final Drawable getSubject() {
        return subject;
    }

    final int getDelay() {
        return delay;
    }

    private long timeSinceStarted() {
        return System.currentTimeMillis() - timeStarted;
    }

    public void start() {
        isRunning = true;
        timeStarted = System.currentTimeMillis();
        this.shouldBeDeleted = false;
    }

    public final void pause() {
        isRunning = false;
    }

    void destroy() {
        isRunning = false;
        this.shouldBeDeleted = true;
    }

    void restart() {
        isRunning = true;
        timeStarted = System.currentTimeMillis();
    }

    abstract void tick(long durationRunning);

    public final void tick() {
        if (!isRunning) {
            return;
        }

        if (timeSinceStarted() > delay) {
            tick(timeSinceStarted() - delay);
        }
    }

    public boolean shouldBeDeleted() {
        return shouldBeDeleted;
    }
}
