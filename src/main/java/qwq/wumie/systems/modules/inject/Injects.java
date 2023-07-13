package qwq.wumie.systems.modules.inject;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import qwq.wumie.event.ModuleDisableEvent;
import qwq.wumie.event.ModuleEnableEvent;
import qwq.wumie.systems.modules.inject.combat.CritInject;
import qwq.wumie.systems.modules.inject.combat.KAInject;
import qwq.wumie.systems.modules.inject.combat.NameTagInject;
import qwq.wumie.systems.modules.inject.combat.VelocityInject;

import java.util.ArrayList;
import java.util.List;

public class Injects {
    private static final List<ModuleInject> moduleInjects = new ArrayList<>();

    public void init() {
        MeteorClient.EVENT_BUS.subscribe(this);

        add(new KAInject());
        add(new NameTagInject());
        add(new CritInject());
        add(new VelocityInject());

        moduleInjects.forEach(ModuleInject::onInit);
    }

    public void postInit() {

    }

    public static  <T extends ModuleInject> T getByModuleClass(Class klass) {
        for (ModuleInject m : moduleInjects) {
            if (m.klass.equals(klass)) {
                return (T) m;
            }
        }
        return null;
    }

    public static  <T extends ModuleInject> T get(Class klass) {
        for (ModuleInject m : moduleInjects) {
            if (m.getClass().equals(klass)) {
                return (T) m;
            }
        }
        return null;
    }

    private void add(ModuleInject mod) {
        moduleInjects.add(mod);
    }

    @EventHandler
    private void onD(ModuleDisableEvent e) {
        for (ModuleInject inject : moduleInjects) {
            if (e.module.name.equals(inject.module.name)) {
                MeteorClient.EVENT_BUS.unsubscribe(inject);
            }
        }
    }

    @EventHandler
    private void onE(ModuleEnableEvent e) {
        for (ModuleInject inject : moduleInjects) {
            if (e.module.name.equals(inject.module.name)) {
                MeteorClient.EVENT_BUS.subscribe(inject);
            }
        }
    }
}
