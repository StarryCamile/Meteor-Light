package qwq.wumie.event;

import meteordevelopment.meteorclient.systems.modules.Module;

public class ModuleDisableEvent {
    private static final ModuleDisableEvent INSTANCE = new ModuleDisableEvent();

    public Module module;

    public static ModuleDisableEvent get(Module module) {
        INSTANCE.module = module;
        return INSTANCE;
    }
}

