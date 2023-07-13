package qwq.wumie.systems.websocket.packets.client;

import qwq.wumie.systems.websocket.Packet;

public class KeepAlivePacket extends Packet {
    public KeepAlivePacket() {
        super("keepalive", "null");
    }
}
