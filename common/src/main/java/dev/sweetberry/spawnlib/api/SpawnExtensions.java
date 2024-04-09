package dev.sweetberry.spawnlib.api;

import dev.sweetberry.spawnlib.internal.SpawnLib;
import dev.sweetberry.spawnlib.internal.duck.Duck_MinecraftServer;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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

    public static void setGlobalSpawn(MinecraftServer server, Holder<ModifiedSpawn> spawn) {
        ((Duck_MinecraftServer)server).spawnlib$setGlobalSpawn(spawn, new CompoundTag());
    }

    public static void setGlobalSpawn(MinecraftServer server, Holder<ModifiedSpawn> spawn, @Nullable Tag metadata) {
        ((Duck_MinecraftServer)server).spawnlib$setGlobalSpawn(spawn, metadata);
    }

    @Nullable
    public static ModifiedSpawn getGlobalSpawn(ServerPlayer player) {
        return SpawnLib.getHelper().getAttachment(player).getGlobalSpawn();
    }

    public static void setGlobalSpawn(ServerPlayer player, Holder<ModifiedSpawn> spawn) {
        setGlobalSpawn(player, spawn, null);
    }

    public static void setGlobalSpawn(ServerPlayer player, Holder<ModifiedSpawn> spawn, @Nullable Tag metadata) {
        SpawnLib.getHelper().getAttachment(player).setGlobalSpawn(spawn);
        SpawnLib.getHelper().getAttachment(player).createMetadataProviders(spawn, SpawnPriority.GLOBAL_PLAYER, metadata);
    }

    public static void clearGlobalSpawn(ServerPlayer player) {
        SpawnLib.getHelper().getAttachment(player).clearGlobalSpawn();
    }

    @Nullable
    public static ModifiedSpawn getLocalSpawn(ServerPlayer player) {
        return SpawnLib.getHelper().getAttachment(player).getLocalSpawn();
    }

    public static void setLocalSpawn(ServerPlayer player, Holder<ModifiedSpawn> spawn) {
        setLocalSpawn(player, spawn, null);
    }

    public static void setLocalSpawn(ServerPlayer player, Holder<ModifiedSpawn> spawn, @Nullable Tag metadata) {
        SpawnLib.getHelper().getAttachment(player).setLocalSpawn(spawn);
        SpawnLib.getHelper().getAttachment(player).createMetadataProviders(spawn, SpawnPriority.LOCAL_PLAYER, metadata);
    }

    public static void clearLocalSpawn(ServerPlayer player) {
        SpawnLib.getHelper().getAttachment(player).clearLocalSpawn();
    }
}
