/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.utils.time;

public class TickTimer {
    int tick = 0;

    public void update() {
        tick++;
    }

    public void reset() {
        tick = 0;
    }

    public boolean hasTimePassed(int ticks) {
        return tick >= ticks;
    }
}
