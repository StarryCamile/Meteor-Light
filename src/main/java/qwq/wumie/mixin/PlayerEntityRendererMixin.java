/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qwq.wumie.systems.modules.player.OldHitting;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    /**
     * @author wumie
     * @reason {@link OldHitting} old hitting
     */
    @Inject(method = "getArmPose",at = @At("HEAD"), cancellable = true)
    private static void getArmPose(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        ItemStack itemStack = player.getStackInHand(hand);
        OldHitting oldHitting = Modules.get().get(OldHitting.class);

        if (!oldHitting.isActive()) return;

        if (itemStack.isEmpty()) {
            cir.setReturnValue(BipedEntityModel.ArmPose.EMPTY);
        } else {
            if (player.getActiveHand() == hand && player.getItemUseTimeLeft() > 0) {
                UseAction useAction = itemStack.getUseAction();
                if (useAction == UseAction.BLOCK) {
                    cir.setReturnValue( BipedEntityModel.ArmPose.BLOCK);
                }

                if (useAction == UseAction.BOW) {
                    cir.setReturnValue( BipedEntityModel.ArmPose.BOW_AND_ARROW);
                }

                if (useAction == UseAction.SPEAR) {
                    cir.setReturnValue( BipedEntityModel.ArmPose.THROW_SPEAR);
                }

                if (useAction == UseAction.CROSSBOW && hand == player.getActiveHand()) {
                    cir.setReturnValue( BipedEntityModel.ArmPose.CROSSBOW_CHARGE);
                }

                if (useAction == UseAction.SPYGLASS) {
                    cir.setReturnValue( BipedEntityModel.ArmPose.SPYGLASS);
                }

                if (useAction == UseAction.TOOT_HORN) {
                    cir.setReturnValue( BipedEntityModel.ArmPose.TOOT_HORN);
                }
            } else if (!player.handSwinging && itemStack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(itemStack)) {
                cir.setReturnValue( BipedEntityModel.ArmPose.CROSSBOW_HOLD);
            } else if (oldHitting.isOldHit(itemStack.getItem(), player)) {
                cir.setReturnValue( BipedEntityModel.ArmPose.BLOCK);
            }

            cir.setReturnValue( (oldHitting.isOldHit(itemStack.getItem(), player)) ? BipedEntityModel.ArmPose.BLOCK : BipedEntityModel.ArmPose.ITEM);
        }
    }
}
