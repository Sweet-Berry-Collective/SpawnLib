package dev.sweetberry.spawnlib.api;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface SpawnModification {
    boolean modify(SpawnContext context);

    default boolean isValidForSpawning(SpawnContext context, Level level, Vec3 pos) {
        var box = context.getPlayerBoundingBox(pos);
        // This might not be the right method to call, we'll figure that out later.
        return level.noCollision(box);
    }

    default boolean isValidForSpawning(SpawnContext context, Vec3 pos) {
        return isValidForSpawning(context, context.getLevel(), pos);
    }

    default boolean setSpawnIfValid(SpawnContext context, Level level, Vec3 pos) {
        if (!isValidForSpawning(context, level, pos))
            return false;
        context.setLevel(level);
        context.setSpawnPos(pos);
        return true;
    }

    default boolean setSpawnIfValid(SpawnContext context, Vec3 pos) {
        return setSpawnIfValid(context, context.getLevel(), pos);
    }
}
