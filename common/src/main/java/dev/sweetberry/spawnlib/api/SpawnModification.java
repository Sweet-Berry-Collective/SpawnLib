package dev.sweetberry.spawnlib.api;

import dev.sweetberry.spawnlib.api.modifications.DimensionSpawnModification;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;

public interface SpawnModification {
    static CompoundTag writeToTag(SpawnModification spawn) {
        var tag = new CompoundTag();
        tag.putString("id", spawn.getId().toString());
        var data = new CompoundTag();
        spawn.toTag(data);
        tag.put("data", data);
        return tag;
    }

    static SpawnModification readFromTag(CompoundTag tag) {
        // TODO
        return new DimensionSpawnModification();
    }

    boolean modify(SpawnContext context);

    void toTag(CompoundTag nbt);

    void fromTag(CompoundTag nbt);

    ResourceLocation getId();

    default boolean isValidForSpawning(SpawnContext context, ServerLevel level, Vec3 pos) {
        var box = context.getPlayerBoundingBox(pos);
        // This might not be the right method to call, we'll figure that out later.
        return level.noCollision(box);
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

    default Vec3 findLowestValidSpawn(SpawnContext context, ServerLevel level, Vec3 pos) {
        while (true) {
            var currValid = isValidForSpawning(context, level, pos);
            var downValid = !isValidForSpawning(context, level, pos.subtract(0, 1, 0));
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
