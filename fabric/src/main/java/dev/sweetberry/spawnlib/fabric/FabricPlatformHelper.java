package dev.sweetberry.spawnlib.fabric;

import dev.sweetberry.spawnlib.internal.Platform;
import dev.sweetberry.spawnlib.internal.PlatformHelper;

public class FabricPlatformHelper implements PlatformHelper {
    @Override
    public Platform getPlatform() {
        return Platform.FABRIC;
    }
}
