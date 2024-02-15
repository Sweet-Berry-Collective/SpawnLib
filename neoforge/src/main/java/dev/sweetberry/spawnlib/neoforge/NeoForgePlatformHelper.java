package dev.sweetberry.spawnlib.neoforge;

import dev.sweetberry.spawnlib.internal.Platform;
import dev.sweetberry.spawnlib.internal.PlatformHelper;

public class NeoForgePlatformHelper implements PlatformHelper {
    @Override
    public Platform getPlatform() {
        return Platform.NEOFORGE;
    }
}
