package dev.sweetberry.spawnlib.api.modification;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.codec.SpawnLibFieldCodec;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.SpawnLibMetadataTypes;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Optional;

public class RandomOffsetSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("random_offset");

    public static final Codec<RandomOffsetSpawnModification> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ExtraCodecs.strictOptionalField(SpawnLibFieldCodec.codec(SpawnLibMetadataTypes.INT), "radius").forGetter(modification -> modification.radius)
    ).apply(inst, RandomOffsetSpawnModification::new));

    private final Optional<Field<Integer>> radius;

    public RandomOffsetSpawnModification(Optional<Field<Integer>> radius) {
        this.radius = radius;
    }

    public int getRadius(SpawnContext context) {
        if (this.radius.isEmpty()) {
            return context.getServer().getSpawnRadius(context.getLevel());
        }
        return this.radius.get().get();
    }

    @Override
    public boolean modify(SpawnContext context) {
        ServerLevel level = context.getLevel();
        context.setSpawnPos(randomSquareOffset(context, level.random, context.getSpawnPos(), getRadius(context)));
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
        return this.radius.<List<Field<?>>>map(List::of).orElseGet(List::of);
    }
}
