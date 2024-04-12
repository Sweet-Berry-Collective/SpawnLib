package dev.sweetberry.spawnlib.api.modification.logic;

import com.mojang.serialization.Codec;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class NeverSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("never");

    public static final Codec<NeverSpawnModification> CODEC = Codec.unit(NeverSpawnModification::new);

    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
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
