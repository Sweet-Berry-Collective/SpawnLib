package dev.sweetberry.spawnlib.api.modification.logic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record NotSpawnModification(SpawnModification condition) implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("not");

    public static final MapCodec<NotSpawnModification> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            SpawnModification.CODEC.fieldOf("condition").forGetter(NotSpawnModification::condition)
    ).apply(inst, NotSpawnModification::new));

    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
        return !condition.modify(context, providers);
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
