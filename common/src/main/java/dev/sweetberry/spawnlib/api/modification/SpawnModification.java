package dev.sweetberry.spawnlib.api.modification;

import com.mojang.serialization.Codec;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.SpawnLibTags;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import dev.sweetberry.spawnlib.internal.registry.SpawnModificationCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface SpawnModification {
    Codec<SpawnModification> CODEC = SpawnModificationCodecs.SPAWN_MODIFICATION_CODECS.byNameCodec().dispatch(SpawnModification::getCodec, codec -> codec);

    /**
     * Modifies the spawn
     * @return true when the modification was successful, false if it failed
     * */
    boolean modify(SpawnContext context);

    ResourceLocation getId();

    Codec<? extends SpawnModification> getCodec();

    /**
     * A list of fields within this SpawnModification.
     * Used internally for resolving metadata.
     */
    List<Field<?>> getFields();

    /**
     * Whether to clear the modification if it fails
     * */
    default boolean clearOnFail() {
        return false;
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

    default boolean setSpawnIfValid(SpawnContext context, ServerLevel level, Vec3 pos) {
        if (!isValidForSpawning(context, level, pos))
            return false;
        context.setLevel(level);
        context.setSpawnPos(pos);
        return true;
    }

    default boolean setSpawnIfValid(SpawnContext context, Vec3 pos) {
        return setSpawnIfValid(context, context.getLevel(), pos);
    }

    default Vec3 findGround(SpawnContext context, ServerLevel level, Vec3 pos) {
        while (true) {
            // TODO: Come up with something better here.
            if (pos.y < level.getMinBuildHeight())
                return pos;
            SpawnLib.LOGGER.info(pos.toString());
            var currValid = isValidForSpawning(context, level, pos);
            var downValid = isValidForSpawning(context, level, pos.subtract(0, 1, 0));
            if (currValid && !downValid)
                // This is the lowest valid spot.
                return pos;
            if (!currValid)
                // Move up to check block above
                pos = pos.add(0, 1, 0);
            else // Down is always valid here
                // Move down to check block below
                pos = pos.subtract(0, 1, 0);
        }
    }

    default Vec3 randomCircularOffset(SpawnContext context, RandomSource random, Vec3 center, double radius) {
        var distance = radius * Math.sqrt(random.nextDouble());
        var angle = random.nextDouble() * Mth.TWO_PI;
        var deltaX = distance * Math.cos(angle);
        var deltaZ = distance * Math.sin(angle);
        return center.add(deltaX, 0, deltaZ);
    }

    default Vec3 randomSquareOffset(SpawnContext context, RandomSource random, Vec3 center, double radius) {
        var diameter = 2 * radius;
        var deltaX = random.nextDouble() * diameter - radius;
        var deltaZ = random.nextDouble() * diameter - radius;
        return center.add(deltaX, 0, deltaZ);
    }

    @FunctionalInterface
    interface Provider {
        SpawnModification provide();
    }
}
