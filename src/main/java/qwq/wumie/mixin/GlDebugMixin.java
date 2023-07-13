/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.mixin;

import meteordevelopment.meteorclient.systems.config.Config;
import net.minecraft.client.gl.GlDebug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(GlDebug.class)
public class GlDebugMixin {
    @Inject(method = "info",at = @At(value = "HEAD"), cancellable = true)
    private static void info(int source, int type, int id, int severity, int messageLength, long message, long l, CallbackInfo ci) {
        if (Config.get() != null) {
            if (Config.get().disableGLDEBUG.get()) ci.cancel();
        }
    }
    @Inject(method = "collectDebugMessages",at = @At("HEAD"))
    private static void collectDebugMessages(CallbackInfoReturnable<List<String>> cir) {
        if (Config.get() != null) {
            if (Config.get().disableGLDEBUG.get()) cir.cancel();
        }
    }
    @Inject(method = "enableDebug",at = @At("HEAD"), cancellable = true)
    private static void enableDebug(int verbosity, boolean sync, CallbackInfo ci) {
        if (Config.get() != null) {
            if (Config.get().disableGLDEBUG.get()) ci.cancel();
        }
    }
}
