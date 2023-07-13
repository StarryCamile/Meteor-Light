/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package qwq.wumie.mixin.via;

import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qwq.wumie.systems.handle.Config;
import qwq.wumie.systems.viaversion.gui.ViaServerInfo;
import qwq.wumie.systems.viaversion.handler.FabricDecodeHandler;

@Mixin(targets = "net.minecraft.client.network.MultiplayerServerListPinger$1",priority = 666)
public abstract class MixinMultiplayerServerListPingerListener implements ClientQueryPacketListener {
    @Accessor
    abstract ClientConnection getField_3774(); // Synthetic

    @Accessor
    abstract ServerInfo getField_3776(); // Synthetic

    @Inject(method = "onResponse(Lnet/minecraft/network/packet/s2c/query/QueryResponseS2CPacket;)V", at = @At(value = "HEAD"))
    private void onResponseCaptureServerInfo(QueryResponseS2CPacket packet, CallbackInfo ci) {
        if (!Config.enableViaVersion) return;

        FabricDecodeHandler decoder = ((MixinClientConnectionAccessor) this.getField_3774()).getChannel()
                .pipeline().get(FabricDecodeHandler.class);
        if (decoder != null) {
            ((ViaServerInfo) getField_3776()).setViaTranslating(decoder.getInfo().isActive());
            ((ViaServerInfo) getField_3776()).setViaServerVer(decoder.getInfo().getProtocolInfo().getServerProtocolVersion());
        }
    }
}