package qwq.wumie.systems.hud.elements;

import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.misc.MeteorIdentifier;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.Identifier;
import qwq.wumie.systems.notification.Notification;
import qwq.wumie.systems.notification.NotificationManager;
import qwq.wumie.utils.render.MSAAFramebuffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationHud extends HudElement {
    public static final HudElementInfo<NotificationHud> INFO = new HudElementInfo<>(Hud.GROUP, "notification-hud", "simple notification.", NotificationHud::new);

    private static final Identifier ERROR_ID = new MeteorIdentifier("notification/error.png");
    private static final Identifier INFO_ID = new MeteorIdentifier("notification/info.png");
    private static final Identifier SUCCESS_ID = new MeteorIdentifier("notification/success.png");
    private static final Identifier WARN_ID = new MeteorIdentifier("notification/warning.png");

    private static final Color ERROR_COLOR = new Color(255, 0, 0);
    private static final Color INFO_COLOR = new Color(255, 255, 255);
    private static final Color SUCCESS_COLOR = new Color(0, 255, 0);
    private static final Color WARN_COLOR = Color.YELLOW;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> maxNotifications = sgGeneral.add(new IntSetting.Builder()
            .name("max-notifications")
            .description("out of the num will remove")
            .defaultValue(7)
            .build()
    );

    private final Setting<Boolean> useCalcWidth = sgGeneral.add(new BoolSetting.Builder()
            .name("use-calc-width")
            .description("Automatic width calculation.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> reverse = sgGeneral.add(new BoolSetting.Builder()
            .name("reverse-notifications")
            .description("Reverse the notification render.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> titleEnable = sgGeneral.add(new BoolSetting.Builder()
            .name("render-title")
            .description("Allow render notification title.")
            .defaultValue(false)
            .build()
    );

    public NotificationHud() {
        super(INFO);
    }

    @Override
    public void tick(HudRenderer renderer) {
        setSize(250, 50);
        super.tick(renderer);
    }

    @Override
    public void render(HudRenderer renderer) {
        NotificationManager notificationManager = NotificationManager.INSTANCE;
        renderer.post(() -> {
            MSAAFramebuffer.use(() -> {
                double boxX = this.x;
                double boxY = this.y;

                if (notificationManager != null) {
                    TextRenderer font = TextRenderer.get();
                    GL.enableBlend();
                    double offset = 4;
                    final List<Notification> copied = new ArrayList<>(notificationManager.notifications);
                    if (reverse.get()) {
                        Collections.reverse(copied);
                    }
                    for (Notification n : copied) {
                        if (copied.size() > maxNotifications.get()) {
                            notificationManager.notifications.get(0).showTime = 0;
                        }

                        double width = useCalcWidth.get() ? offset + (35 + font.getWidth(n.text, 1.1)) + offset : 250;

                        if (n.showTime <= 1 && n.startUpdated) {
                            n.x = smoothMove(n.x, boxX + width);
                            if (n.x >= (boxX + width) - 2) {
                                n.willRemove = true;
                            }
                        } else if (n.startUpdated) {
                            n.x = smoothMove(n.x, (boxX + 250) - width);
                        }

                        n.y = smoothMove(n.y, boxY);

                        gameRender.drawRoundRect(n.x, n.y, width, 50, 4, new Color(70, 70, 70, 150));
                        Color proColor = new Color();

                        switch (n.type) {
                            case INFO -> {
                                GL.bindTexture(INFO_ID);
                                proColor = INFO_COLOR;
                            }
                            case ERROR -> {
                                GL.bindTexture(ERROR_ID);
                                proColor = ERROR_COLOR;
                            }
                            case WARING -> {
                                GL.bindTexture(WARN_ID);
                                proColor = WARN_COLOR;
                            }
                            case SUCCESS -> {
                                GL.bindTexture(SUCCESS_ID);
                                proColor = SUCCESS_COLOR;
                            }
                        }

                        renderer.texture(n.x + offset, n.y + 15, 23, 23, Color.WHITE);
                        if (titleEnable.get()) {
                            font.render(n.title, boxX + offset, boxY + 1, Color.WHITE, 0.7);
                        }
                        font.render(n.text, n.x + 35, n.y + 15, proColor, 1.1);
                        //gameRender.drawRoundRect(n.x, n.y + 45, width * (n.showTime / n.maxShowTime), 5, 4, proColor);

                        boxY -= 50 + offset;
                    }

                    GL.disableBlend();
                }
            });
        });
        super.render(renderer);
    }
}
