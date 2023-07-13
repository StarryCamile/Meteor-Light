/*By Yurnu 6666*/
package qwq.wumie.systems.modules.crash;

import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;


public class SBCrash extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> amount = sgGeneral.add(new IntSetting.Builder()
            .name("SB Amount")
            .description("Let your client crash :D")
            .defaultValue(15)
            .min(1)
            .sliderMax(100)
            .build()
    );

    public SBCrash() {
        super(Categories.Crash, "SB-crash", "Crashes your client");
    }

    private final Setting<Boolean> disableOnLeave = sgGeneral.add(new BoolSetting.Builder()
            .name("disable-on-leave")
            .description("Disables spam when you leave a server.")
            .defaultValue(true)
            .build()
    );

    int ticks = 0;
    boolean start = false;
    @Override
    public void onActivate() {
        if (mc.world != null && mc.player != null) {
            info("The client crashed in 10 seconds.");
            ticks  = 0;
            start = true;
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) throws Exception {
        if (start) {
            int endTicks = 20 * 10;
            if (ticks >= endTicks) {
                throw new Exception("sb yurnu L");
            } else {
                ticks++;
            }
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        if (disableOnLeave.get()) toggle();
        ticks = 0;
        start = false;
    }
}
