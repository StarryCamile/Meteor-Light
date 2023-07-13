package qwq.wumie.systems.websocket;

import qwq.wumie.systems.websocket.client.SocketClient;
import qwq.wumie.systems.websocket.packets.client.KeepAlivePacket;
import qwq.wumie.systems.websocket.server.SocketServer;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import org.java_websocket.enums.ReadyState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SocketLaunch {
    public static SocketServer mainServer;
    public static SocketClient mainClient;

    public static boolean clientRunning = false;
    public static boolean serverRunning = false;

    public static List<Packet> packets = new ArrayList<>();

    public static List<EventHandle> acceptsEvent = new ArrayList<>();

    public static void startClient(String ip) throws URISyntaxException, InterruptedException {
        packets.add(new KeepAlivePacket());
        clientRunning = true;
        SocketClient client = new SocketClient(new URI(ip));
        mainClient = client;
        mainClient.connect();
        while (clientRunning) {
            if (!client.getReadyState().equals(ReadyState.OPEN)) {
                mainClient.reconnect();
                info("正在连接...");
                Thread.sleep(7500);
            }
        }
    }

    public static void addEvent(EventHandle e) {
        acceptsEvent.add(e);
    }

    public static void removeEvent(EventHandle e) {
        acceptsEvent.remove(e);
    }

    public static void stop() {
        serverRunning = false;
        clientRunning = false;
        try {
            if (mainServer != null) {
                mainServer.saveUsers();
                mainServer.stop(1000);
            }
            if (mainClient != null) mainClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void start(int port) throws InterruptedException, IOException {
        packets.add(new KeepAlivePacket());
        SocketServer server = new SocketServer(port);
        serverRunning = true;
        mainServer = server;
        mainServer.loadUsers();
        info("--正在启动WebSocket服务--");
        info("Port: " + server.getPort());
        mainServer.start();
        BufferedReader sIn = new BufferedReader(new InputStreamReader(System.in));
    }

    public static void info(String message) {
        ChatUtils.infoPrefix("Socket", message);
    }
}
