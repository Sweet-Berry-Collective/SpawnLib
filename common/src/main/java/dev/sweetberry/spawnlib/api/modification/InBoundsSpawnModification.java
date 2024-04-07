package dev.sweetberry.spawnlib.api.modification;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.codec.SpawnLibFieldCodec;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.SpawnLibMetadataTypes;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class InBoundsSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("in_bounds");

    public static final Codec<InBoundsSpawnModification> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ExtraCodecs.strictOptionalField(SpawnLibFieldCodec.codec(SpawnLibMetadataTypes.VEC3), "min").forGetter(modification -> modification.min),
            ExtraCodecs.strictOptionalField(SpawnLibFieldCodec.codec(SpawnLibMetadataTypes.VEC3), "max").forGetter(modification -> modification.max)
    ).apply(inst, InBoundsSpawnModification::new));

    private Optional<Field<Vec3>> min;
    private Optional<Field<Vec3>> max;

    public InBoundsSpawnModification(Optional<Field<Vec3>> min, Optional<Field<Vec3>> max) {
        this.min = min;
        this.max = max;
    }

    private static double bounded(Optional<Double> min, Optional<Double> max, double value) {
        if (min.isPresent() && value < min.get())
            return min.get();
        if (max.isPresent() && value > max.get())
            return max.get();
        return value;
    }

    @Override
    public boolean modify(SpawnContext context) {
        var pos = context.getSpawnPos();

        pos = new Vec3(
                bounded(getMinX(), getMaxX(), pos.x),
                bounded(getMinY(), getMaxY(), pos.y),
                bounded(getMinZ(), getMaxZ(), pos.z)
        );

        if (pos != context.getSpawnPos())
            context.setSpawnPos(pos);
        return true;
    }

    public Optional<Double> getMinX() {
        return this.min.flatMap(field -> getOptionalBound(field.get().x));
    }

    public Optional<Double> getMinY() {
        return this.min.flatMap(field -> getOptionalBound(field.get().y));
    }

    public Optional<Double> getMinZ() {
        return this.min.flatMap(field -> getOptionalBound(field.get().z));
    }

    public Optional<Double> getMaxX() {
        return this.max.flatMap(field -> getOptionalBound(field.get().x));
    }

    public Optional<Double> getMaxY() {
        return this.max.flatMap(field -> getOptionalBound(field.get().y));
    }

    public Optional<Double> getMaxZ() {
        return this.max.flatMap(field -> getOptionalBound(field.get().z));
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
        return List.of();
    }
}
