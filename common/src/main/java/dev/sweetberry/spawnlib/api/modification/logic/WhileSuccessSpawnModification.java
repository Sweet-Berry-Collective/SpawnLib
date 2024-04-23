package dev.sweetberry.spawnlib.api.modification.logic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record WhileSuccessSpawnModification(SpawnModification condition, List<SpawnModification> functions) implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("while_success");

    public static final MapCodec<WhileSuccessSpawnModification> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            SpawnModification.CODEC.fieldOf("condition").forGetter(WhileSuccessSpawnModification::condition),
            SpawnModification.CODEC.listOf().optionalFieldOf("functions", List.of()).forGetter(WhileSuccessSpawnModification::functions)
    ).apply(inst, WhileSuccessSpawnModification::new));

    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
        while (condition.modify(context, providers))
            for (var modification : functions)
                modification.modify(context, providers);
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
}
