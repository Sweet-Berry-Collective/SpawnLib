package dev.sweetberry.spawnlib.api.modification.logic;

import com.mojang.serialization.Codec;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class IsValidSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("is_valid");

    public static final Codec<IsValidSpawnModification> CODEC = Codec.unit(IsValidSpawnModification::new);

    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
        return isValidForSpawning(context, context.getLevel(), context.getSpawnPos());
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
