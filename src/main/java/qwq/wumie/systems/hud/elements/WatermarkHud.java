package qwq.wumie.systems.hud.elements;

import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import qwq.wumie.utils.render.MSAAFramebuffer;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class WatermarkHud extends HudElement {
    public static final HudElementInfo<WatermarkHud> INFO = new HudElementInfo<>(Hud.GROUP, "watermark-hud", "anti leak.", WatermarkHud::new);

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> customTitle = sgGeneral.add(new StringSetting.Builder()
            .name("custom-title")
            .description("sets the title at index 0")
            .defaultValue("Meteor")
            .build()
    );

    private final TitleEffect splits = new TitleEffect(new TitleEffect.Name[]{new TitleEffect.Name("Meteor", Color.WHITE, 0), new TitleEffect.Name("null", Color.WHITE, 1), new TitleEffect.Name("null", Color.WHITE, 2), new TitleEffect.Name("null", Color.WHITE, 3), new TitleEffect.Name("null", Color.WHITE, 4)});
    private final Color bgColor = new Color(5, 5, 5, 147);
    private double width = -1;
    private final double offset = 12;

    public WatermarkHud() {
        super(INFO);
    }

    @Override
    public void tick(HudRenderer renderer) {
        setSize(width == -1 ? 80 : width, 45);
        super.tick(renderer);
    }

    @Override
    public void render(HudRenderer renderer) {
        if (!Utils.canUpdate()) return;

        renderer.post(() -> {
            MSAAFramebuffer.use(() -> {
                double localWidth = calcWidth();
                gameRender.drawRoundRect(this.x,this.y,localWidth,50,5,bgColor);
                for (int i = 0; i < splits.chars.length; i++) {
                    double xPos = this.x + 2 + calcX(i);
                    TitleEffect.Name entry = splits.chars[i];
                    double fontScale = 1;
                    double y = this.y + offset;
                    switch (entry.i) {
                        case 0 -> {
                            entry.n = customTitle.get();
                            fontScale = 1.25;
                            y = y - 2;
                        }
                        case 1 -> entry.n = mc.player.getName().getString();
                        case 2 -> entry.n = ping() + " Ping";
                        case 3 -> {
                            LocalTime time = LocalTime.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                            entry.n = time.format(formatter);
                        }
                        case 4 -> {
                            ServerInfo serverInfo = mc.getCurrentServerEntry();
                            String serverIp = "null";
                            if (mc.isInSingleplayer()) serverIp = "localhost";
                            else if (serverInfo != null) serverIp = serverInfo.address;
                            entry.n = serverIp;
                        }
                    }

                    String title = entry.n;
                    TextRenderer fontRender = TextRenderer.get();
                    fontRender.render(title,xPos,y,entry.c,true,fontScale);
                    double fWdith = fontRender.getWidth(title,true,fontScale);
                    if (entry.i != 4) {
                        gameRender.drawRect(xPos + fWdith + 1, this.y + 10, 2, 30, Color.BLACK.copy().a(170));
                    }
                }

                this.width = localWidth;
            });
        });
        super.render(renderer);
    }

    private double calcWidth() {
        TextRenderer textRenderer = TextRenderer.get();
        double hOffset = 4;
        double finalWidth = 0;
        double titleWidth = textRenderer.getWidth(customTitle.get(), true, 1.25);
        finalWidth += titleWidth + hOffset;
        double nameWidth = textRenderer.getWidth(mc.player.getName().getString(), true);
        finalWidth += nameWidth + hOffset;
        double pingWidth = textRenderer.getWidth(ping() + " Ping", true);
        finalWidth += pingWidth + hOffset;
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        double timeWidth = textRenderer.getWidth(time.format(formatter), true);
        finalWidth += timeWidth + hOffset;
        ServerInfo serverInfo = mc.getCurrentServerEntry();
        String serverIp = "null";
        if (mc.isInSingleplayer()) {
            serverIp = "localhost";
        } else if (serverInfo != null) {
            serverIp = serverInfo.address;
        }
        double ipWidth = textRenderer.getWidth(serverIp, true);
        finalWidth += ipWidth + hOffset;

        return finalWidth == 0 ? 80 : finalWidth;
    }

    private double calcX(int index) {
        int i = 0;
        TextRenderer textRenderer = TextRenderer.get();
        double hOffset = 4;
        double finalWidth = 0;
        double titleWidth = textRenderer.getWidth(customTitle.get(), true, 1.25);
        finalWidth += titleWidth + hOffset;
        i++;
        if (i == index) {
            return finalWidth;
        }
        double nameWidth = textRenderer.getWidth(mc.player.getName().getString(), true);
        finalWidth += nameWidth + hOffset;
        i++;
        if (i == index) {
            return finalWidth;
        }
        double pingWidth = textRenderer.getWidth(ping() + " Ping", true);
        finalWidth += pingWidth + hOffset;
        i++;
        if (i == index) {
            return finalWidth;
        }
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        double timeWidth = textRenderer.getWidth(time.format(formatter), true);
        finalWidth += timeWidth + hOffset;
        i++;
        if (i == index) {
            return finalWidth;
        }
        ServerInfo serverInfo = mc.getCurrentServerEntry();
        String serverIp = "null";
        if (mc.isInSingleplayer()) {
            serverIp = "localhost";
        } else if (serverInfo != null) {
            serverIp = serverInfo.address;
        }
        double ipWidth = textRenderer.getWidth(serverIp, true);
        finalWidth += ipWidth + hOffset;
        return finalWidth;
    }

    private int ping() {
        if (mc.getNetworkHandler() == null || mc.player == null) return 0;

        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        return playerListEntry != null ? playerListEntry.getLatency() : 0;
    }

    private static class TitleEffect {
        public Name[] chars;

        public TitleEffect(Name[] chars) {
            this.chars = chars;
        }

        static class Name {
            public String n;
            public Color c;
            public int i;

            public Name(String n, Color c, int i) {
                this.n = n;
                this.c = c;
                this.i = i;
            }
        }
    }
}
