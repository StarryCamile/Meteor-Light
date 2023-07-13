package qwq.wumie.systems.viaversion.platform;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.PlayerLookTargetProvider;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.provider.PlayerAbilitiesProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import meteordevelopment.meteorclient.MeteorClient;
import qwq.wumie.systems.viaversion.providers.FabricVersionProvider;
import qwq.wumie.systems.viaversion.providers.VFPlayerAbilitiesProvider;
import qwq.wumie.systems.viaversion.providers.VFPlayerLookTargetProvider;
import qwq.wumie.systems.viaversion.providers.VRHandItemProvider;

public class VFLoader implements ViaPlatformLoader {
    @Override
    public void load() {
        Via.getManager().getProviders().use(VersionProvider.class, new FabricVersionProvider());

        if (Via.getPlatform().getConf().isItemCache()) {
            VRHandItemProvider handProvider = new VRHandItemProvider();
            MeteorClient.EVENT_BUS.subscribe(handProvider);
            Via.getManager().getProviders().use(HandItemProvider.class, handProvider);
        }

        Via.getManager().getProviders().use(PlayerAbilitiesProvider.class, new VFPlayerAbilitiesProvider());
        Via.getManager().getProviders().use(PlayerLookTargetProvider.class, new VFPlayerLookTargetProvider());
    }

    @Override
    public void unload() {
        // Nothing to do
    }
}
