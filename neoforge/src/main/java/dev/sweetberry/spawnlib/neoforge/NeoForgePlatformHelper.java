package dev.sweetberry.spawnlib.neoforge;

import dev.sweetberry.spawnlib.api.SpawnModification;
import dev.sweetberry.spawnlib.internal.Platform;
import dev.sweetberry.spawnlib.internal.PlatformHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NeoForgePlatformHelper implements PlatformHelper {
    @Override
    public Platform getPlatform() {
        return Platform.NEOFORGE;
    }

    @Override
    @Nullable
    public SpawnModification getLocalSpawn(ServerPlayer player) {
        return null;
    }

    @Override
    @Nullable
    public SpawnModification getGlobalSpawn(ServerPlayer player) {
        return null;
    }

    @Override
    @NotNull
    public SpawnModification getGlobalSpawn(MinecraftServer server) {
        return null;
    }
}
