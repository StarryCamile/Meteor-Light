/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package qwq.wumie.systems.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class Jetpack extends Module {

    public Jetpack() {
        super(Categories.Movement,"Jetpack","Allows you to fly as if you had a jetpack.");
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if (mc.options.jumpKey.isPressed()) {
            mc.player.jump();
        }
    }
}
