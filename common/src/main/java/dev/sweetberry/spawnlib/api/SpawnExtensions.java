package dev.sweetberry.spawnlib.api;

import dev.sweetberry.spawnlib.internal.duck.Duck_MinecraftServer;
import dev.sweetberry.spawnlib.internal.duck.Duck_ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class SpawnExtensions {
    public static SpawnModification getGlobalSpawn(MinecraftServer server) {
        return ((Duck_MinecraftServer)server).spawnlib$getGlobalSpawn();
    }

    @Nullable
    public static SpawnModification getGlobalSpawn(ServerPlayer player) {
        return ((Duck_ServerPlayer)player).spawnlib$getGlobalSpawn();
    }

    @Nullable
    public static SpawnModification getLocalSpawn(ServerPlayer player) {
        return ((Duck_ServerPlayer)player).spawnlib$getLocalSpawn();
    }
}
