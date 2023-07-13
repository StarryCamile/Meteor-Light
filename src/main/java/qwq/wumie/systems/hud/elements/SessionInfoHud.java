package qwq.wumie.systems.hud.elements;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import qwq.wumie.systems.music.player.PlayMusic;
import qwq.wumie.utils.render.MSAAFramebuffer;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SessionInfoHud extends HudElement {
    public static final HudElementInfo<SessionInfoHud> INFO = new HudElementInfo<>(Hud.GROUP, "session-info-hud", "The info for playing status.", SessionInfoHud::new);
    public static final SessionInfo sessionInfo = new SessionInfo();

    public SessionInfoHud() {
        super(INFO);
        MeteorClient.EVENT_BUS.subscribe(sessionInfo);
    }

    private double hei;

    @Override
    public void tick(HudRenderer renderer) {
        setSize(250, hei);
        super.tick(renderer);
    }

    @Override
    public void render(HudRenderer renderer) {
        renderer.post(() -> {
            MSAAFramebuffer.use(() -> {
                gameRender.drawRoundRect(this.x, this.y, box.width, box.height, 5, new Color(7, 7, 7, 120));
                TextRenderer font = TextRenderer.get();
                centerText("Session Info", 250 / 2, 4, Color.WHITE, 1.15);
                double curX = this.x + 4;
                double curY = this.x + 4+font.getHeight(true,1.15)+4;
                double endX = this.x + box.width - 4;
                // Play Time
                font.render("Play Time",curX,curY,Color.WHITE,true);
                font.render(sessionInfo.getTimeInfo(),endX -font.getWidth(sessionInfo.getTimeInfo(),true),curY,Color.WHITE,true);

                this.hei = curY + font.getHeight(true)+4;
            });
        });
        super.render(renderer);
    }

    public void centerText(String s, double x, double y, Color color, double fontScale) {
        TextRenderer text = TextRenderer.get();
        double fX = text.getWidth(s, fontScale);
        double sX = x - (fX / 2);
        text.render(s, sX, y, color,true, fontScale);
    }

    static class SessionInfo {
        public long playTime;
        public int kills;
        private Session last,current;

        public SessionInfo() {
            playTime = 0;
            kills = 0;
            current = MinecraftClient.getInstance().getSession();
        }

        @EventHandler
        private void onRenderUpdate(Render2DEvent e) {
            if (Utils.canUpdate()) {
                if ((System.currentTimeMillis() % 2) == 1) {
                    playTime++;
                }
            }
        }

        @EventHandler
        private void onTick(TickEvent e) {
            if (last != current) {
                reset();
                last = current;
            }
        }

        public void reset() {
            this.playTime = 0;
            this.kills = 0;
            current = MinecraftClient.getInstance().getSession();
        }

        public String getTimeInfo() {
            long sec = playTime / 1000;
            long min = sec / 60;
            long hour = min / 60;

            if (min * 60 > sec) {
                min--;
            }
            if (hour*60 > min) {
                hour--;
            }
            long secOffset = sec - min * 60;
            long minOffset = min % 60;
            long hourOffset = min - hour * 60;
            String secStr = (secOffset + "").length() == "1".length() ? "0" + secOffset : secOffset + "";
            String minStr = (minOffset + "").length() == "1".length() ? "0" + minOffset : minOffset + "";
            String hourStr = (hourOffset + "").length() == "1".length() ? "0" + hourOffset : hourOffset + "";
            return hourStr + ":" + minStr + ":" + secStr;
        }
    }
}
