package dev.sweetberry.spawnlib.neoforge;

import dev.sweetberry.spawnlib.internal.Platform;
import dev.sweetberry.spawnlib.internal.PlatformHelper;
import dev.sweetberry.spawnlib.internal.attachment.PlayerSpawnsAttachment;
import dev.sweetberry.spawnlib.internal.attachment.WorldSpawnAttachment;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class NeoForgePlatformHelper implements PlatformHelper {
    @Override
    public Platform getPlatform() {
        return Platform.NEOFORGE;
    }

    @Override
    public PlayerSpawnsAttachment getAttachment(ServerPlayer player) {
        return player.getData(SpawnLibNeoForge.PLAYER_ATTACHMENT);
    }

    @Override
    public WorldSpawnAttachment getAttachment(MinecraftServer server) {
        return server.getLevel(Level.OVERWORLD).getData(SpawnLibNeoForge.WORLD_ATTACHMENT);
    }

    @Override
    public MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key) {
        return new RegistryBuilder<>(key).create();
    }
}
