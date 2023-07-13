package qwq.wumie.systems.handle;

import com.google.common.collect.Range;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.events.world.UpdateEvent;
import meteordevelopment.meteorclient.renderer.SMesh;
import meteordevelopment.meteorclient.systems.config.SeedConfigs;
import meteordevelopment.meteorclient.utils.Utils;
import qwq.wumie.systems.notification.NotificationManager;
import qwq.wumie.utils.VectorUtils;
import qwq.wumie.utils.world.seeds.Seeds;
import meteordevelopment.orbit.EventHandler;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import qwq.wumie.systems.music.MusicManager;
import qwq.wumie.systems.viaversion.commands.VRCommandHandler;
import qwq.wumie.systems.viaversion.config.VFConfig;
import qwq.wumie.systems.viaversion.platform.FabricInjector;
import qwq.wumie.systems.viaversion.platform.FabricPlatform;
import qwq.wumie.systems.viaversion.platform.VFLoader;
import qwq.wumie.systems.viaversion.protocol.HostnameParserProtocol;
import qwq.wumie.systems.viaversion.util.JLoggerToLog4j;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static meteordevelopment.meteorclient.MeteorClient.EVENT_BUS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class MainHandler {
    // ViaVersion
    public static final CompletableFuture<Void> INIT_FUTURE = new CompletableFuture<>();
    public static final java.util.logging.Logger JLOGGER = new JLoggerToLog4j(LogManager.getLogger("Meteor-Via"));
    public static final ExecutorService ASYNC_EXECUTOR;
    public static final EventLoop EVENT_LOOP;
    static {
        ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Meteor-Via-%d").build();
        ASYNC_EXECUTOR = Executors.newFixedThreadPool(8, factory);
        EVENT_LOOP = new DefaultEventLoop(factory);
        EVENT_LOOP.submit(INIT_FUTURE::join);
    }
    private static void initViaVersion() {
        FabricPlatform platform = new FabricPlatform();

        Via.init(ViaManagerImpl.builder()
                .injector(new FabricInjector())
                .loader(new VFLoader())
                .commandHandler(new VRCommandHandler())
                .platform(platform).build());

        platform.init();

        ViaManagerImpl manager = (ViaManagerImpl) Via.getManager();
        manager.init();

        Via.getManager().getProtocolManager().registerBaseProtocol(HostnameParserProtocol.INSTANCE, Range.lessThan(Integer.MIN_VALUE));
        ProtocolVersion.register(-2, "AUTO");

        FabricLoader.getInstance().getEntrypoints("viafabric:via_api_initialized", Runnable.class).forEach(Runnable::run);

        File viaFolder = new File(MeteorClient.FOLDER,"via-version");
        config = new VFConfig(new File(viaFolder, "meteor-via.yml"));

        manager.onServerLoaded();

        INIT_FUTURE.complete(null);
    }

    public static VFConfig config;
    private static MainHandler INSTANCE;
    public static MainHandler getInstance() {
        return INSTANCE;
    }
    public static MusicManager music = new MusicManager();
    private static MeteorClient client;
    public static MeteorAddon ADDON;
    public static SMesh.BMesh dMesh;
    public static final MusicManager musicManager = new MusicManager();
    public static NotificationManager notificationManager;

    public MainHandler() {
        INSTANCE = this;
    }

    public void preLoad() {
        if (INSTANCE == null) {
            INSTANCE = this;
        }
        EVENT_BUS.registerLambdaFactory("" , (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
        Config.load();
    }

    public static void initClient(MeteorClient c) {
        client = c;
        EVENT_BUS.registerLambdaFactory("qwq.wumie" , (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
        EVENT_BUS.registerLambdaFactory("qwq" , (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
        VectorUtils.init();
        if (Config.enableViaVersion) {
            initViaVersion();
        }

        EVENT_BUS.subscribe(INSTANCE);
        notificationManager = new NotificationManager();
        SMesh.load();
        SMesh.glLoadIdentity();
        dMesh = SMesh.getbMesh();
        Seeds.get().setSeeds(SeedConfigs.load());
        VectorUtils.postInit();

        try {
            musicManager.start();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load Music Manager");
        }
    }

    public static void onStop() {
        music.stop();
        SeedConfigs.save();
    }

    @EventHandler
    private void onTickPre(TickEvent.Pre event) {
        if (Utils.canUpdate()) {
            EVENT_BUS.post(UpdateEvent.get(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
            EVENT_BUS.post(UpdateEvent.Pre.get(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
        }
    }

    @EventHandler
    private void onTickPre(TickEvent.Post event) {
        if (Utils.canUpdate()) {
            EVENT_BUS.post(UpdateEvent.get(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
            EVENT_BUS.post(UpdateEvent.Post.get(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
        }
    }
}
