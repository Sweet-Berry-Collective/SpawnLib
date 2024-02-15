package dev.sweetberry.spawnlib.neoforge;

import dev.sweetberry.spawnlib.api.SpawnModification;
import dev.sweetberry.spawnlib.internal.Platform;
import dev.sweetberry.spawnlib.internal.PlatformHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class NeoForgePlatformHelper implements PlatformHelper {
    @Override
    public Platform getPlatform() {
        return Platform.NEOFORGE;
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
    public @NotNull SpawnModification getGlobalSpawn(MinecraftServer server) {
        return null;
    }
}
