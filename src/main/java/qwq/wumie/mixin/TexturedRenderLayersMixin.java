package qwq.wumie.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.render.TexturedRenderLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import qwq.wumie.systems.modules.render.Rendering;

@Mixin(TexturedRenderLayers.class)
public class TexturedRenderLayersMixin {
    @ModifyVariable(method = "getChestTextureId(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/block/enums/ChestType;Z)Lnet/minecraft/client/util/SpriteIdentifier;", at = @At("LOAD"), ordinal = 0, argsOnly = true)
    private static boolean chrsitmas(boolean christmas) {
        Rendering rendering = Modules.get().get(Rendering.class);
        if (rendering != null && rendering.chistmas())
            return true;
        return christmas;
    }
}
