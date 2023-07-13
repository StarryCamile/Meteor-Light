package qwq.wumie.event;


import meteordevelopment.meteorclient.systems.modules.Module;

public class ModuleEnableEvent {
    private static final ModuleEnableEvent INSTANCE = new ModuleEnableEvent();

    public Module module;

    public static ModuleEnableEvent get(Module module) {
        INSTANCE.module = module;
        return INSTANCE;
    }
}
