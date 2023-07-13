/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qwq.wumie.systems.modules.render.BobView;
import qwq.wumie.systems.modules.render.Rendering;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final
    MinecraftClient client;

    @Shadow public abstract void reset();

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawEntityOutlinesFramebuffer()V", ordinal = 0))
    private void renderShader(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        Rendering renderingModule = Modules.get().get(Rendering.class);
        if (renderingModule == null) return;
        PostEffectProcessor shader = renderingModule.getShaderEffect();

        if (shader != null) {
            shader.setupDimensions(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight());
            shader.render(tickDelta);
        }
    }

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void onBobView(MatrixStack matrices, float delta, CallbackInfo info) {
        if (Modules.get() != null && Modules.get().isActive(BobView.class)) {
            BobView module = Modules.get().get(BobView.class);

            if (!module.shouldDisable() && client.getCameraEntity() instanceof PlayerEntity player) {
                float normal = -(player.horizontalSpeed * module.getSpeed() + (player.horizontalSpeed * module.getSpeed() - player.prevHorizontalSpeed * module.getSpeed()) * delta);
                float stride = MathHelper.lerp(delta, player.prevStrideDistance * module.getSpeed(), player.strideDistance * module.getSpeed());

                matrices.translate((MathHelper.sin(normal * 3.1415927F) * stride * module.getHorizontal()), (-Math.abs(MathHelper.cos(normal * 3.1415927F) * stride)), 0.0F);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(normal * 3.1415927F) * stride * module.getRotate()));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Math.abs(MathHelper.cos(normal * 3.1415927F - module.getShake()) * stride) * module.getVertical()));
            }

            info.cancel();
        }
    }
}
