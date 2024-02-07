package dev.sweetberry.spawnlib.neoforge;

import dev.sweetberry.spawnlib.impl.Platform;
import dev.sweetberry.spawnlib.impl.PlatformHelper;

public class NeoForgePlatformHelper implements PlatformHelper {
    @Override
    public Platform getPlatform() {
        return Platform.NEO_FORGE;
    }
}
