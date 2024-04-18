package dev.sweetberry.spawnlib.api.modification;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.codec.FieldCodec;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.SpawnLibMetadataTypes;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class OffsetPositionSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("offset_position");

    public static final Codec<OffsetPositionSpawnModification> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            FieldCodec.codec(SpawnLibMetadataTypes.VEC3).fieldOf("position").forGetter(modification -> modification.position)
    ).apply(inst, OffsetPositionSpawnModification::new));

    private final Field<Vec3> position;

    public OffsetPositionSpawnModification(Field<Vec3> position) {
        this.position = position;
    }

    public Vec3 getPosition(SpawnContext context, List<MetadataProvider> providers) {
        return position.get(context, providers);
    }

    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
        Vec3 vec3 = context.getSpawnPos().add(getX(context, providers).orElse(0.0), getY(context, providers).orElse(0.0), getZ(context, providers).orElse(0.0));
        if (!vec3.equals(context.getSpawnPos()))
            context.setSpawnPos(vec3);
        return true;
    }

    public Optional<Double> getX(SpawnContext context, List<MetadataProvider> providers) {
        return getOptionalBound(this.position.get(context, providers).x);
    }

    public Optional<Double> getY(SpawnContext context, List<MetadataProvider> providers) {
        return getOptionalBound(this.position.get(context, providers).y);
    }

    public Optional<Double> getZ(SpawnContext context, List<MetadataProvider> providers) {
        return getOptionalBound(this.position.get(context, providers).z);
    }

    private Optional<Double> getOptionalBound(double value) {
        if (Double.isNaN(value))
            return Optional.empty();
        return Optional.of(value);
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
        return List.of(this.position);
    }
}
