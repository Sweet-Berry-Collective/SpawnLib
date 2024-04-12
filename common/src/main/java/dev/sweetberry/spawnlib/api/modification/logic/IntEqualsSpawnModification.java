package dev.sweetberry.spawnlib.api.modification.logic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.codec.SpawnLibFieldCodec;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.SpawnLibMetadataTypes;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Objects;

public record IntEqualsSpawnModification(Field<Integer> first, Field<Integer> second) implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("int_equals");

    public static final Codec<IntEqualsSpawnModification> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SpawnLibFieldCodec.codec(SpawnLibMetadataTypes.INT).fieldOf("first").forGetter(IntEqualsSpawnModification::first),
            SpawnLibFieldCodec.codec(SpawnLibMetadataTypes.INT).fieldOf("second").forGetter(IntEqualsSpawnModification::second)
    ).apply(inst, IntEqualsSpawnModification::new));

    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
        return Objects.equals(first.get(context, providers), second.get(context, providers));
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
