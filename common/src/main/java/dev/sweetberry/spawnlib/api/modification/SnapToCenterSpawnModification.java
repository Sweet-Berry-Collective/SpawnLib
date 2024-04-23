package dev.sweetberry.spawnlib.api.modification;

import com.mojang.serialization.MapCodec;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SnapToCenterSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("snap_to_center");

    public static final MapCodec<SnapToCenterSpawnModification> CODEC = MapCodec.unit(SnapToCenterSpawnModification::new);

    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
        var pos = context.getSpawnPos();
        pos = BlockPos.containing(pos.x, pos.y, pos.z).getCenter();
        pos = new Vec3(pos.x, Math.floor(pos.y), pos.z);
        context.setSpawnPos(pos);
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
