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
import dev.sweetberry.spawnlib.api.modification.logic.*;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class SpawnModificationCodecs {
    public static void registerAll(RegistrationCallback<Codec<? extends SpawnModification>> callback) {
        register(callback, DimensionSpawnModification.ID, DimensionSpawnModification.CODEC);
        register(callback, FindGroundSpawnModification.ID, FindGroundSpawnModification.CODEC);
        register(callback, HeightmapTypeSpawnModification.ID, HeightmapTypeSpawnModification.CODEC);
        register(callback, InBoundsSpawnModification.ID, InBoundsSpawnModification.CODEC);
        register(callback, OffsetPositionSpawnModification.ID, OffsetPositionSpawnModification.CODEC);
        register(callback, RandomOffsetSpawnModification.ID, RandomOffsetSpawnModification.CODEC);
        register(callback, RestrictToBlockSpawnModification.ID, RestrictToBlockSpawnModification.CODEC);
        register(callback, RestrictToFluidSpawnModification.ID, RestrictToFluidSpawnModification.CODEC);
        register(callback, SeaLevelSpawnModification.ID, SeaLevelSpawnModification.CODEC);
        register(callback, SetPositionSpawnModification.ID, SetPositionSpawnModification.CODEC);
        register(callback, TryUntilSafeSpawnModification.ID, TryUntilSafeSpawnModification.CODEC);
        register(callback, AlwaysSpawnModification.ID, AlwaysSpawnModification.CODEC);
        register(callback, NeverSpawnModification.ID, NeverSpawnModification.CODEC);
        register(callback, IfSuccessSpawnModification.ID, IfSuccessSpawnModification.CODEC);
        register(callback, IfFailureSpawnModification.ID, IfFailureSpawnModification.CODEC);
        register(callback, WhileSuccessSpawnModification.ID, WhileSuccessSpawnModification.CODEC);
        register(callback, WhileFailureSpawnModification.ID, WhileFailureSpawnModification.CODEC);
        register(callback, IsValidSpawnModification.ID, IsValidSpawnModification.CODEC);
        register(callback, BlockSpawnModification.ID, BlockSpawnModification.CODEC);
        register(callback, NotSpawnModification.ID, NotSpawnModification.CODEC);
        register(callback, AndSpawnModification.ID, AndSpawnModification.CODEC);
        register(callback, OrSpawnModification.ID, OrSpawnModification.CODEC);
        register(callback, XorSpawnModification.ID, XorSpawnModification.CODEC);
    }

    public static void register(RegistrationCallback<Codec<? extends SpawnModification>> callback, ResourceLocation id, Codec<? extends SpawnModification> codec) {
        callback.register(SpawnLibRegistries.SPAWN_MODIFICATION_CODECS, id, codec);
    }
}
