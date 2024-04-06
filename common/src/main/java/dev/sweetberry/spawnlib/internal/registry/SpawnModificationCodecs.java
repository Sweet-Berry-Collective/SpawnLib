package dev.sweetberry.spawnlib.internal.registry;

import com.mojang.serialization.Codec;
import dev.sweetberry.spawnlib.api.modification.DimensionSpawnModification;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;

public class SpawnModificationCodecs {
    public static void registerAll(RegistrationCallback<Codec<? extends SpawnModification>> callback) {
       callback.register(SpawnLib.getHelper().getSpawnModificationCodecRegistry(), DimensionSpawnModification.ID, DimensionSpawnModification.CODEC);
    }
}
