package dev.sweetberry.spawnlib.internal.registry;

import com.mojang.serialization.MapCodec;
import dev.sweetberry.spawnlib.api.modification.*;
import dev.sweetberry.spawnlib.api.modification.logic.*;
import net.minecraft.resources.ResourceLocation;

public class SpawnModificationCodecs {
    public static void registerAll(RegistrationCallback<MapCodec<? extends SpawnModification>> callback) {
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
        register(callback, WorldSpawnModification.ID, WorldSpawnModification.CODEC);
        register(callback, IsGamemodeSpawnModification.ID, IsGamemodeSpawnModification.CODEC);
        register(callback, SnapToCenterSpawnModification.ID, SnapToCenterSpawnModification.CODEC);
    }

    public static void register(RegistrationCallback<MapCodec<? extends SpawnModification>> callback, ResourceLocation id, MapCodec<? extends SpawnModification> codec) {
        callback.register(SpawnLibRegistries.SPAWN_MODIFICATION_CODECS, id, codec);
    }
}
