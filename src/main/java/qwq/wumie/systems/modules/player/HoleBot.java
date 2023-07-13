/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.systems.modules.player;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import meteordevelopment.meteorclient.events.world.UpdateEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.HoleESP;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class HoleBot extends Module {
    public HoleBot() {
        super(Categories.Player, "hole-bot", "walk to hole :)");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> disableHoleESP = sgGeneral.add(new BoolSetting.Builder()
        .name("disable-holeesp")
        .defaultValue(false)
        .build()
    );

    @Override
    public void onActivate() {
        final HoleESP holeESP = Modules.get().get(HoleESP.class);
        if (holeESP == null) return;
        if (!holeESP.isActive()) {
            holeESP.toggle();
        }
        super.onActivate();
    }

    @Override
    public void onDeactivate() {
        final HoleESP holeESP = Modules.get().get(HoleESP.class);
        if (holeESP == null) return;
        if (holeESP.isActive() && disableHoleESP.get()) {
            holeESP.toggle();
        }
        super.onDeactivate();
    }

    @EventHandler
    private void update(UpdateEvent event) {
        final HoleESP holeESP = Modules.get().get(HoleESP.class);
        if (holeESP == null) return;
        List<HoleESP.Hole> holes = holeESP.getHoles();
        if (holes.size() == 0) return;
        holes.sort((h1,h2)-> Double.compare(PlayerUtils.distanceTo(new BlockPos(h1.blockPos)),PlayerUtils.distanceTo(h2.blockPos)));

        final IBaritone baritone = BaritoneAPI.getProvider().getPrimaryBaritone();

        if (holes.get(0) == null) return;
        String targetHole = "goto " + holes.get(0).blockPos.getX() + " " + holes.get(0).blockPos.getY() + " " + holes.get(0).blockPos.getZ();
        baritone.getCommandManager().execute(targetHole);
    }
}
