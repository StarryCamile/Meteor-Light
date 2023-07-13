/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.utils.render;

import net.minecraft.client.MinecraftClient;

public class ScreenUtil {
    public int width;
    public int height;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getFactor() {
        return factor;
    }

    public double factor;

    public ScreenUtil(MinecraftClient mc) {
        this.width = mc.getWindow().getScaledWidth();
        this.height = mc.getWindow().getScaledHeight();
        this.factor = mc.getWindow().getScaleFactor();
    }
}
