package dev.sweetberry.spawnlib.api.modification;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.codec.SpawnLibFieldCodec;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.SpawnLibMetadataTypes;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class OffsetPositionSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("offset_position");

    public static final Codec<OffsetPositionSpawnModification> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SpawnLibFieldCodec.codec(SpawnLibMetadataTypes.VEC3).fieldOf("position").forGetter(modification -> modification.position)
    ).apply(inst, OffsetPositionSpawnModification::new));

    private final Field<Vec3> position;

    public OffsetPositionSpawnModification(Field<Vec3> position) {
        this.position = position;
    }

    public Vec3 getPosition() {
        return position.get();
    }

    @Override
    public boolean modify(SpawnContext context) {
        Vec3 vec3 = context.getSpawnPos().add(getX().orElse(0.0), getY().orElse(0.0), getZ().orElse(0.0));
        if (!vec3.equals(context.getSpawnPos())) {
            context.setSpawnPos(vec3);
        }
        return true;
    }

    public Optional<Double> getX() {
        return getOptionalBound(this.position.get().x);
    }

    public Optional<Double> getY() {
        return getOptionalBound(this.position.get().y);
    }

    public Optional<Double> getZ() {
        return getOptionalBound(this.position.get().z);
    }

    private Optional<Double> getOptionalBound(double value) {
        if (Double.isNaN(value)) {
            return Optional.empty();
        }
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
