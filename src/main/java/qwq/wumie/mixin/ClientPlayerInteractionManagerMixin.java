package qwq.wumie.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qwq.wumie.event.StopUsingItemEvent;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = "stopUsingItem", at = @At("HEAD"))
    public void onStopUsingItem(PlayerEntity player, CallbackInfo ci) {
        MeteorClient.EVENT_BUS.post(StopUsingItemEvent.get(player.getInventory().getMainHandStack()));
    }
}