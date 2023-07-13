package qwq.wumie.systems.modules.lemon;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import qwq.wumie.systems.handle.ModuleHandler;
import qwq.wumie.systems.modules.combat.AntiCev;
import qwq.wumie.systems.modules.combat.HolePush;
import qwq.wumie.systems.modules.lemon.combat.*;
import qwq.wumie.systems.modules.lemon.move.HoleSnap;
import qwq.wumie.systems.modules.lemon.utils.DeathUtils;

public class LemonModule extends ModuleHandler {
    public static LemonModule INSTANCE = new LemonModule();
    private static Modules mods;

    public void onInit(Modules modules) {
        mods = modules;
        DeathUtils.init();

        //add(new HoleSnap(),new CevBreaker(),new CityBreaker(),new CivBreaker(),/*new LemonAuraPlus(),*/new NewBurrow(),new NewSurround(),new PistonAura(),new LemonAura(),new AntiCev(),new BurBreaker(),new HolePush());
    }

    public void add(Module... m) {
        if (mods !=  null) {
            for (Module mod : m) {
                mods.add(mod);
            }
        }
    }

    public void add(Module m) {
        if (mods !=  null) mods.add(m);
    }

    public void initCombat() {

    }
}
