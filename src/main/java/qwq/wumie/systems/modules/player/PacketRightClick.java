/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package qwq.wumie.systems.modules.player;

import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class PacketRightClick extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Integer> tickDelay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("The delay between breaks.")
        .defaultValue(0)
        .min(0)
        .sliderMax(20)
        .build()
    );

    private final Setting<Boolean> block = sgGeneral.add(new BoolSetting.Builder()
        .name("only-block")
        .description("null")
        .defaultValue(false)
        .build()
    );

    private final Setting<Block> blocks = sgGeneral.add(new BlockSetting.Builder()
        .name("block-whitelist")
        .description("Which blocks to show x-rayed.")
            .visible(block::get)
            .defaultValue(Blocks.GRAVEL)
        .build()
    );

    private final Setting<Boolean> silent = sgGeneral.add(new BoolSetting.Builder()
        .name("Silent")
        .description("null")
        .defaultValue(false)
            .visible(block::get)
        .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Faces the blocks being mined server side.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> autodisable = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-disable")
        .defaultValue(true)
        .build()
    );

    // Render

    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
        .name("render")
        .description("Renders a block overlay on the block being broken.")
        .defaultValue(true)
        .build()
    );

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The color of the sides of the blocks being rendered.")
        .defaultValue(new SettingColor(0, 255, 0, 10))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The color of the lines of the blocks being rendered.")
        .defaultValue(new SettingColor(0, 255, 0, 255))
        .build()
    );

    private int ticks;

    private final BlockPos.Mutable blockPos = new BlockPos.Mutable(0, -1, 0);
    private Direction direction;

    public PacketRightClick() {
        super(Categories.Player, "packet-rightclick", "Attempts to instantly rightclick blocks.");
    }

    @Override
    public void onActivate() {
        ticks = 0;
        blockPos.set(mc.player.getBlockX(), -1, mc.player.getBlockZ());
    }


    @EventHandler
    private void onGameLift(GameLeftEvent e) {
        if (autodisable.get()) this.toggle();
    }

    @EventHandler
    private void onStartBreakingBlock(StartBreakingBlockEvent event) {
        direction = event.direction;
        blockPos.set(event.blockPos);
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {

            if (ticks >= tickDelay.get()) {
                ticks = 0;

                if (shouldClick()) {
                    BlockHitResult blockHitResult = new BlockHitResult(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), direction, new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()),false);
                    if (silent.get() || blocks.get().asItem() != null) {
                        Item targetItem = blocks.get().asItem();
                        FindItemResult item = InvUtils.findInHotbar(targetItem);
                        if (!item.found()) return;
                        InvUtils.swap(item.slot(), true);
                    }
                    if (rotate.get()) {
                        Rotations.rotate(Rotations.getYaw(blockPos), Rotations.getPitch(blockPos), () -> mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, blockHitResult,0)));
                    } else {
                        mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, blockHitResult, 0));
                    }

                }
            } else {
                ticks++;
            }
        }

    private boolean shouldClick() {
        if (!block.get()) return true;
        if (blockPos.getY() != -1 || !block.get()) return true;
        return block.get() || mc.player.getMainHandStack().getItem() == blocks.get().asItem() || blockPos.getY() != -1;
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (!render.get() || !shouldClick()) return;
        event.renderer.box(blockPos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }
}
