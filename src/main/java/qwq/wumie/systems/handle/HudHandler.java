package qwq.wumie.systems.handle;

import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import qwq.wumie.systems.hud.elements.*;
import qwq.wumie.systems.hud.elements.custom.CustomLine;
import qwq.wumie.systems.hud.elements.custom.CustomQuad;

public class HudHandler {
    private static Hud hud;

    public static void init(Hud hud) {
        HudHandler.hud = hud;

        register(ProfileHud.PROFILE.info);
        register(FPSGraphHud.INFO);
        register(PacketHud.INFO);
        //register(ToastNotifications.INFO);
        register(StatsHud.INFO);
        register(TargetHud.INFO);
        register(CustomQuad.INFO);
        register(PlayerHeadHud.INFO);
        register(CustomLine.INFO);
        register(KeyStrokesHud.INFO);
        register(MusicHud.INFO);
        register(NotificationHud.INFO);
        register(WatermarkHud.INFO);
        register(SessionInfoHud.INFO);
    }

    private static void register(HudElementInfo<?> info) {
        if (hud != null) {
            hud.register(info);
        }
    }
}
