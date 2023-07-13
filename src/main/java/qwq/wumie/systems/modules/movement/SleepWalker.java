/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.systems.modules.movement;

import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.UpdateEvent;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import org.lwjgl.glfw.GLFW;

public class SleepWalker extends Module {
    public SleepWalker() {
        super(Categories.Movement, "SleepWalker", "sleep walk");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Keybind> resetKey = sgGeneral.add(new KeybindSetting.Builder()
            .name("reset-key")
            .defaultValue(Keybind.fromKey(GLFW.GLFW_KEY_DELETE))
            .build()
    );

    public final Setting<Keybind> fastResetKey = sgGeneral.add(new KeybindSetting.Builder()
            .name("fast-reset-key")
            .defaultValue(Keybind.fromKey(GLFW.GLFW_KEY_END))
            .build()
    );

    private boolean lastSleep;

    @EventHandler
    private void onKey(KeyEvent event) {
        if (Utils.canUpdate())
            if (event.action.equals(KeyAction.Press)) {
                if (event.key == fastResetKey.get().getValue())
                    stopSleeping();

                if (event.key == resetKey.get().getValue())
                    if (lastSleep) stopSleeping();

            }
    }

    @EventHandler
    private void onPacket(PacketEvent.Sent event) {
        if (event.packet instanceof ClientCommandC2SPacket packet) {
            if (packet.getMode().equals(ClientCommandC2SPacket.Mode.STOP_SLEEPING)) {
                lastSleep = false;
            }
        }
    }

    @Override
    public void onActivate() {
        lastSleep = mc.player.isSleeping();
        super.onActivate();
    }

    private void stopSleeping() {
        mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SLEEPING));
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player.isSleeping()) {
            mc.player.setPose(EntityPose.STANDING);
            mc.player.clearSleepingPosition();
            lastSleep = true;
        }
    }
}
