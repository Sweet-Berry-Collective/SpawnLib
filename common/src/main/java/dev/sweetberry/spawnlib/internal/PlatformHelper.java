package dev.sweetberry.spawnlib.internal;

import dev.sweetberry.spawnlib.internal.attachment.PlayerSpawnsAttachment;
import dev.sweetberry.spawnlib.internal.attachment.WorldSpawnAttachment;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public interface PlatformHelper {
    Platform getPlatform();

    PlayerSpawnsAttachment getAttachment(ServerPlayer player);

    WorldSpawnAttachment getAttachment(MinecraftServer server);

    MinecraftServer getServer();

    <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key);
}
