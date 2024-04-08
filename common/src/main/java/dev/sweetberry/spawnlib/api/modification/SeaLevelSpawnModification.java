package dev.sweetberry.spawnlib.api.modification;

import com.mojang.serialization.Codec;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SeaLevelSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("sea_level");

    public static final Codec<SeaLevelSpawnModification> CODEC = Codec.unit(SeaLevelSpawnModification::new);

    public SeaLevelSpawnModification() {
    }

    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
        context.setSpawnPos(new Vec3(context.getSpawnPos().x, context.getLevel().getChunkSource().getGenerator().getSeaLevel(), context.getSpawnPos().z));
        return true;
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
