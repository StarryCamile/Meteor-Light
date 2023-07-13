package qwq.wumie.systems.modules.misc;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import qwq.wumie.systems.websocket.SocketLaunch;

import java.util.List;

public class Socket extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<SocketMode> mode = sgGeneral.add(new EnumSetting.Builder<SocketMode>()
            .name("mode")
            .defaultValue(SocketMode.Client)
            .build()
    );

    public final Setting<String> connectUrl = sgGeneral.add(new StringSetting.Builder()
            .name("connect-url")
            .defaultValue("ws://127.0.0.1:14514")
            .visible(() -> mode.get().equals(SocketMode.Client))
            .build()
    );

    public final Setting<Integer> serverPort = sgGeneral.add(new IntSetting.Builder()
            .name("server-port")
            .defaultValue(14515)
            .range(1,65536)
            .sliderRange(1,65535)
            .visible(() -> mode.get().equals(SocketMode.Server))
            .build()
    );

    public final Setting<Boolean> qqMessage = sgGeneral.add(new BoolSetting.Builder()
            .name("qq-message")
            .defaultValue(false)
            .build()
    );

    public final Setting<List<String>> acceptPlayers = sgGeneral.add(new StringListSetting.Builder()
            .name("forward-players")
            .description("accept.")
            .defaultValue(List.of("ImWuMie","MuXi"))
            .build()
    );

    public final Setting<String> spiltStr = sgGeneral.add(new StringSetting.Builder()
            .name("split-string")
            .defaultValue(":")
            .build()
    );

    public final Setting<Boolean> fishingBot = sgGeneral.add(new BoolSetting.Builder()
            .name("fishing-bot")
            .defaultValue(false)
            .build()
    );

    public final Setting<Boolean> debug = sgGeneral.add(new BoolSetting.Builder()
            .name("debug")
            .defaultValue(false)
            .build()
    );

    public Socket() {
        super(Categories.Misc, "socket", "idk......");
    }

    public boolean isAnnoyPlayer(String playerName) {
        for (String s : acceptPlayers.get()) {
            if (playerName.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public String getSender(String playerName) {
        for (String s : acceptPlayers.get()) {
            if (playerName.contains(s)) {
                return s;
            }
        }
        return "";
    }

    @Override
    public void onActivate() {
        Thread client = new Thread(() -> {
            try {
                SocketLaunch.startClient(this.connectUrl.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Thread server = new Thread(() -> {
            try {
                SocketLaunch.start(this.serverPort.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        switch (mode.get()) {
            case Client -> {
                client.setName("Client Socket");
                client.start();
            }
            case Server -> {
                server.setName("Server Socket");
                server.start();
            }
        }
        super.onActivate();
    }

    public boolean qqMessage() {
        return isActive() && qqMessage.get();
    }

    public boolean fishing() {
        return isActive() && fishingBot.get();
    }

    @Override
    public void onDeactivate() {
        SocketLaunch.stop();
        super.onDeactivate();
    }

    public enum SocketMode {
        Client,
        Server
    }
}
