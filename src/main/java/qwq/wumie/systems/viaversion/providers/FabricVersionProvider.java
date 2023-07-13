package qwq.wumie.systems.viaversion.providers;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.ChannelPipeline;
import qwq.wumie.systems.handle.MainHandler;
import qwq.wumie.systems.viaversion.provider.AbstractFabricVersionProvider;
import qwq.wumie.systems.viaversion.service.ProtocolAutoDetector;
import net.minecraft.network.ClientConnection;
import qwq.wumie.systems.viaversion.config.VFConfig;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class FabricVersionProvider extends AbstractFabricVersionProvider {
    @Override
    protected Logger getLogger() {
        return MainHandler.JLOGGER;
    }

    @Override
    protected VFConfig getConfig() {
        return MainHandler.config;
    }

    @Override
    protected CompletableFuture<ProtocolVersion> detectVersion(InetSocketAddress address) {
        return ProtocolAutoDetector.detectVersion(address);
    }

    @Override
    protected boolean isMulticonnectHandler(ChannelPipeline pipe) {
        return pipe.get(ClientConnection.class).getPacketListener().getClass().getName().startsWith("net.earthcomputer.multiconnect");
    }
}
