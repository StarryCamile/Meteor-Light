/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2022 Meteor Development.
 */

package qwq.wumie.systems.modules.movement;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import qwq.wumie.utils.time.TickTimer;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Phase extends Module {
    public int delay = 0;
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mod")
        .description("vanilla  ground")
        .defaultValue(Mode.Vanilla)
        .build()
    );

    public Phase() {
        super(Categories.Movement,"Phase","go through walls like a ghost");
    }

    private TickTimer tickTimer = new TickTimer();

    public enum Mode {
        TP,
        Vanilla,
        Ground,
        NoClip;
    }

    @EventHandler
    private void onUpdate(TickEvent.Pre event) {
        boolean isInsideBlock = BlockUtils.collideBlockIntersects(mc.player.getBoundingBox(),(block) -> !(block instanceof AirBlock));
        if (mode.get().equals(Mode.Vanilla) || mode.get().equals(Mode.NoClip)) {
            mc.player.noClip = true;
            mc.player.getVelocity().multiply(1, 0, 1);
        }
        if (mode.get().equals(Mode.Vanilla)) {
            tickTimer.update();
            if (!mc.player.isOnGround() || !tickTimer.hasTimePassed(2) || !mc.player.horizontalCollision || !(!isInsideBlock || mc.player.isSneaking()))
                return;
            float yaw = (float) Math.toRadians(mc.player.getYaw());
            double x = -sin(yaw) * 0.04;
            double z = cos(yaw) * 0.04;
            mc.player.updatePosition(mc.player.getX() + x, mc.player.getY(), mc.player.getZ() + z);
            tickTimer.reset();
            /*
            if (!mc.player.isOnGround() || !tickTimer.hasTimePassed(2) || !mc.player.horizontalCollision || !(!isInsideBlock || mc.player.isSneaking())) return;
            var direction = PlayerUtils.getDirection();
            var posX = -sin(direction) * 0.3;
            var posZ = cos(direction) * 0.3;
            for (int i = 0;i < 3;i++) {
                sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 0.06, mc.player.getZ(), true));
                sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX() + posX * i, mc.player.getY(), mc.player.getZ() + posZ * i, true));
            }
            mc.player.setBoundingBox(mc.player.getBoundingBox().offset(posX, 0.0, posZ));
            mc.player.updatePosition(mc.player.getX() + posX, mc.player.getY(), mc.player.getZ() + posZ);
            tickTimer.reset();*/
        }
    }

    @EventHandler
    public void onTick(TickEvent.Pre e) {
        assert mc.player != null;
        assert mc.world != null;

        if (mode.get().equals(Mode.Ground) && delay == 0) {
            if (mc.options.sneakKey.isPressed() && mc.player.verticalCollision) {
                for (int i = mc.player.getBlockY() - 1; i > 0; i--) {
                    BlockState bs1 = mc.world.getBlockState(mc.player.getBlockPos().subtract(new Vec3i(0, mc.player.getBlockY() - i, 0)));
                    BlockState bs2 = mc.world.getBlockState(mc.player.getBlockPos().subtract(new Vec3i(0, mc.player.getBlockY() - i - 1, 0)));
                    if (!bs1.blocksMovement() && !bs2.blocksMovement() && bs1.getBlock() != Blocks.LAVA && bs2.getBlock() != Blocks.LAVA) {
                        mc.player.updatePosition(mc.player.getX(), i, mc.player.getZ());
                        break;
                    }
                }
                delay = 20;
            }
        }

        if (mode.get().equals(Mode.TP) && mc.player.horizontalCollision && mc.options.sneakKey.isPressed()) {
            Vec3i v31 = mc.player.getMovementDirection().getVector();
            Vec3d v3 = new Vec3d(v31.getX(), 0, v31.getZ());
            for (double o = 2; o < 100; o++) {
                Vec3d coff = v3.multiply(o);
                BlockPos cpos = mc.player.getBlockPos().add(BlockPos.ofFloored(new Vec3d(coff.x, coff.y, coff.z)));
                BlockState bs1 = mc.world.getBlockState(cpos);
                BlockState bs2 = mc.world.getBlockState(cpos.up());
                if (!bs1.blocksMovement() && !bs2.blocksMovement() && bs1.getBlock() != Blocks.LAVA && bs2.getBlock() != Blocks.LAVA) {
                    mc.player.updatePosition(cpos.getX() + 0.5, cpos.getY(), cpos.getZ() + 0.5);
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), false));
                    break;
                }
            }
        }

        if (delay > 0) {
            delay--;
        }
    }
}
