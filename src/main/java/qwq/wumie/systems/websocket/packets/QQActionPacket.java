package qwq.wumie.systems.websocket.packets;

import qwq.wumie.systems.commands.commands.Check.GsonUtils;
import meteordevelopment.meteorclient.systems.modules.Modules;
import qwq.wumie.systems.modules.misc.Socket;
import qwq.wumie.systems.websocket.Packet;
import qwq.wumie.systems.websocket.SocketLaunch;
import qwq.wumie.systems.websocket.server.GMessage;

public class QQActionPacket extends Packet {
    public Action action;
    public String group_id;
    public String text;
    public GMessage message;

    public QQActionPacket(Action action, String text, String group_id, String message) {
        super("qqAction_" + action.toString(), "[text:" + text + ";group:" + group_id + ";message:"+message+"]");
        this.action = action;
        this.group_id = group_id;
        this.text = text;
        GMessage gm = GsonUtils.jsonToBean(message,GMessage.class);
        this.message = gm;
    }

    public enum Action {
        message;

        public static Action getAction(String name) {
            for (Action a : values()) {
                if (a.toString().equals(name)) {
                    return a;
                }
            }
            return Action.message;
        }
    }

    @Override
    public void apply() {
        switch (action) {
            case message: {
                Socket socket = Modules.get().get(Socket.class);
                if (!socket.qqMessage()) return;
                String name = message.getSender().getCard().isEmpty() ? message.getSender().getNickname() : message.getSender().getCard();
                SocketLaunch.info("[QQ] ("+group_id+") "+name +" -> "+message.getRaw_message());
            }
        }

        super.apply();
    }
}
