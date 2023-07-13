package qwq.wumie.systems.websocket.server;

import meteordevelopment.meteorclient.MeteorClient;
import qwq.wumie.systems.commands.commands.Check.GsonUtils;
import meteordevelopment.meteorclient.systems.modules.Modules;
import qwq.wumie.systems.modules.misc.Socket;
import qwq.wumie.systems.websocket.NetworkHandle;
import qwq.wumie.systems.websocket.Packet;
import qwq.wumie.systems.websocket.SocketLaunch;
import qwq.wumie.systems.websocket.packets.CommandPacket;
import qwq.wumie.systems.websocket.packets.SayPacket;
import qwq.wumie.systems.websocket.results.MessageResult;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SocketServer extends WebSocketServer {
    private int connections = 0;
    public static File folder = new File(MeteorClient.FOLDER,"socket");
    public static File userFolder = new File(folder,"users");
    public List<String> ids = new ArrayList<>();
    public List<User> users = new ArrayList<>();

    public SocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        connections++;
        SocketLaunch.info("一个客户端连接到此,当前连接数: " + connections);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        connections--;
        SocketLaunch.info("一个客户端断开连接,当前连接数: " + connections);
    }

    public User getUser(String qq) {
        for (User user : users) {
            if (user.qq.equals(qq)) {
                return user;
            }
        }
        return new User(null,0);
    }

    public boolean inUsers(String qq) {
        for (User user : users) {
            if (user.qq.equals(qq)) {
                return true;
            }
        }
        return false;
    }

    public void saveUsers() {
        for (User u : users) {
            FileUtils.save(userFolder,u.qq+".json",GsonUtils.beanToJson(u));
        }
    }

    public void loadUsers() throws IOException {
        if (!folder.exists()) folder.mkdirs();
        if (!userFolder.exists()) userFolder.mkdirs();
        users.clear();

        Files.list(folder.toPath().resolve("users")).forEach(path -> {
            if (isValidFile(path)) {
                File f = path.toFile();
                String json = FileUtils.read(f).get(0);
                User u = GsonUtils.jsonToBean(json,User.class);
                users.add(u);
            }
        });
    }

    private static boolean isValidFile(Path file) {
        String extension = file.toFile().getName().endsWith(".json") ? "json" : "操你妈又乱放文件";
        return  (extension.equals("json"));
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        GMessage info = GsonUtils.jsonToBean(s,GMessage.class);

        if (info.getRaw_message() == null) return;

        if (info.getRaw_message().startsWith("packet")) {
            String qq = info.user_id;
            if (!inUsers(qq)) {
                User user = new User(qq,0);
                if (user.qq.equals("3177784940")) {
                    user.setLevel(2);
                }
                users.add(user);
                saveUsers();
                try {
                    loadUsers();
                } catch (Exception ignored) {}
            }
            try {
                loadUsers();
            } catch (Exception ignored) {}
            User user = getUser(qq);
            if (user.level == 0) {
                broadcast(new MessageResult(new MessageResult.Params(info.group_id,"你没有使用他的权限")).toJSON());
                return;
            }

            String[] args = info.getRaw_message().split(" ");
            switch (args[1].toLowerCase()) {
                case "chat" -> {
                    String message = info.getRaw_message().substring("packet chat ".length());
                    SayPacket packet = new SayPacket(message);
                    NetworkHandle.applyPacket(packet);
                    MessageResult result = new MessageResult(new MessageResult.Params(info.group_id,"已发送"+message));
                    broadcast(result.toJSON());
                }
                case "cmd" -> {
                    String command = info.getRaw_message().substring("packet cmd ".length());
                    CommandPacket packet = new CommandPacket(command,false);
                    NetworkHandle.applyPacket(packet);
                    MessageResult result = new MessageResult(new MessageResult.Params(info.group_id,"已发送"+command));
                    broadcast(result.toJSON());
                }
                case "custom" -> {
                    String packet = info.raw_message.substring("packet custom ".length());
                    Packet p = NetworkHandle.readPacket(packet);
                    if (p.name.isEmpty()) break;
                    NetworkHandle.applyPacket(p);
                    MessageResult result = new MessageResult(new MessageResult.Params(info.group_id,"已发送"+packet));
                    broadcast(result.toJSON());
                }
            }
            return;
        }

        if (info.getPost_type().equalsIgnoreCase("message")) {
            String name = info.sender.getCard().isEmpty() ? info.sender.getNickname() : info.sender.card;
            SocketLaunch.info("[QQ] ("+info.group_id+") "+name+" -> "+info.getRaw_message());
            return;
        }

        if (Modules.get().get(Socket.class).debug.get())  SocketLaunch.info("[Server] ReceivePacket: "+s);
        Packet packet = NetworkHandle.readPacket(s);
        if (packet.name.isEmpty()) return;
        NetworkHandle.applyPacket(packet);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }

    public void sendDebug(String s) {
        SocketLaunch.info("SendPacket: "+s);
        broadcast(s);
    }
}
