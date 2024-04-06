package dev.sweetberry.spawnlib.fabric;

import com.mojang.serialization.Codec;
import dev.sweetberry.spawnlib.api.metadata.MetadataType;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.fabric.registry.SpawnLibRegistries;
import dev.sweetberry.spawnlib.internal.Platform;
import dev.sweetberry.spawnlib.internal.PlatformHelper;
import dev.sweetberry.spawnlib.internal.attachment.ModifiedSpawnsAttachment;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class FabricPlatformHelper implements PlatformHelper {
    @Override
    public Platform getPlatform() {
        return Platform.FABRIC;
    }

    @Override
    public Registry<Codec<? extends SpawnModification>> getSpawnModificationCodecRegistry() {
        return SpawnLibRegistries.SPAWN_MODIFICATION_CODEC;
    }

    @Override
    public Registry<MetadataType<?>> getMetadataTypeRegistry() {
        return SpawnLibRegistries.METADATA_TYPE;
    }

    @Override
    public ModifiedSpawnsAttachment getAttachment(ServerPlayer player) {
        return player.getAttachedOrCreate(SpawnLibFabric.MODIFIED_SPAWNS_ATTACHMENT);
    }

    @Override
    public MinecraftServer getServer() {
        return SpawnLibFabric.getServer();
    }
}
