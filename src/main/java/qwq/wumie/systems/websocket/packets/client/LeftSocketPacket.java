package qwq.wumie.systems.websocket.packets.client;

import qwq.wumie.systems.websocket.Packet;
import qwq.wumie.systems.websocket.SocketLaunch;
import qwq.wumie.systems.websocket.server.SocketServer;

public class LeftSocketPacket extends Packet {
    public LeftSocketPacket(String id) {
        super("left", id);
    }

    @Override
    public void apply() {
        SocketServer server = SocketLaunch.mainServer;
        SocketLaunch.info("ID为: " + action + " 的断开连接.");
        server.ids.removeIf(s -> s.equals(action));
        super.apply();
    }
}
