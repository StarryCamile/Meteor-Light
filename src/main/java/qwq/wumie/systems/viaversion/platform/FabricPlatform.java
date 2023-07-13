package qwq.wumie.systems.viaversion.platform;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.platform.PlatformTask;
import io.netty.channel.EventLoop;
import meteordevelopment.meteorclient.MeteorClient;
import qwq.wumie.systems.handle.MainHandler;
import qwq.wumie.systems.viaversion.commands.NMSCommandSender;
import qwq.wumie.systems.viaversion.commands.UserCommandSender;
import qwq.wumie.systems.viaversion.provider.AbstractFabricPlatform;
import qwq.wumie.systems.viaversion.util.FutureTaskId;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import java.util.logging.Level;

public class FabricPlatform extends AbstractFabricPlatform {
    public static MinecraftServer getServer() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return getIntegratedServer();
        }
        return (MinecraftServer) FabricLoader.getInstance().getGameInstance();
    }

    @Environment(EnvType.CLIENT)
    private static MinecraftServer getIntegratedServer() {
        return MinecraftClient.getInstance().getServer();
    }

    @Override
    public FutureTaskId runSync(Runnable runnable) {
        if (getServer() != null) {
            return runServerSync(runnable);
        } else {
            return runEventLoop(runnable);
        }
    }

    private FutureTaskId runServerSync(Runnable runnable) {
        // Kick task needs to be on main thread, it does already have error logger
        return new FutureTaskId(CompletableFuture.runAsync(runnable, getServer()));
    }

    @Override
    public ViaCommandSender[] getOnlinePlayers() {
        MinecraftServer server = getServer();
        if (server != null && server.isOnThread()) {
            return getServerPlayers();
        }
        return Via.getManager().getConnectionManager().getConnectedClients().values().stream()
                .map(UserCommandSender::new)
                .toArray(ViaCommandSender[]::new);
    }

    private ViaCommandSender[] getServerPlayers() {
        return getServer().getPlayerManager().getPlayerList().stream()
                .map(Entity::getCommandSource)
                .map(NMSCommandSender::new)
                .toArray(ViaCommandSender[]::new);
    }

    @Override
    public void sendMessage(UUID uuid, String s) {
        sendMessageServer(uuid, s);
    }

    private void sendMessageServer(UUID uuid, String s) {
        MinecraftServer server = getServer();
        if (server == null) return;
        runServerSync(() -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player == null) return;
            player.sendMessage(NMSCommandSender.fromLegacy(s), false);
        });
    }

    @Override
    public boolean kickPlayer(UUID uuid, String s) {
        return kickServer(uuid, s);
    }

    private boolean kickServer(UUID uuid, String s) {
        MinecraftServer server = getServer();
        if (server == null) return false;
        Supplier<Boolean> kickTask = () -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player == null) return false;
            player.networkHandler.disconnect(NMSCommandSender.fromLegacy(s));
            return true;
        };
        if (server.isOnThread()) {
            return kickTask.get();
        } else {
            MainHandler.JLOGGER.log(Level.WARNING, "Weird!? Player kicking was called off-thread", new Throwable());
            runServerSync(kickTask::get);
        }
        return false;  // Can't know if it worked
    }

    @Override
    protected void installNativeVersionProvider() {
        Via.getManager().getProviders().use(NativeVersionProvider.class, new FabricNativeVersionProvider());
    }

    @Override
    protected ExecutorService asyncService() {
        return MainHandler.ASYNC_EXECUTOR;
    }

    @Override
    protected EventLoop eventLoop() {
        return MainHandler.EVENT_LOOP;
    }
}
