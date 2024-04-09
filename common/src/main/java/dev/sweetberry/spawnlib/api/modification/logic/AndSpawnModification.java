package dev.sweetberry.spawnlib.api.modification.logic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record AndSpawnModification(SpawnModification first, SpawnModification second) implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("and");

    public static final Codec<AndSpawnModification> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SpawnModification.CODEC.fieldOf("first").forGetter(AndSpawnModification::first),
            SpawnModification.CODEC.fieldOf("second").forGetter(AndSpawnModification::second)
    ).apply(inst, AndSpawnModification::new));

    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
        return first.modify(context, providers) && second.modify(context, providers);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Codec<? extends SpawnModification> getCodec() {
        return CODEC;
    }
}
