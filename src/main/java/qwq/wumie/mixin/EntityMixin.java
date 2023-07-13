package qwq.wumie.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qwq.wumie.systems.modules.movement.AntiPistonPush;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "getPistonBehavior", at = @At(value = "HEAD"), cancellable = true)
    protected void onAdjustMovementForPiston(CallbackInfoReturnable<PistonBehavior> info) {
        if (Modules.get().isActive(AntiPistonPush.class)) info.setReturnValue(PistonBehavior.IGNORE);
    }
}
