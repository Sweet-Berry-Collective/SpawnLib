package dev.sweetberry.spawnlib.api;

import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class SpawnExtensions {
    @Nullable
    public static Holder<ModifiedSpawn> getGlobalSpawn(MinecraftServer server) {
        return SpawnLib.getHelper().getAttachment(server).getSpawn();
    }

    public static void setGlobalSpawn(MinecraftServer server, Holder<ModifiedSpawn> spawn) {
        setGlobalSpawn(server, spawn, new CompoundTag());
    }

    public static void setGlobalSpawn(MinecraftServer server, Holder<ModifiedSpawn> spawn, @Nullable Tag metadata) {
        SpawnLib.getHelper().getAttachment(server).setSpawn(spawn, metadata);
    }

    @Nullable
    public static Holder<ModifiedSpawn> getGlobalSpawn(ServerPlayer player) {
        return SpawnLib.getHelper().getAttachment(player).getGlobalSpawn();
    }

    public static void setGlobalSpawn(ServerPlayer player, Holder<ModifiedSpawn> spawn) {
        setGlobalSpawn(player, spawn, new CompoundTag());
    }

    public static void setGlobalSpawn(ServerPlayer player, Holder<ModifiedSpawn> spawn, @Nullable Tag metadata) {
        SpawnLib.getHelper().getAttachment(player).setGlobalSpawn(spawn, metadata);
    }

    public static boolean clearGlobalSpawn(ServerPlayer player) {
        return SpawnLib.getHelper().getAttachment(player).clearGlobalSpawn();
    }

    @Nullable
    public static Holder<ModifiedSpawn> getLocalSpawn(ServerPlayer player) {
        return SpawnLib.getHelper().getAttachment(player).getLocalSpawn();
    }

    public static void setLocalSpawn(ServerPlayer player, Holder<ModifiedSpawn> spawn) {
        setLocalSpawn(player, spawn, new CompoundTag());
    }

    public static void setLocalSpawn(ServerPlayer player, Holder<ModifiedSpawn> spawn, @Nullable Tag metadata) {
        SpawnLib.getHelper().getAttachment(player).setLocalSpawn(spawn, metadata);
    }

    public static boolean clearLocalSpawn(ServerPlayer player) {
        return SpawnLib.getHelper().getAttachment(player).clearLocalSpawn();
    }
}
