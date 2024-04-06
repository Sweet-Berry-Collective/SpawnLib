package dev.sweetberry.spawnlib.fabric.registry;

import com.mojang.serialization.Codec;
import dev.sweetberry.spawnlib.api.SpawnLibRegistryKeys;
import dev.sweetberry.spawnlib.api.metadata.MetadataType;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;

public class SpawnLibRegistries {
    public static final MappedRegistry<Codec<? extends SpawnModification>> SPAWN_MODIFICATION_CODEC = FabricRegistryBuilder.createSimple(SpawnLibRegistryKeys.SPAWN_MODIFICATION_CODEC).buildAndRegister();
    public static final Registry<MetadataType<?>> METADATA_TYPE = FabricRegistryBuilder.createSimple(SpawnLibRegistryKeys.METADATA_TYPE).buildAndRegister();
}
