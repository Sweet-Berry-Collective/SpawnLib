package dev.sweetberry.spawnlib.api.metadata;

import com.mojang.serialization.Codec;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import dev.sweetberry.spawnlib.internal.registry.RegistrationCallback;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class SpawnLibMetadataTypes {
    public static final MetadataType<ResourceKey<Level>> DIMENSION = resourceKey(Level.RESOURCE_KEY_CODEC);

    public static <T> MetadataType<ResourceKey<T>> resourceKey(Codec<ResourceKey<T>> codec) {
        return new MetadataType<>(castClass(ResourceKey.class), codec);
    }

    private static <T> Class<T> castClass(Class<?> clazz) {
        return (Class<T>) clazz;
    }

    public static void registerAll(RegistrationCallback<MetadataType<?>> callback) {
        callback.register(SpawnLib.getHelper().getMetadataTypeRegistry(), SpawnLib.id("dimension"), DIMENSION
        );
    }
}
