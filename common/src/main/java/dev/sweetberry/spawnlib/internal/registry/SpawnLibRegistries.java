package dev.sweetberry.spawnlib.internal.registry;

import com.mojang.serialization.Codec;
import dev.sweetberry.spawnlib.api.SpawnLibRegistryKeys;
import dev.sweetberry.spawnlib.api.metadata.MetadataType;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.core.Registry;

public class SpawnLibRegistries {
    public static final Registry<Codec<? extends SpawnModification>> SPAWN_MODIFICATION_CODECS = SpawnLib.getHelper().createRegistry(SpawnLibRegistryKeys.SPAWN_MODIFICATION_CODEC);
    public static final Registry<MetadataType<?>> METADATA_TYPE = SpawnLib.getHelper().createRegistry(SpawnLibRegistryKeys.METADATA_TYPE);

    public static void init() {}
}
