package dev.sweetberry.spawnlib.api.modification;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.codec.SpawnLibFieldCodec;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.SpawnLibMetadataTypes;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.List;

public class DimensionSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("in_dimension");

    public static final Codec<DimensionSpawnModification> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SpawnLibFieldCodec.codec(SpawnLibMetadataTypes.DIMENSION).fieldOf("dimension").forGetter(modification -> modification.dimension)
    ).apply(inst, DimensionSpawnModification::new));


    private final Field<ResourceKey<Level>> dimension;

    public DimensionSpawnModification(Field<ResourceKey<Level>> dimension) {
        this.dimension = dimension;
    }

    public DimensionSpawnModification(ResourceKey<Level> dimension) {
        this.dimension = new Field<>(dimension);
    }

    public DimensionSpawnModification() {
        this(Level.OVERWORLD);
    }

    public ResourceKey<Level> getDimension(SpawnContext context, List<MetadataProvider> providers) {
        return dimension.get(context, providers);
    }

    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
        var level = context.getLevel(getDimension(context, providers));
        context.setLevel(level);
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Codec<? extends SpawnModification> getCodec() {
        return CODEC;
    }

    @Override
    public List<Field<?>> getFields() {
        return List.of(this.dimension);
    }
}
