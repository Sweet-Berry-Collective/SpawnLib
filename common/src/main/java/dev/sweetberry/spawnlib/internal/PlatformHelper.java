package dev.sweetberry.spawnlib.internal;

import dev.sweetberry.spawnlib.api.SpawnModification;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PlatformHelper {
    Platform getPlatform();

    @Nullable
    SpawnModification getLocalSpawn(ServerPlayer player);

    @Nullable
    SpawnModification getGlobalSpawn(ServerPlayer player);

    @NotNull
    SpawnModification getGlobalSpawn(MinecraftServer server);
}
