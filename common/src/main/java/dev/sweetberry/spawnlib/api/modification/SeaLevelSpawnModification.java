package dev.sweetberry.spawnlib.api.modification;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.codec.SpawnLibFieldCodec;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.SpawnLibMetadataTypes;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SeaLevelSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("sea_level");

    public static final Codec<SeaLevelSpawnModification> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ExtraCodecs.strictOptionalField(SpawnLibFieldCodec.codec(SpawnLibMetadataTypes.HEIGHTMAP_TYPE), "heightmap_type", new Field<>(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES)).forGetter(modification -> modification.heightmapType)
    ).apply(inst, SeaLevelSpawnModification::new));

    private final Field<Heightmap.Types> heightmapType;

    public SeaLevelSpawnModification(Field<Heightmap.Types> heightmapType) {
        this.heightmapType = heightmapType;
    }

    @Override
    public boolean modify(SpawnContext context) {
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

    @Override
    public List<Field<?>> getFields() {
        return List.of(this.heightmapType);
    }
}
