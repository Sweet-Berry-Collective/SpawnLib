package dev.sweetberry.spawnlib.api.modification.logic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.api.modification.TryUntilSafeSpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.List;

public record IfSuccessSpawnModification(SpawnModification condition, List<SpawnModification> functions) implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("if_success");

    public static final Codec<IfSuccessSpawnModification> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SpawnModification.CODEC.fieldOf("condition").forGetter(IfSuccessSpawnModification::condition),
            ExtraCodecs.strictOptionalField(SpawnModification.CODEC.listOf(), "functions", List.of()).forGetter(IfSuccessSpawnModification::functions)
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
    public Codec<? extends SpawnModification> getCodec() {
        return CODEC;
    }
}
