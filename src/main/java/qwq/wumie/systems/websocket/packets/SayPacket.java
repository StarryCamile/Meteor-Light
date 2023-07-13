package qwq.wumie.systems.websocket.packets;

import qwq.wumie.systems.websocket.Packet;
import meteordevelopment.meteorclient.utils.player.ChatUtils;

public class SayPacket extends Packet {
    public SayPacket(String message) {
        super("say", message);
    }

    @Override
    public void apply() {
        ChatUtils.sendPlayerMsg(action);
        super.apply();
    }
}
