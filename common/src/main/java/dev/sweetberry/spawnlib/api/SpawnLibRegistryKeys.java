package dev.sweetberry.spawnlib.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.sweetberry.spawnlib.api.metadata.MetadataType;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class SpawnLibRegistryKeys {
    public static final ResourceKey<Registry<ModifiedSpawn>> SPAWN = ResourceKey.createRegistryKey(SpawnLib.id("spawn"));

    public static final ResourceKey<Registry<MapCodec<? extends SpawnModification>>> SPAWN_MODIFICATION_CODEC = ResourceKey.createRegistryKey(SpawnLib.id("spawn_modification_codec"));
    public static final ResourceKey<Registry<MetadataType<?>>> METADATA_TYPE = ResourceKey.createRegistryKey(SpawnLib.id("metadata_type"));

    public static final ResourceKey<ModifiedSpawn> DEFAULT_SPAWN = ResourceKey.create(SPAWN, SpawnLib.id("default"));
}
