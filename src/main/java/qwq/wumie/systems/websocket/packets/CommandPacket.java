package qwq.wumie.systems.websocket.packets;

import qwq.wumie.systems.websocket.Packet;
import meteordevelopment.meteorclient.utils.player.ChatUtils;

public class CommandPacket extends Packet {
    public CommandPacket(String command, boolean botCmd) {
        super("command_" + (botCmd ? "a" : "b"), command);
    }

    @Override
    public void apply() {
        boolean botCmd = name.substring("command_".length()).equals("a");
        ChatUtils.sendPlayerMsg(action);
        super.apply();
    }
}
