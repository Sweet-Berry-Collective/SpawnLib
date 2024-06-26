package dev.sweetberry.spawnlib.api.modification;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.codec.FieldCodec;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.SpawnLibMetadataTypes;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class HeightmapTypeSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("heightmap");

    public static final MapCodec<HeightmapTypeSpawnModification> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            FieldCodec.codec(SpawnLibMetadataTypes.HEIGHTMAP_TYPE).optionalFieldOf("heightmap_type", new Field<>(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES)).forGetter(modification -> modification.heightmapType)
    ).apply(inst, HeightmapTypeSpawnModification::new));

    private final Field<Heightmap.Types> heightmapType;

    public HeightmapTypeSpawnModification(Field<Heightmap.Types> heightmapType) {
        this.heightmapType = heightmapType;
    }

    public Heightmap.Types getHeightmapType(SpawnContext context, List<MetadataProvider> providers) {
        return this.heightmapType.get(context, providers);
    }

    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
        context.setSpawnPos(Vec3.atBottomCenterOf(context.getLevel().getHeightmapPos(getHeightmapType(context, providers), BlockPos.containing(context.getSpawnPos()))));
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

    @Override
    public List<Field<?>> getFields() {
        return List.of(this.heightmapType);
    }
}
