package dev.sweetberry.spawnlib.api;

@FunctionalInterface
public interface SpawnModification {
    boolean modify(SpawnContext context);
}
