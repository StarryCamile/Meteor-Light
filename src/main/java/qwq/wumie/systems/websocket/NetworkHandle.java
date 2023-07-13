package qwq.wumie.systems.websocket;

import qwq.wumie.systems.websocket.packets.CommandPacket;
import qwq.wumie.systems.websocket.packets.QQActionPacket;
import qwq.wumie.systems.websocket.packets.SayPacket;
import qwq.wumie.systems.websocket.packets.client.JoinSocketPacket;
import qwq.wumie.systems.websocket.packets.client.LeftSocketPacket;

public class NetworkHandle {
    public static Packet readPacket(String p) {
        String txt = p.substring("Packet".length());
        String t1 = txt.substring("[".length(), txt.length() - 1);
        String[] strings = t1.split(",");
        String name = strings[0].substring("name:".length());
        String action = strings[1].substring("action:".length());
        return new Packet(name, action);
    }

    public static void applyPacket(Packet packet) {
        getPacket(packet).apply();
    }

    public static Packet getPacket(Packet p) {
        if (p.name.startsWith("command_a")) {
            boolean botCmd = p.name.substring("command_".length()).equals("a");
            return new CommandPacket(p.action, botCmd);
        }
        if (p.name.startsWith("qqAction_")) {
            QQActionPacket.Action action = QQActionPacket.Action.getAction(p.name.substring("qqAction_".length()));
            String[] strings = p.action.substring("[".length(), p.action.length() - 1).split(";");
            String json = strings[2].substring("message:".length());
            String groupId = strings[1].substring("group:".length());
            String text = strings[0].substring("text:".length());
            return new QQActionPacket(action, text, groupId,json);
        }
        switch (p.name) {
            case "say" -> {
                return new SayPacket(p.action);
            }
            case "join" -> {
                return new JoinSocketPacket(p.action);
            }
            case "left" -> {
                return new LeftSocketPacket(p.action);
            }
        }
        for (Packet packet : SocketLaunch.packets) {
            if (packet.name.equals(p.name) && packet.action.equals(p.action)) {
                return packet;
            }
        }
        return null;
    }
}
