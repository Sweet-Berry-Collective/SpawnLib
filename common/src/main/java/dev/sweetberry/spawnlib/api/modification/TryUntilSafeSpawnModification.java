package dev.sweetberry.spawnlib.api.modification;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.SpawnPriority;
import dev.sweetberry.spawnlib.api.codec.FieldCodec;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.MetadataType;
import dev.sweetberry.spawnlib.api.metadata.SpawnLibMetadataTypes;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TryUntilSafeSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("try_until_safe");

    public static final Codec<TryUntilSafeSpawnModification> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ExtraCodecs.strictOptionalField(FieldCodec.codec(SpawnLibMetadataTypes.INT), "max_iterations", new Field<>(64)).forGetter(modification -> modification.maxIterations),
            SpawnModification.CODEC.listOf().fieldOf("functions").forGetter(TryUntilSafeSpawnModification::getFunctions)
    ).apply(inst, TryUntilSafeSpawnModification::new));

    private final Field<Integer> maxIterations;
    private final List<SpawnModification> functions;

    public TryUntilSafeSpawnModification(Field<Integer> maxIterations, List<SpawnModification> functions) {
        this.maxIterations = maxIterations;
        this.functions = functions;
    }

    public int getMaxIterations(SpawnContext context, List<MetadataProvider> providers) {
        return this.maxIterations.get(context, providers);
    }

    public List<SpawnModification> getFunctions() {
        return this.functions;
    }

    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
        for (int i = 0; i < getMaxIterations(context, providers); ++i) {
            providers.add(new Provider(i));
            for (SpawnModification modification : functions)
                modification.modify(context, providers);
            providers.remove(providers.size()-1);

            Vec3 spawnPos = context.getSpawnPos().subtract(0, 1, 0);
            if (isValidForSpawningIgnoreFluids(context, context.getLevel(), context.getSpawnPos()) && (!isValidForSpawningIgnoreFluids(context, context.getLevel(), spawnPos) || !context.getLevel().getFluidState(BlockPos.containing(spawnPos)).is(Fluids.EMPTY)))
                return true;
        }

        return false;
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
        return List.of(maxIterations);
    }

    @Override
    public List<SpawnModification> getInnerModifications() {
        return functions;
    }

    public record Provider(int iteration) implements MetadataProvider {
        @Override
        public <T> Optional<T> getData(SpawnPriority priority, @Nullable String scope, String id, MetadataType<T> metadataType) {
            if (!Objects.equals(scope, ID.toString()))
                return Optional.empty();
            if (!Objects.equals(id, "index"))
                return Optional.empty();
            return Optional.of((T)(Object)iteration);
        }
    }
}
