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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RandomOffsetSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("random_offset");

    public static final Codec<RandomOffsetSpawnModification> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ExtraCodecs.strictOptionalField(SpawnLibFieldCodec.codec(SpawnLibMetadataTypes.INT), "radius").forGetter(modification -> modification.radius),
            ExtraCodecs.strictOptionalField(SpawnLibFieldCodec.codec(SpawnLibMetadataTypes.BOOLEAN), "circular").forGetter(modification -> modification.circular)
    ).apply(inst, RandomOffsetSpawnModification::new));

    private final Optional<Field<Integer>> radius;
    private final Optional<Field<Boolean>> circular;

    public RandomOffsetSpawnModification(Optional<Field<Integer>> radius, Optional<Field<Boolean>> circular) {
        this.radius = radius;
        this.circular = circular;
    }

    public int getRadius(SpawnContext context) {
        if (radius.isEmpty())
            return context.getServer().getSpawnRadius(context.getLevel());
        return radius.get().get();
    }

    public boolean isCircular() {
        return circular.isPresent() && circular.get().get();
    }

    @Override
    public boolean modify(SpawnContext context) {
        ServerLevel level = context.getLevel();

        context.setSpawnPos(
                isCircular()
                        ? randomCircularOffset(level.random, context.getSpawnPos(), getRadius(context))
                        : randomSquareOffset(level.random, context.getSpawnPos(), getRadius(context))
        );
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
        var list = new ArrayList<Field<?>>();
        radius.ifPresent(list::add);
        circular.ifPresent(list::add);
        return list;
    }

    public Vec3 randomCircularOffset(RandomSource random, Vec3 center, double radius) {
        var distance = radius * Math.sqrt(random.nextDouble());
        var angle = random.nextDouble() * Mth.TWO_PI;
        var deltaX = distance * Math.cos(angle);
        var deltaZ = distance * Math.sin(angle);
        return center.add(deltaX, 0, deltaZ);
    }

    public Vec3 randomSquareOffset(RandomSource random, Vec3 center, double radius) {
        var diameter = 2 * radius;
        var deltaX = random.nextDouble() * diameter - radius;
        var deltaZ = random.nextDouble() * diameter - radius;
        return center.add(deltaX, 0, deltaZ);
    }
}
