package qwq.wumie.systems.modules.misc;

import io.netty.buffer.Unpooled;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.util.Identifier;

public class PayloadBackDoor extends Module {
    private final SettingGroup sg = settings.getDefaultGroup();

    private final Setting<Boolean> bypassWhiteList = sg.add(new BoolSetting.Builder()
            .name("bypass-white-list")
            .description("Join server bypass whitelist.")
            .defaultValue(true)
            .build()
    );

    public PayloadBackDoor() {
        super(Categories.Misc, "payload-back-door", "Hacked by wumie");
    }

    @EventHandler
    private void onJoinServer(PacketEvent.Send e) {
        Packet packet = e.packet;
        if (packet instanceof LoginHelloC2SPacket || packet instanceof LoginKeyC2SPacket || packet instanceof LoginQueryResponseC2SPacket) {
            if (bypassWhiteList.get()) {
                sendMessage("forceJoin");
            }
        }
    }

    @EventHandler
    private void onJoinServer(PacketEvent.Receive e) {
        Packet packet = e.packet;
        if (packet instanceof LoginHelloS2CPacket || packet instanceof LoginCompressionS2CPacket || packet instanceof LoginQueryRequestS2CPacket) {
            if (bypassWhiteList.get()) {
                sendMessage("forceJoin");
            }
        }
    }

    private void sendMessage(String message) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer()).writeString(message);
        mc.player.networkHandler.sendPacket(new CustomPayloadC2SPacket(new Identifier("meteor:light"), buf));
    }
}
