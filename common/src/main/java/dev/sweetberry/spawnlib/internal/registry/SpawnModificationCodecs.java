package dev.sweetberry.spawnlib.internal.registry;

import com.mojang.serialization.Codec;
import dev.sweetberry.spawnlib.api.modification.DimensionSpawnModification;
import dev.sweetberry.spawnlib.api.modification.FindGroundSpawnModification;
import dev.sweetberry.spawnlib.api.modification.HeightmapTypeSpawnModification;
import dev.sweetberry.spawnlib.api.modification.InBoundsSpawnModification;
import dev.sweetberry.spawnlib.api.modification.OffsetPositionSpawnModification;
import dev.sweetberry.spawnlib.api.modification.RandomOffsetSpawnModification;
import dev.sweetberry.spawnlib.api.modification.RestrictToBlockSpawnModification;
import dev.sweetberry.spawnlib.api.modification.RestrictToFluidSpawnModification;
import dev.sweetberry.spawnlib.api.modification.SeaLevelSpawnModification;
import dev.sweetberry.spawnlib.api.modification.SetPositionSpawnModification;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.api.modification.TryUntilSafeSpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.core.Registry;

public class SpawnModificationCodecs {
    public static void registerAll(RegistrationCallback<Codec<? extends SpawnModification>> callback) {
        callback.register(SpawnLibRegistries.SPAWN_MODIFICATION_CODECS, DimensionSpawnModification.ID, DimensionSpawnModification.CODEC);
        callback.register(SpawnLibRegistries.SPAWN_MODIFICATION_CODECS, FindGroundSpawnModification.ID, FindGroundSpawnModification.CODEC);
        callback.register(SpawnLibRegistries.SPAWN_MODIFICATION_CODECS, HeightmapTypeSpawnModification.ID, HeightmapTypeSpawnModification.CODEC);
        callback.register(SpawnLibRegistries.SPAWN_MODIFICATION_CODECS, InBoundsSpawnModification.ID, InBoundsSpawnModification.CODEC);
        callback.register(SpawnLibRegistries.SPAWN_MODIFICATION_CODECS, OffsetPositionSpawnModification.ID, OffsetPositionSpawnModification.CODEC);
        callback.register(SpawnLibRegistries.SPAWN_MODIFICATION_CODECS, RandomOffsetSpawnModification.ID, RandomOffsetSpawnModification.CODEC);
        callback.register(SpawnLibRegistries.SPAWN_MODIFICATION_CODECS, RestrictToBlockSpawnModification.ID, RestrictToBlockSpawnModification.CODEC);
        callback.register(SpawnLibRegistries.SPAWN_MODIFICATION_CODECS, RestrictToFluidSpawnModification.ID, RestrictToFluidSpawnModification.CODEC);
        callback.register(SpawnLibRegistries.SPAWN_MODIFICATION_CODECS, SeaLevelSpawnModification.ID, SeaLevelSpawnModification.CODEC);
        callback.register(SpawnLibRegistries.SPAWN_MODIFICATION_CODECS, SetPositionSpawnModification.ID, SetPositionSpawnModification.CODEC);
        callback.register(SpawnLibRegistries.SPAWN_MODIFICATION_CODECS, TryUntilSafeSpawnModification.ID, TryUntilSafeSpawnModification.CODEC);
    }
}
