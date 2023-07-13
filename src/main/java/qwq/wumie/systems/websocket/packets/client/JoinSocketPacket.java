package qwq.wumie.systems.websocket.packets.client;

import qwq.wumie.systems.websocket.Packet;
import qwq.wumie.systems.websocket.SocketLaunch;
import qwq.wumie.systems.websocket.server.SocketServer;

public class JoinSocketPacket extends Packet {
    public JoinSocketPacket(String id) {
        super("join", id);
    }

    @Override
    public void apply() {
        SocketServer server = SocketLaunch.mainServer;
        SocketLaunch.info("ID为: " + action + " 的加入了Socket.");
        server.ids.add(action);
        super.apply();
    }
}
