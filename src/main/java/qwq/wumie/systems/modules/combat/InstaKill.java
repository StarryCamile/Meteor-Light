/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.systems.modules.combat;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class InstaKill extends Module {
    public static final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    public static Boolean activate = false;
    public static Integer loopPasses = 100;

    private final SettingGroup sgInstaKill = settings.createGroup("General");

    public InstaKill() {
        super(Categories.Combat, "InstaKill", "InstaKill exploit");
    }

    private final Setting<Integer> loopPassesSlider = sgInstaKill.add(new IntSetting.Builder()
        .name("Loop passes")
        .description("Number of loop passes")
        .defaultValue(100)
        .sliderMin(1)
        .sliderMax(200)
        .build()
    );

    @Override
    public void onActivate() {
        activate = true;
    }

    @EventHandler
    private void onTickPost(TickEvent.Post event) {
        loopPasses = loopPassesSlider.get();
    }

    @Override
    public void onDeactivate() {
        activate = false;
    }

    public static void addVelocityToPlayer() {
        if (activate) {
            assert minecraftClient.player != null;
            minecraftClient.player.networkHandler.sendPacket(new ClientCommandC2SPacket(minecraftClient.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
            for (int i = 0; i < loopPasses; i++) {
                minecraftClient.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(minecraftClient.player.getX(), minecraftClient.player.getY() - 1.0E-9, minecraftClient.player.getZ(), true));
                minecraftClient.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(minecraftClient.player.getX(), minecraftClient.player.getY() + 1.0E-9, minecraftClient.player.getZ(), false));
            }
        }
    }
}
