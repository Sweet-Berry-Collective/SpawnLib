package dev.sweetberry.spawnlib.api.modification.logic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record XorSpawnModification(SpawnModification first, SpawnModification second) implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("xor");

    public static final MapCodec<XorSpawnModification> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            SpawnModification.CODEC.fieldOf("first").forGetter(XorSpawnModification::first),
            SpawnModification.CODEC.fieldOf("second").forGetter(XorSpawnModification::second)
    ).apply(inst, XorSpawnModification::new));

    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
        return first.modify(context, providers) ^ second.modify(context, providers);
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
