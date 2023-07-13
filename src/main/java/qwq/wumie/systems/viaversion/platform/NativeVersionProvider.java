package qwq.wumie.systems.viaversion.platform;

import com.viaversion.viaversion.api.platform.providers.Provider;

public interface NativeVersionProvider extends Provider {
    int getNativeServerVersion();
}
