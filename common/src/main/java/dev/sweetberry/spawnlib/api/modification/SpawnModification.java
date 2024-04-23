package dev.sweetberry.spawnlib.api.modification;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.SpawnLibTags;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.Metadata;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.internal.registry.SpawnLibRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface SpawnModification {
    Codec<SpawnModification> CODEC = SpawnLibRegistries.SPAWN_MODIFICATION_CODECS.byNameCodec().dispatch(SpawnModification::getCodec, codec -> codec);

    /**
     * Modifies the spawn
     * @return true when the modification was successful, false if it failed
     * */
    boolean modify(SpawnContext context, List<MetadataProvider> providers);

    ResourceLocation getId();

    MapCodec<? extends SpawnModification> getCodec();

    /**
     * A list of fields within this SpawnModification.
     * Used internally for resolving metadata.
     */
    default List<Field<?>> getFields() {
        return List.of();
    }

    /*
     * TODO: Comment this.
     */
    default List<Metadata<?>> getBuiltInMetadata() {
        return List.of();
    }

    default List<SpawnModification> getInnerModifications() {
        return List.of();
    }

    default boolean isValidForSpawningIgnoreFluids(SpawnContext context, ServerLevel level, Vec3 pos) {
        var box = context.getPlayerBoundingBox(pos);
        // This might not be the right method to call, we'll figure that out later.
        return level.noCollision(box);
    }

    default boolean isValidForSpawning(SpawnContext context, ServerLevel level, Vec3 pos) {
        return isValidForSpawningIgnoreFluids(context, level, pos) && !level.getFluidState(BlockPos.containing(pos)).is(SpawnLibTags.SPAWN_BLOCKING);
    }

    default boolean isValidForSpawning(SpawnContext context, Vec3 pos) {
        return isValidForSpawning(context, context.getLevel(), pos);
    }
}
