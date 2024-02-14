package dev.sweetberry.spawnlib.fabric;

import dev.sweetberry.spawnlib.api.SpawnModification;
import dev.sweetberry.spawnlib.internal.Platform;
import dev.sweetberry.spawnlib.internal.PlatformHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FabricPlatformHelper implements PlatformHelper {
    @Override
    public Platform getPlatform() {
        return Platform.FABRIC;
    }

    @Override
    public SpawnModification getLocalSpawn(Player player) {
        return null;
    }

    @Override
    public SpawnModification getGlobalSpawn(Player player) {
        return null;
    }

    @Override
    public SpawnModification getGlobalSpawn(Level level) {
        return null;
    }
}
