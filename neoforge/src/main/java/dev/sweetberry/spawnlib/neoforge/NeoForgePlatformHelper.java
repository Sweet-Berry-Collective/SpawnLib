package dev.sweetberry.spawnlib.neoforge;

import com.mojang.serialization.Codec;
import dev.sweetberry.spawnlib.api.metadata.MetadataType;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.Platform;
import dev.sweetberry.spawnlib.internal.PlatformHelper;
import dev.sweetberry.spawnlib.internal.attachment.ModifiedSpawnsAttachment;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class NeoForgePlatformHelper implements PlatformHelper {
    @Override
    public Platform getPlatform() {
        return Platform.NEOFORGE;
    }

    @Override
    public Registry<Codec<? extends SpawnModification>> getSpawnModificationCodecRegistry() {
        return null;
    }

    @Override
    public Registry<MetadataType<?>> getMetadataTypeRegistry() {
        return null;
    }

    @Override
    public ModifiedSpawnsAttachment getAttachment(ServerPlayer player) {
        return null;
    }

    @Override
    public MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }
}
