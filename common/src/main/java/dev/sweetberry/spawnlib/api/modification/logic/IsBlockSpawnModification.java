package dev.sweetberry.spawnlib.api.modification.logic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.codec.FieldCodec;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.SpawnLibMetadataTypes;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public record IsBlockSpawnModification(Field<BlockState> state) implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("is_block");

    public static final MapCodec<IsBlockSpawnModification> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            FieldCodec.codec(SpawnLibMetadataTypes.BLOCK_STATE).fieldOf("state").forGetter(IsBlockSpawnModification::state)
    ).apply(inst, IsBlockSpawnModification::new));


    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
        var pos = context.getSpawnPos();
        return context.getLevel().getBlockState(BlockPos.containing(pos.x, pos.y, pos.z)) == state.get(context, providers);
    }

    @Override
    public ResourceLocation getId() {
        return null;
    }

    @Override
    public MapCodec<? extends SpawnModification> getCodec() {
        return null;
    }
}
