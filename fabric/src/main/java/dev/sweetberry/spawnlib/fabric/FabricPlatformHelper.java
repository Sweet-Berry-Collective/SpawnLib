package dev.sweetberry.spawnlib.fabric;

import dev.sweetberry.spawnlib.internal.Platform;
import dev.sweetberry.spawnlib.internal.PlatformHelper;
import dev.sweetberry.spawnlib.internal.attachment.PlayerSpawnsAttachment;
import dev.sweetberry.spawnlib.internal.attachment.WorldSpawnAttachment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class FabricPlatformHelper implements PlatformHelper {
    @Override
    public Platform getPlatform() {
        return Platform.FABRIC;
    }

    @Override
    public PlayerSpawnsAttachment getAttachment(ServerPlayer player) {
        return player.getAttachedOrCreate(SpawnLibFabric.PLAYER_ATTACHMENT);
    }

    @Override
    public WorldSpawnAttachment getAttachment(MinecraftServer server) {
        return server.getLevel(Level.OVERWORLD).getAttachedOrCreate(SpawnLibFabric.WORLD_ATTACHMENT);
    }

    @Override
    public MinecraftServer getServer() {
        return SpawnLibFabric.getServer();
    }

    @Override
    public <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key) {
        return FabricRegistryBuilder.createSimple(key).buildAndRegister();
    }
}
