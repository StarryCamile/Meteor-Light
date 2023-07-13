/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.mixin.via;

import com.mojang.blaze3d.systems.RenderSystem;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import meteordevelopment.meteorclient.utils.misc.MeteorIdentifier;
import net.minecraft.client.gui.DrawContext;
import qwq.wumie.systems.handle.Config;
import qwq.wumie.systems.viaversion.gui.ViaServerInfo;
import qwq.wumie.utils.world.seeds.Seeds;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = MultiplayerServerListWidget.ServerEntry.class,priority = 666)
public class MixinServerEntry {
    @Shadow
    @Final
    private ServerInfo server;

    private static final Identifier GUI_ICONS_TEXTURES = new Identifier("textures/gui/icons.png");

    @Redirect(method = "render", at = @At(value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIFFIIII)V"))
    private void redirectPingIcon(DrawContext instance, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        if (Config.enableViaVersion && texture.equals(GUI_ICONS_TEXTURES) && ((ViaServerInfo) this.server).isViaTranslating()) {
            instance.drawTexture(new MeteorIdentifier("textures/via/icons.png"), x, y, u, v, width, height, textureWidth, textureHeight);
            return;
        }
        instance.drawTexture(texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;setMultiplayerScreenTooltip(Ljava/util/List;)V"))
    private void addServerVer(MultiplayerScreen multiplayerScreen, List<Text> tooltipText) {
        List<Text> lines = new ArrayList<>(tooltipText);
        if (Config.enableViaVersion) {
            ProtocolVersion proto = ProtocolVersion.getProtocol(((ViaServerInfo) this.server).getViaServerVer());
            lines.add(Text.translatable("VIA: %s", proto.getName()));
        }
        lines.add(Text.literal("Seed:"+ (Seeds.get().getSeedForIP(server.address) == null ? "Unknown" : Seeds.get().getSeedForIP(server.address).seed.toString())));
        multiplayerScreen.setMultiplayerScreenTooltip(lines);
    }
}
