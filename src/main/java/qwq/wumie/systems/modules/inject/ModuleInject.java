package qwq.wumie.systems.modules.inject;

import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import net.minecraft.client.MinecraftClient;

public class ModuleInject<T extends Module> {
    protected MinecraftClient mc = MinecraftClient.getInstance();
    public T module;
    public final Class<T> klass;
    protected RenderUtils gameRender;

    public ModuleInject(Class<T> moduleKlass) {
        klass = moduleKlass;
        this.module = Modules.get().get(klass);
        if (module == null) {
            throw new NullPointerException("ModuleClass: "+klass.getName()+" Not Found");
        }

        gameRender = RenderUtils.instance;
    }

    public void setRender(RenderUtils render) {
        this.gameRender = render;
    }

    public void onInit() {

    }

    public SettingGroup createGroup(String name) {
      return  module.settings.createGroup(name);
    }

    public SettingGroup getGroup(String name) {
        return  module.settings.getGroup(name);
    }

    public SettingGroup getDefaultGroup() {
        return  module.settings.getDefaultGroup();
    }
}
