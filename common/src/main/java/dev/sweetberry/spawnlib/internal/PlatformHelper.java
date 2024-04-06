package dev.sweetberry.spawnlib.internal;

import com.mojang.serialization.Codec;
import dev.sweetberry.spawnlib.api.metadata.MetadataType;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.attachment.ModifiedSpawnsAttachment;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public interface PlatformHelper {
    Platform getPlatform();

    Registry<Codec<? extends SpawnModification>> getSpawnModificationCodecRegistry();
    Registry<MetadataType<?>> getMetadataTypeRegistry();
    ModifiedSpawnsAttachment getAttachment(ServerPlayer player);
    MinecraftServer getServer();
}
