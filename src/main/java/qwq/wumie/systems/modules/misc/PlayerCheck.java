package qwq.wumie.systems.modules.misc;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.UpdateEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import qwq.wumie.utils.misc.RandomUtils;
import qwq.wumie.utils.time.MSTimer;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerCheck extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> checkMinDelay = sgGeneral.add(new DoubleSetting.Builder().name("check-min-delay").range(0.0,Double.MAX_VALUE).sliderRange(0.0,Double.MAX_VALUE).defaultValue(3500.0).build());
    private final Setting<Double> checkMaxDelay = sgGeneral.add(new DoubleSetting.Builder().name("check-max-delay").range(0.0,Double.MAX_VALUE).sliderRange(0.0,Double.MAX_VALUE).defaultValue(4000.0).build());

    private final Setting<Boolean> invisiblePlayerCheck = sgGeneral.add(new BoolSetting.Builder()
            .name("invisible-player-check")
            .description("check server invisible players")
            .defaultValue(true)
            .build()
    );


    private int checkTimes = 0;
    private final MSTimer msTimer = new MSTimer();
    private long delay = RandomUtils.randomDelay(checkMinDelay.get().intValue(), checkMaxDelay.get().intValue());
    private List<String> lastPlayers = new ArrayList<>();
    private final List<String> players = new ArrayList<>();

    private List<String> tabPlayers = new ArrayList<>();

    private List<String> invisiblePlayers = new ArrayList<>();

    public PlayerCheck() {
        super(Categories.Misc, "player-check", "check server players");
    }

    @EventHandler
    private void onUpdate(UpdateEvent e) {
        if (msTimer.hasTimePassed(delay)) {
            check();
            msTimer.reset();
            delay = RandomUtils.randomDelay(checkMinDelay.get().intValue(), checkMaxDelay.get().intValue());
        }
    }

    public void check() {
        if (!isActive()) toggle();
        lastPlayers = players;
        players.clear();
        Random random = new Random();
        mc.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(random.nextInt(200), "minecraft:msg "));
    }

    @EventHandler
    private void onPacket(PacketEvent.Receive event) {
        if (event.packet instanceof PlayerListS2CPacket packet) {
            List<String> idk = new ArrayList<>();
            for (PlayerListS2CPacket.Entry entry : packet.getEntries()) {
                if (entry != null && entry.profile().getName() != null) {
                    idk.add(entry.profile().getName());
                }
            }
            tabPlayers = idk;
        }

        try {
            if (event.packet instanceof CommandSuggestionsS2CPacket packet) {
                Suggestions matches = packet.getSuggestions();
                if (matches == null) {
                    error("Invalid Packet.");
                    return;
                }

                for (Suggestion suggestion : matches.getList()) {
                    String player = suggestion.getText();

                    if (!players.contains(player)) {
                        players.add(player);
                    }
                }
                checkTimes++;
                if (checkTimes == 0) {
                    lastPlayers = players;
                }
                if (checkTimes != 0) {
                    onChanged(lastPlayers,players);
                }
            }
        } catch (Exception e) {
            error("An error occurred while trying to get players");
        }
    }

    @EventHandler
    private void onLeft(GameLeftEvent event) {
        checkTimes = 0;
        msTimer.reset();
    }

    private void onChanged(List<String> last,List<String> current) {
        // invisiblePlayers check
        List<String> joinPlayers = new ArrayList<>();
        for (String name : current) {
            if (!last.contains(name)) {
                joinPlayers.add(name);
            }
        }
        List<String> invisible = new ArrayList<>();
        for (String name : joinPlayers) {
            if (!tabPlayers.contains(name)) {
                invisible.add(name);
            }
        }

        onInvisiblePlayerChanged(invisiblePlayers,invisible);
        invisiblePlayers = invisible;
    }

    private void onInvisiblePlayerChanged(List<String> last,List<String> current) {
        List<String> changedPlayers = new ArrayList<>();
        for (String name : current) {
            if (!last.contains(name)) {
                changedPlayers.add(name);
            }
        }
        if (!changedPlayers.isEmpty()) {
            StringBuilder a = new StringBuilder("Update InvisiblePlayers: ");
            for (String s : changedPlayers) {
                a.append(s).append(" ,");
            }
            if (invisiblePlayerCheck.get()) {
                info(a.toString());
            }
        }
    }

    public int getCheckTimes() {
        return checkTimes;
    }

    public List<String> getLastPlayers() {
        return lastPlayers;
    }

    public List<String> getPlayers() {
        return players;
    }

    public List<String> getTabPlayers() {
        return tabPlayers;
    }

    public List<String> getInvisiblePlayers() {
        return invisiblePlayers;
    }

    @Override
    public void onActivate() {
        msTimer.reset();
    }
}
