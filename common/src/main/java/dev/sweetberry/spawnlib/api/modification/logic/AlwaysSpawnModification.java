package dev.sweetberry.spawnlib.api.modification.logic;

import com.mojang.serialization.MapCodec;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class AlwaysSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("always");

    public static final MapCodec<AlwaysSpawnModification> CODEC = MapCodec.unit(AlwaysSpawnModification::new);

    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
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
