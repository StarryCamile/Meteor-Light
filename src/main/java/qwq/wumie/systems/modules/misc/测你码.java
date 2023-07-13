/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.systems.modules.misc;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.UpdateEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class 测你码 extends Module {
    public 测你码() {
        super(Categories.Misc, "packet-cancel", "我测你码");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> send = sgGeneral.add(new BoolSetting.Builder()
        .name("send")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> received = sgGeneral.add(new BoolSetting.Builder()
        .name("send")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> sendLimit = sgGeneral.add(new IntSetting.Builder()
        .name("send-limit")
        .defaultValue(400)
        .visible(send::get)
        .build()
    );

    private final Setting<Integer> receivedLimit = sgGeneral.add(new IntSetting.Builder()
        .name("received-limit")
        .defaultValue(500)
        .visible(received::get)
        .build()
    );

    @EventHandler
    private void idk(UpdateEvent e) {
        int sendps = mc.getNetworkHandler() == null ? 23 : (int) mc.getNetworkHandler().getConnection().getAveragePacketsSent();
        int receivedps = mc.getNetworkHandler() == null ? 86 : (int) mc.getNetworkHandler().getConnection().getAveragePacketsReceived();

        if (send.get()) {
            if (sendps > sendLimit.get()) {
                e.cancel();
            }
        }

        if (received.get()) {
            if (receivedps > receivedLimit.get()) {
                e.cancel();
            }
        }
    }
}
