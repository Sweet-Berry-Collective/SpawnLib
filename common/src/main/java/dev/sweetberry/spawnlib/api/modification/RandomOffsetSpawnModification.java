package dev.sweetberry.spawnlib.api.modification;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.codec.FieldCodec;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.SpawnLibMetadataTypes;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RandomOffsetSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("random_offset");

    public static final MapCodec<RandomOffsetSpawnModification> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            FieldCodec.codec(SpawnLibMetadataTypes.INT).optionalFieldOf("radius").forGetter(modification -> modification.radius),
            FieldCodec.codec(SpawnLibMetadataTypes.BOOLEAN).optionalFieldOf("circular", new Field<>(false)).forGetter(modification -> modification.circular)
    ).apply(inst, RandomOffsetSpawnModification::new));

    private final Optional<Field<Integer>> radius;
    private final Field<Boolean> circular;

    public RandomOffsetSpawnModification(Optional<Field<Integer>> radius, Field<Boolean> circular) {
        this.radius = radius;
        this.circular = circular;
    }

    public int getRadius(SpawnContext context, List<MetadataProvider> providers) {
        if (radius.isEmpty())
            return context.getServer().getSpawnRadius(context.getLevel());
        return radius.get().get(context, providers);
    }

    public boolean isCircular(SpawnContext context, List<MetadataProvider> providers) {
        return circular.get(context, providers);
    }

    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
        ServerLevel level = context.getLevel();

        context.setSpawnPos(
                isCircular(context, providers)
                        ? randomCircularOffset(level.random, context.getSpawnPos(), getRadius(context, providers))
                        : randomSquareOffset(level.random, context.getSpawnPos(), getRadius(context, providers))
        );
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends SpawnModification> getCodec() {
        return CODEC;
    }

    @Override
    public List<Field<?>> getFields() {
        var list = new ArrayList<Field<?>>();
        radius.ifPresent(list::add);
        list.add(circular);
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
