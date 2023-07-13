package qwq.wumie.systems.viaversion.platform;

import net.minecraft.SharedConstants;

public class FabricNativeVersionProvider implements NativeVersionProvider {
    @Override
    public int getNativeServerVersion() {
        return SharedConstants.getGameVersion().getProtocolVersion();
    }
}
