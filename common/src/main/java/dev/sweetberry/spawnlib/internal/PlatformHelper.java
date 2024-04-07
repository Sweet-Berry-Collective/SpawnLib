package dev.sweetberry.spawnlib.internal;

import dev.sweetberry.spawnlib.internal.attachment.ModifiedSpawnsAttachment;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public interface PlatformHelper {
    Platform getPlatform();

    ModifiedSpawnsAttachment getAttachment(ServerPlayer player);
    MinecraftServer getServer();

    <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key);
}
