/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qwq.wumie.systems.modules.player.OldHitting;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract boolean isUsingItem();

    @Shadow protected ItemStack activeItemStack;

    @Shadow protected int itemUseTimeLeft;

    /**
     * @author wumie
     * @reason old hitting
     */
    @Inject(method = "isBlocking",at = @At("HEAD"), cancellable = true)
    private void isBlocking(CallbackInfoReturnable<Boolean> cir) {
        OldHitting oldHitting = Modules.get().get(OldHitting.class);
        if (oldHitting.isActive()) {
            if (this.isUsingItem() && !this.activeItemStack.isEmpty()) {
                Item item = this.activeItemStack.getItem();

                if (item.getUseAction(this.activeItemStack) == UseAction.BLOCK) {
                    cir.setReturnValue(item.getMaxUseTime(this.activeItemStack) - this.itemUseTimeLeft >= 5);
                } else {
                    cir.setReturnValue(false);
                }
            } else {
                cir.setReturnValue(false);
            }
        }
    }
}
