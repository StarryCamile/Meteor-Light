package qwq.wumie.version.meteor051;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import qwq.wumie.systems.handle.MainHandler;
import qwq.wumie.systems.modules.inject.Injects;
import qwq.wumie.systems.modules.inject.ModuleInject;
import qwq.wumie.systems.notification.NotificationManager;
import qwq.wumie.utils.misc.NotificationUtil;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.random.Random;
import qwq.wumie.systems.sound.MeteorSoundManager;

import java.io.File;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ModulePlus {
    public final MeteorSoundManager soundManager = new MeteorSoundManager();
    public final NotificationUtil notification = NotificationUtil.instance;
    public final RenderUtils gameRender = RenderUtils.instance;
    public final File moduleFolder = new File(MeteorClient.FOLDER,"module-file");
    public Random soundRandom = SoundInstance.createRandom();

    public void sendChatMessage(String message) {
        ChatUtils.sendPlayerMsg(message);
    }

    public void sendPacket(Packet packet) {
        mc.getNetworkHandler().sendPacket(packet);
    }

    public void attackEntity(Entity entity) {
        assert mc.interactionManager != null;
        mc.interactionManager.attackEntity(mc.player,entity);
    }

    public void offModule(Module module) {
        NotificationManager notificationManager = NotificationManager.INSTANCE;

        if (Config.get().toggleSound.get()) soundManager.disableSound.play();

        if (mc.world != null && notificationManager != null) {
            notificationManager.success("Module","Toggled " +module.title+ " off.");
        }
    }

    public void enableModule(Module module) {
        NotificationManager notificationManager = NotificationManager.INSTANCE;

        if (Config.get().toggleSound.get()) soundManager.enableSound.play();

        if (mc.world != null && notificationManager != null) {
            notificationManager.success("Module","Toggled " +module.title+ " on.");
        }
    }
}
