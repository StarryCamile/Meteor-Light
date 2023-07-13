/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.systems.modules.player;

import de.gerrygames.viarewind.ViaFabricAddon;
import meteordevelopment.meteorclient.events.render.HeldItemRendererEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class OldHitting extends Module {
    public OldHitting() {
        super(Categories.Player, "old-hitting", "1.8 combat!");
    }

    public int swingSpeed = 8;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<RenderMode> animationMode = sgGeneral.add(new EnumSetting.Builder<RenderMode>()
        .name("animation-mode")
        .defaultValue(RenderMode.Vanilla)
        .build()
    );

    private final Setting<Boolean> slowEffect = sgGeneral.add(new BoolSetting.Builder()
            .name("slow-effect")
            .description("make you slow")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> dShield = sgGeneral.add(new BoolSetting.Builder()
            .name("d-Shield")
            .description("shield on off hand")
            .defaultValue(false)
            .build()
    );

    @Override
    public void onActivate() {
        super.onActivate();
    }

    @EventHandler
    private void onTick(TickEvent.Pre e) {
        if (isOldHit(mc.player.getMainHandStack().getItem()) && slowEffect.get()) {
            mc.player.setCurrentHand(Hand.MAIN_HAND);
        }
    }

    @EventHandler
    private void onRenderItem(HeldItemRendererEvent event) {
        MatrixStack matrices = event.matrix;
        Arm arm = mc.player.getMainArm();
        float f = 1.0F - (event.prevEquipProgressMainHand + (event.equipProgressMainHand - event.prevEquipProgressMainHand) * event.tickDelta);
        float f1 = mc.player.getHandSwingProgress(event.tickDelta);
        if (isOldHit(mc.player.getMainHandStack().getItem())) {
            switch (animationMode.get()) {
                case Jello -> {
                    this.doBlockTransformations(matrices);
                    final int alpha = (int)Math.min(255L, ((System.currentTimeMillis() % 255L > 127L) ? Math.abs(Math.abs(System.currentTimeMillis()) % 255L - 255L) : (System.currentTimeMillis() % 255L)) * 2L);
                    translate(matrices,0.3f, -0.0f, 0.4f);
                    rotate(matrices,0.0f, 0.0f, 0.0f, 1.0f);
                    translate(matrices,0.0f, 0.5f, 0.0f);
                    rotate(matrices,90.0f, 1.0f, 0.0f, -1.0f);
                    translate(matrices,0.6f, 0.5f, 0.0f);
                    rotate(matrices,-90.0f, 1.0f, 0.0f, -1.0f);
                    rotate(matrices,-10.0f, 1.0f, 0.0f, -1.0f);
                    rotate(matrices,mc.player.isUsingItem() ? (-alpha / 5.0f) : 1.0f, 1.0f, -0.0f, 1.0f);
                }
                case Swong -> {
                    event.applyEquipOffset(matrices,arm,f / 2.0F);
                    rotate(matrices,-MathHelper.sin(MathHelper.sqrt(f1) * 3.1415927F) * 40.0F / 2.0F, MathHelper.sqrt(f1) / 2.0F, -0.0F, 9.0F);
                    rotate(matrices,-MathHelper.sqrt(f1) * 30.0F, 1.0F, MathHelper.sqrt(f1) / 2.0F, -0.0F);
                    doBlockTransformations(matrices);
                }
                case Vanilla -> {
                doBlockTransformations(matrices);
                this.swingSpeed = 8;
                }
                case Version1_7 -> {
                    event.transformFirstPersonItem(matrices,arm,f, f1);
                    matrices.translate(0, 0.3, 0);
                    doBlockTransformations(matrices);
                }
                case Rotate -> {
                    translate(matrices,0.56F, -0.52F, -0.71999997F);
                    translate(matrices,0.0F, 0f * -0.6F, 0.0F);
                    rotate(matrices,45.0F, 0.0F, 1.0F, 0.0F);
                    float var3 = MathHelper.sin(0f * 0f * 3.1415927F);
                    float var4 = MathHelper.sin(MathHelper.sqrt(0f) * 3.1415927F);
                    rotate(matrices, var3 * -34.0F, 0.0F, 1.0F, 0.2F);
                    rotate(matrices,var4 * -20.7F, 0.2F, 0.1F, 1.0F);
                    rotate(matrices,var4 * -68.6F, 1.3F, 0.1F, 0.2F);
                    matrices.scale(0.4F, 0.4F, 0.4F);
                    doBlockTransformations(matrices);
                    translate(matrices,-0.5F, 0.2F, 0.0F);
                    rotate(matrices,MathHelper.sqrt(f1) * 10.0F * 40.0F, 1.0F, -0.0F, 2.0F);
                }
            }
        }
    }

    public void doBlockTransformations(MatrixStack matrices) {
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-102f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(30f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(82.5f));
    }

    public void rotate(MatrixStack matrices,float angle, float x, float y, float z) {
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(angle*x));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle*y));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(angle*z));
    }

    public void translate(MatrixStack matrices, float x, float y, float z) {
        matrices.translate(x,y,z);
    }

    public enum RenderMode {
        Vanilla,
        Jello,
        Swong,
        Rotate,
        Version1_7;

        @Override
        public String toString() {
            return this == Version1_7 ? "1.7" : super.toString();
        }
    }

    public boolean isOldHit(Item item) {
        return (item instanceof SwordItem && (mc.options.useKey.isPressed()) && isActive());
    }

    public boolean isOldHit(Item item,AbstractClientPlayerEntity player) {
        boolean a = player.getStackInHand(Hand.OFF_HAND).getItem() instanceof ShieldItem;
        return (item instanceof SwordItem && (dShield.get() ? a : player.isUsingItem()) && isActive());
    }
}
