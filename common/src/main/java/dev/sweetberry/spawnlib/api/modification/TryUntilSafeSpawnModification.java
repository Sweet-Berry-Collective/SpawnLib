package dev.sweetberry.spawnlib.api.modification;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.codec.SpawnLibFieldCodec;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.SpawnLibMetadataTypes;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TryUntilSafeSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("try_until_safe");

    public static final Codec<TryUntilSafeSpawnModification> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ExtraCodecs.strictOptionalField(SpawnLibFieldCodec.codec(SpawnLibMetadataTypes.INT), "max_iterations", new Field<>(64)).forGetter(modification -> modification.maxIterations),
            ExtraCodecs.strictOptionalField(SpawnModification.CODEC.listOf(), "functions", List.of()).forGetter(TryUntilSafeSpawnModification::getFunctions)
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
        ServerPlayer player = context.getPlayer();
        SpawnContext copiedContext = new SpawnContext(player);

        outer: for (int i = 0; i < getMaxIterations(context, providers); ++i) {
            copiedContext.copy(context);
            for (SpawnModification modification : functions)
                if (!modification.modify(copiedContext, providers))
                    continue outer;

            Vec3 spawnPos = copiedContext.getSpawnPos().subtract(0, 1, 0);
            if (isValidForSpawningIgnoreFluids(copiedContext, copiedContext.getLevel(), copiedContext.getSpawnPos()) && (!isValidForSpawningIgnoreFluids(copiedContext, copiedContext.getLevel(), spawnPos) || !copiedContext.getLevel().getFluidState(BlockPos.containing(spawnPos)).is(Fluids.EMPTY))) {
                context.copy(copiedContext);
                return true;
            }
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


}
