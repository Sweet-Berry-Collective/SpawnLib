package dev.sweetberry.spawnlib.fabric;

import dev.sweetberry.spawnlib.impl.Platform;
import dev.sweetberry.spawnlib.impl.PlatformHelper;

public class FabricPlatformHelper implements PlatformHelper {
    @Override
    public Platform getPlatform() {
        return Platform.FABRIC;
    }
}
