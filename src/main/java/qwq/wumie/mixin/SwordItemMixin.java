package qwq.wumie.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import qwq.wumie.systems.modules.player.OldHitting;
import net.minecraft.item.*;
import net.minecraft.util.UseAction;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SwordItem.class)
public class SwordItemMixin extends ToolItem implements Vanishable {
    public SwordItemMixin(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        OldHitting oldHitting = Modules.get().get(OldHitting.class);
        return oldHitting.isActive() ? UseAction.BLOCK : super.getUseAction(stack);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        OldHitting oldHitting = Modules.get().get(OldHitting.class);
        return oldHitting.isActive() ? 72000 : super.getMaxUseTime(stack);
    }
}
