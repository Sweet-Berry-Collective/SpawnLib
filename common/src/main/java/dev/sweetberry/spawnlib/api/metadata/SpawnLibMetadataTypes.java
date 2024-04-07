package dev.sweetberry.spawnlib.api.metadata;

import com.mojang.serialization.Codec;
import dev.sweetberry.spawnlib.api.codec.SpawnLibCodecs;
import dev.sweetberry.spawnlib.api.modification.InBoundsSpawnModification;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import dev.sweetberry.spawnlib.internal.registry.RegistrationCallback;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;

public class SpawnLibMetadataTypes {
    public static final Registry<MetadataType<?>> METADATA_TYPE = SpawnLib.getHelper().getMetadataTypeRegistry();

    public static final MetadataType<Integer> INT = new MetadataType<>(Integer.class, Codec.INT);
    public static final MetadataType<Double> DOUBLE = new MetadataType<>(Double.class, Codec.DOUBLE);
    public static final MetadataType<Float> FLOAT = new MetadataType<>(Float.class, Codec.FLOAT);
    public static final MetadataType<Boolean> BOOLEAN = new MetadataType<>(Boolean.class, Codec.BOOL);
    public static final MetadataType<Vec3> VEC3 = new MetadataType<>(Vec3.class, SpawnLibCodecs.OPTIONAL_VEC3);

    public static final MetadataType<ResourceKey<Level>> DIMENSION = resourceKey(Level.RESOURCE_KEY_CODEC);
    public static final MetadataType<HolderSet<Block>> BLOCKS = holderSet(Registries.BLOCK, BuiltInRegistries.BLOCK.holderByNameCodec(), BuiltInRegistries.BLOCK.byNameCodec());
    public static final MetadataType<HolderSet<Fluid>> FLUIDS = holderSet(Registries.FLUID, BuiltInRegistries.FLUID.holderByNameCodec(), BuiltInRegistries.FLUID.byNameCodec());
    public static final MetadataType<Heightmap.Types> HEIGHTMAP_TYPE = new MetadataType<>(Heightmap.Types.class, Heightmap.Types.CODEC);

    public static <T> MetadataType<HolderSet<T>> holderSet(ResourceKey<? extends Registry<T>> resourceKey, Codec<Holder<T>> holderCodec, Codec<T> codec) {
        return new MetadataType<>(castClass(HolderSet.class), SpawnLibCodecs.listOrSingularHolderSet(RegistryCodecs.homogeneousList(resourceKey, codec), holderCodec));
    }

    public static <T> MetadataType<ResourceKey<T>> resourceKey(Codec<ResourceKey<T>> codec) {
        return new MetadataType<>(castClass(ResourceKey.class), codec);
    }

    private static <T> Class<T> castClass(Class<?> clazz) {
        return (Class<T>) clazz;
    }

    public static void registerAll(RegistrationCallback<MetadataType<?>> callback) {
        callback.register(METADATA_TYPE, SpawnLib.id("int"), INT);
        callback.register(METADATA_TYPE, SpawnLib.id("float"), FLOAT);
        callback.register(METADATA_TYPE, SpawnLib.id("double"), DOUBLE);
        callback.register(METADATA_TYPE, SpawnLib.id("boolean"), BOOLEAN);
        callback.register(METADATA_TYPE, SpawnLib.id("optional_vec3"), VEC3);
        callback.register(METADATA_TYPE, SpawnLib.id("dimension"), DIMENSION);
        callback.register(METADATA_TYPE, SpawnLib.id("fluids"), FLUIDS);
        callback.register(METADATA_TYPE, SpawnLib.id("heightmap_type"), HEIGHTMAP_TYPE);
    }
}
