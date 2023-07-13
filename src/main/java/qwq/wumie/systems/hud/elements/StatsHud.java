package qwq.wumie.systems.hud.elements;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.hud.elements.TextHud;
import qwq.wumie.utils.VectorUtils;
import qwq.wumie.utils.misc.Stats;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class StatsHud extends HudElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> header = sgGeneral.add(new BoolSetting.Builder()
        .name("header")
        .description("Renders a header over the stats.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> ignoreFriends = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-friends")
        .description("Ignores friends.")
        .defaultValue(true)
        .onChanged(changed -> updateChanges())
        .build()
    );

    private final Setting<Boolean> checkTargets = sgGeneral.add(new BoolSetting.Builder()
        .name("check-targets")
        .description("Checks if any of the active modules targets a player that died / poped near you.")
        .defaultValue(false)
        .onChanged(changed -> updateChanges())
        .build()
    );

    private final Setting<Boolean> clearPopsOnDeath = sgGeneral.add(new BoolSetting.Builder()
        .name("clear-pos-on-death")
        .description("Resets your pop scores on death.")
        .defaultValue(true)
        .onChanged(changed -> updateChanges())
        .build()
    );

    private final Setting<Boolean> clearKillsOnDeath = sgGeneral.add(new BoolSetting.Builder()
        .name("clear-kills-on-death")
        .description("Resets your kill scores on death.")
        .defaultValue(true)
        .onChanged(changed -> updateChanges())
        .build()
    );

    private final Setting<Boolean> pops = sgGeneral.add(new BoolSetting.Builder()
        .name("pops")
        .description("Shows how often you poped players.")
        .defaultValue(true)
        .onChanged(changed -> updateChanges())
        .build()
    );

    private final Setting<Boolean> kills = sgGeneral.add(new BoolSetting.Builder()
        .name("kills")
        .description("Shows how often you killed players.")
        .defaultValue(true)
        .onChanged(changed -> updateChanges())
        .build()
    );

    private final Setting<Boolean> deaths = sgGeneral.add(new BoolSetting.Builder()
        .name("deaths")
        .description("Shows how often you poped players.")
        .defaultValue(true)
        .onChanged(changed -> updateChanges())
        .build()
    );

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("deaths")
        .description("Shows how often you poped players.")
        .defaultValue(7.5)
        .min(2)
        .max(50)
        .sliderMin(2)
        .sliderMax(10)
        .onChanged(changed -> updateChanges())
        .build()
    );

    public static final HudElementInfo<StatsHud> INFO = new HudElementInfo<>(Hud.GROUP,"stats", "Displays if selected modules are enabled or disabled.",StatsHud::new);

    private Stats scores;

    private int allPops = 0;
    private int allKills = 0;
    private int allDeaths = 0;

    public StatsHud() {
        super(INFO);
    }

    @Override
    public void tick(HudRenderer renderer) {
        if (scores == null) scores = VectorUtils.scores;

        double width = 0;
        double height = 0;

        if (header.get()) height += renderer.textHeight();

        if (pops.get()) {
            width += renderer.textWidth("Pops: " + allPops);
            height += renderer.textHeight();
        }
        if (kills.get()) {
            width += renderer.textWidth("Kills: " + allKills);
            height += renderer.textHeight();
        }
        if (deaths.get()) {
            if (renderer.textWidth("Deaths: " + allDeaths) > width) width = renderer.textWidth("Deaths: " + allDeaths);
            height += renderer.textHeight();
        }

        setSize(width, height);
    }

    @Override
    public void render(HudRenderer renderer) {
        if (scores != null) {
            allPops = isInEditor() && mc.world == null ? 18 : scores.allPops;
            allKills = isInEditor() && mc.world == null ? 5 : scores.allKills;
            allDeaths = isInEditor() && mc.world == null ? 0 : scores.deaths;

            double x = this.x;
            double y = this.y;

            Color primaryColor = TextHud.getSectionColor(0);
            Color secondaryColor = TextHud.getSectionColor(1);

            if (header.get()) {
                renderer.text("Scores:", x, y, primaryColor,true);
                y += renderer.textHeight();
            }

            if (pops.get()) {
                renderer.text("Pops:", x, y, primaryColor,true);
                x += renderer.textWidth("Pops:");
                renderer.text(" " + allPops, x, y, secondaryColor,true);
                y += renderer.textHeight();
            }
            if (kills.get()) {
                x = this.x;
                renderer.text("Kills:", x, y, primaryColor,true);
                x += renderer.textWidth("Kills:");
                renderer.text(" " + allKills, x, y, secondaryColor,true);
                y += renderer.textHeight();
            }
            if (deaths.get()) {
                x = this.x;
                renderer.text("Deaths:", x, y, primaryColor,true);
                x += renderer.textWidth("Deaths:");
                renderer.text(" " + allDeaths, x, y,secondaryColor,true);
            }
        }
    }

    private void updateChanges() {
        if (scores != null) {
            scores.setIgnoreFriends(ignoreFriends.get());
            scores.setClearPopsOnDeath(clearPopsOnDeath.get());
            scores.setClearKillsOnDeath(clearKillsOnDeath.get());
            scores.setCheckTargets(checkTargets.get());
            scores.setRange(range.get());
        }
    }
}
