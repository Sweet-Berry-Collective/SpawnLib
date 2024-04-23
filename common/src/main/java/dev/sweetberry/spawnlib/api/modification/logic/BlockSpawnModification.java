package dev.sweetberry.spawnlib.api.modification.logic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record BlockSpawnModification(List<SpawnModification> functions) implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("block");

    public static final MapCodec<BlockSpawnModification> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            SpawnModification.CODEC.listOf().optionalFieldOf("functions", List.of()).forGetter(BlockSpawnModification::functions)
    ).apply(inst, BlockSpawnModification::new));


    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
        boolean last = false;
        for (var modification : functions)
            last = modification.modify(context, providers);
        return last;
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
