package dev.sweetberry.spawnlib.internal;

import dev.sweetberry.spawnlib.api.SpawnModification;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface PlatformHelper {
    Platform getPlatform();

    @Nullable
    SpawnModification getLocalSpawn(Player player);

    @Nullable
    SpawnModification getGlobalSpawn(Player player);

    SpawnModification getGlobalSpawn(Level level);
}
