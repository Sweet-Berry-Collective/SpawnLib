package dev.sweetberry.spawnlib.api.modification.logic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record IfSuccessSpawnModification(SpawnModification condition, List<SpawnModification> functions) implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("if_success");

    public static final MapCodec<IfSuccessSpawnModification> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            SpawnModification.CODEC.fieldOf("condition").forGetter(IfSuccessSpawnModification::condition),
            SpawnModification.CODEC.listOf().optionalFieldOf("functions", List.of()).forGetter(IfSuccessSpawnModification::functions)
    ).apply(inst, IfSuccessSpawnModification::new));

    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
        if (condition.modify(context, providers)) {
            for (var modification : functions)
                modification.modify(context, providers);
            return true;
        }
        return false;
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
