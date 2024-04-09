package dev.sweetberry.spawnlib.internal.duck;

import dev.sweetberry.spawnlib.api.ModifiedSpawn;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.List;

public interface Duck_MinecraftServer {
    Holder<ModifiedSpawn> spawnlib$getGlobalSpawn();

    List<MetadataProvider> spawnlib$getMetadataProviders();

    default void spawnlib$setGlobalSpawn(Holder<ModifiedSpawn> spawn) {
        spawnlib$setGlobalSpawn(spawn, new CompoundTag());
    }
    void spawnlib$setGlobalSpawn(Holder<ModifiedSpawn> spawn, Tag metadata);
}
