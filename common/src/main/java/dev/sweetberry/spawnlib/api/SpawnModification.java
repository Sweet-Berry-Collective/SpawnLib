package dev.sweetberry.spawnlib.api;

import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface SpawnModification {
    boolean modify(SpawnContext context);

    void toTag(Tag nbt);

    void fromTag(Tag nbt);

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

    @FunctionalInterface
    interface Provider {
        SpawnModification provide();
    }
}
