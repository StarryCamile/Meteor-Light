/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package qwq.wumie.systems.modules.misc;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.WritableBookItem;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;

public class AntiBookKick extends Module {
    private final SettingGroup sg = settings.getDefaultGroup();

    private final Setting<Integer> maxSize = sg.add(new IntSetting.Builder()
            .name("max-size")
            .range(100,1000000)
            .sliderRange(100,1000000)
            .defaultValue(10000)
            .build()
    );

    public AntiBookKick() {
        super(Categories.Misc,"AntiBookKick","Prevents being kicked by clicking on books");
    }

    @EventHandler
    public void onRe(PacketEvent.Receive e) {
        if (mc.player.getMainHandStack().getItem() instanceof WrittenBookItem || mc.player.getMainHandStack().getItem() instanceof WritableBookItem || mc.player.getMainHandStack().getNbt().getSize() > maxSize.get()) {
            e.cancel();
        }
    }
}
