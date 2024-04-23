package dev.sweetberry.spawnlib.api.modification;

import com.mojang.serialization.MapCodec;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class WorldSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("world_spawn");
    public static final MapCodec<WorldSpawnModification> CODEC = MapCodec.unit(WorldSpawnModification::new);

    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
        context.setSpawnPos(context.getServer().overworld().getSharedSpawnPos().getCenter());
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
