package dev.sweetberry.spawnlib.api.metadata;

import com.mojang.serialization.Codec;
import dev.sweetberry.spawnlib.api.codec.SpawnLibCodecs;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import dev.sweetberry.spawnlib.internal.registry.RegistrationCallback;
import dev.sweetberry.spawnlib.internal.registry.SpawnLibRegistries;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;

public class SpawnLibMetadataTypes {
    public static final MetadataType<Integer> INT = new MetadataType<>(Integer.class, Codec.INT);
    public static final MetadataType<Double> DOUBLE = new MetadataType<>(Double.class, Codec.DOUBLE);
    public static final MetadataType<Float> FLOAT = new MetadataType<>(Float.class, Codec.FLOAT);
    public static final MetadataType<Boolean> BOOLEAN = new MetadataType<>(Boolean.class, Codec.BOOL);
    public static final MetadataType<Vec3> VEC3 = new MetadataType<>(Vec3.class, SpawnLibCodecs.OPTIONAL_VEC3);
    public static final MetadataType<BoundingBox> BOUNDING_BOX = new MetadataType<>(BoundingBox.class, BoundingBox.CODEC);

    public static final MetadataType<ResourceKey<Level>> DIMENSION = resourceKey(Level.RESOURCE_KEY_CODEC);
    public static final MetadataType<HolderSet<Block>> BLOCKS = holderSet(Registries.BLOCK, BuiltInRegistries.BLOCK.holderByNameCodec(), BuiltInRegistries.BLOCK.byNameCodec());
    public static final MetadataType<HolderSet<Fluid>> FLUIDS = holderSet(Registries.FLUID, BuiltInRegistries.FLUID.holderByNameCodec(), BuiltInRegistries.FLUID.byNameCodec());
    public static final MetadataType<Heightmap.Types> HEIGHTMAP_TYPE = new MetadataType<>(Heightmap.Types.class, Heightmap.Types.CODEC);

    public static final MetadataType<GameType> GAME_TYPE = new MetadataType<>(GameType.class, SpawnLibCodecs.GAME_TYPE);

    public static final MetadataType<BlockState> BLOCK_STATE = new MetadataType<>(BlockState.class, BlockState.CODEC);

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
        callback.register(SpawnLibRegistries.METADATA_TYPE, SpawnLib.id("int"), INT);
        callback.register(SpawnLibRegistries.METADATA_TYPE, SpawnLib.id("float"), FLOAT);
        callback.register(SpawnLibRegistries.METADATA_TYPE, SpawnLib.id("double"), DOUBLE);
        callback.register(SpawnLibRegistries.METADATA_TYPE, SpawnLib.id("boolean"), BOOLEAN);
        callback.register(SpawnLibRegistries.METADATA_TYPE, SpawnLib.id("position"), VEC3);
        callback.register(SpawnLibRegistries.METADATA_TYPE, SpawnLib.id("bounding_box"), BOUNDING_BOX);
        callback.register(SpawnLibRegistries.METADATA_TYPE, SpawnLib.id("dimension"), DIMENSION);
        callback.register(SpawnLibRegistries.METADATA_TYPE, SpawnLib.id("blocks"), BLOCKS);
        callback.register(SpawnLibRegistries.METADATA_TYPE, SpawnLib.id("fluids"), FLUIDS);
        callback.register(SpawnLibRegistries.METADATA_TYPE, SpawnLib.id("heightmap_type"), HEIGHTMAP_TYPE);
        callback.register(SpawnLibRegistries.METADATA_TYPE, SpawnLib.id("gamemode"), GAME_TYPE);
        callback.register(SpawnLibRegistries.METADATA_TYPE, SpawnLib.id("block_state"), BLOCK_STATE);
    }
}
