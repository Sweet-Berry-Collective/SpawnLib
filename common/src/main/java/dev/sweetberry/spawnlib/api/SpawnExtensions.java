package dev.sweetberry.spawnlib.api;

import dev.sweetberry.spawnlib.internal.SpawnLib;
import dev.sweetberry.spawnlib.internal.duck.Duck_MinecraftServer;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class SpawnExtensions {
    public static ModifiedSpawn getGlobalSpawn(MinecraftServer server) {
        return ((Duck_MinecraftServer)server).spawnlib$getGlobalSpawn().value();
    }

    @Nullable
    public static ModifiedSpawn getGlobalSpawn(ServerPlayer player) {
        return SpawnLib.getHelper().getAttachment(player).getGlobalSpawn();
    }

    @Nullable
    public static ModifiedSpawn getLocalSpawn(ServerPlayer player) {
        return SpawnLib.getHelper().getAttachment(player).getLocalSpawn();
    }

    @Nullable
    public static <T> T getDataFromSpawn(ServerPlayer player, Holder<ModifiedSpawn> priority) {
        return SpawnLib.getHelper().getAttachment(player).getData(priority);
    }
}
