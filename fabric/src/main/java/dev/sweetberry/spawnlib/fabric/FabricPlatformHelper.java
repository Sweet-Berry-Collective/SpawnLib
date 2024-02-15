package dev.sweetberry.spawnlib.fabric;

import dev.sweetberry.spawnlib.api.SpawnModification;
import dev.sweetberry.spawnlib.internal.Platform;
import dev.sweetberry.spawnlib.internal.PlatformHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FabricPlatformHelper implements PlatformHelper {
    @Override
    public Platform getPlatform() {
        return Platform.FABRIC;
    }

    @Override
    public SpawnModification getLocalSpawn(ServerPlayer player) {
        return null;
    }

    @Override
    public SpawnModification getGlobalSpawn(ServerPlayer player) {
        return null;
    }

    @Override
    public @NotNull SpawnModification getGlobalSpawn(MinecraftServer server) {
        return null;
    }
}
