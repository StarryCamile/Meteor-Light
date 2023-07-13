/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.mixin;

import meteordevelopment.meteorclient.mixininterface.IPlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.*;
import qwq.wumie.mixininterface.FPlayerMoveC2SPacket;

@Mixin(PlayerMoveC2SPacket.class)
public class PlayerMoveC2SPacketMixin implements FPlayerMoveC2SPacket {
    @Mutable
    @Shadow
    @Final
    protected double x;
    @Mutable
    @Shadow
    @Final
    protected double y;
    @Mutable
    @Shadow
    @Final
    protected double z;
    @Mutable
    @Shadow
    @Final
    protected float yaw;
    @Mutable
    @Shadow
    @Final
    protected float pitch;
    @Mutable
    @Shadow @Final protected boolean onGround;
    @Unique
    private int tag;

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public void setYaw(float y) {
        this.yaw = y;
    }

    @Override
    public void setPitch(float p) {
        this.pitch = p;
    }

    @Override
    public void setOnGround(boolean ground) {
        this.onGround = ground;
    }
}
