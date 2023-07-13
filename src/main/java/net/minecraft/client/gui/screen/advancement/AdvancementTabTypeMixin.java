/*
package net.minecraft.client.gui.screen.advancement;

import meteordevelopment.meteorclient.advancement.AdvancementInfo;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementTabType.class)
public class AdvancementTabTypeMixin {

    @Inject(method="getTabX", at=@At("HEAD"), cancellable = true) 
    public void getAdjustedTabX(int index, CallbackInfoReturnable cir) {
        if ((Object)this == AdvancementTabType.RIGHT) {
            cir.setReturnValue(MinecraftClient.getInstance().currentScreen.width - AdvancementInfo.AI_spaceX*2 - 4);
            cir.cancel();
        }
    }

    @Inject(method="getTabY", at=@At("HEAD"), cancellable = true) 
    public void getAdjustedTabY(int index, CallbackInfoReturnable cir) {
        if ((Object)this == AdvancementTabType.BELOW) {
            cir.setReturnValue(MinecraftClient.getInstance().currentScreen.height - AdvancementInfo.AI_spaceY*2 - 4);
            cir.cancel();
        }
    }
}*/
