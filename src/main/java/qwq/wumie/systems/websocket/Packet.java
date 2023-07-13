package qwq.wumie.systems.websocket;

public class Packet {
    public String name;
    public String action;

    public Packet(String name, String action) {
        this.name = name;
        this.action = action;
    }

    public void apply() {
    }

    public String toString() {
        return "Packet[name:" + name + ",action:" + action + "]";
    }
}
