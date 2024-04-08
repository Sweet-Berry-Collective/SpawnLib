package dev.sweetberry.spawnlib.api;

import dev.sweetberry.spawnlib.internal.SpawnLib;
import dev.sweetberry.spawnlib.internal.duck.Duck_MinecraftServer;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class SpawnExtensions {
    @Nullable
    public static ModifiedSpawn getGlobalSpawn(MinecraftServer server) {
        if (((Duck_MinecraftServer)server).spawnlib$getGlobalSpawn() == null)
            return null;

        return ((Duck_MinecraftServer)server).spawnlib$getGlobalSpawn().value();
    }

    @Nullable
    public static ModifiedSpawn getGlobalSpawn(ServerPlayer player) {
        return SpawnLib.getHelper().getAttachment(player).getGlobalSpawn();
    }

    public static void setGlobalSpawn(ServerPlayer player, Holder<ModifiedSpawn> spawn) {
        SpawnLib.getHelper().getAttachment(player).setGlobalSpawn(spawn);
    }

    public static void clearGlobalSpawn(ServerPlayer player) {
        SpawnLib.getHelper().getAttachment(player).clearGlobalSpawn();
    }

    @Nullable
    public static ModifiedSpawn getLocalSpawn(ServerPlayer player) {
        return SpawnLib.getHelper().getAttachment(player).getLocalSpawn();
    }

    public static void setLocalSpawn(ServerPlayer player, Holder<ModifiedSpawn> spawn) {
        SpawnLib.getHelper().getAttachment(player).setLocalSpawn(spawn);
    }

    public static void clearLocalSpawn(ServerPlayer player) {
        SpawnLib.getHelper().getAttachment(player).clearLocalSpawn();
    }
}
