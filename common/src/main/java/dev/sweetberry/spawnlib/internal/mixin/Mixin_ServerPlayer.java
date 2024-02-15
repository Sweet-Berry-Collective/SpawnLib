package dev.sweetberry.spawnlib.internal.mixin;

import dev.sweetberry.spawnlib.api.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import dev.sweetberry.spawnlib.internal.duck.Duck_ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class Mixin_ServerPlayer implements Duck_ServerPlayer {
    @Unique
    @Nullable
    private SpawnModification spawnlib$globalSpawn;

    @Unique
    @Nullable
    private SpawnModification spawnlib$localSpawn;

    @Override
    public SpawnModification spawnlib$getGlobalSpawn() {
        return spawnlib$globalSpawn;
    }

    @Override
    public SpawnModification spawnlib$getLocalSpawn() {
        return spawnlib$localSpawn;
    }

    @Inject(
            method = "addAdditionalSaveData",
            at = @At("TAIL")
    )
    private void spawnLib$addSpawnModifications(CompoundTag playerData, CallbackInfo ci) {
        var spawnTag = new CompoundTag();
        if (spawnlib$globalSpawn != null)
            spawnTag.put("global", SpawnModification.writeToTag(spawnlib$globalSpawn));
        if (spawnlib$localSpawn != null)
            spawnTag.put("local", SpawnModification.writeToTag(spawnlib$localSpawn));
        playerData.put(SpawnLib.SPAWN_MODIFICATION_NBT_KEY, spawnTag);
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At("TAIL")
    )
    private void spawnLib$readSpawnModifications(CompoundTag playerData, CallbackInfo ci) {
        if (!playerData.contains(SpawnLib.SPAWN_MODIFICATION_NBT_KEY))
            return;
        var spawnTag = playerData.getCompound(SpawnLib.SPAWN_MODIFICATION_NBT_KEY);
        if (spawnTag.contains("global"))
            spawnlib$globalSpawn = SpawnModification.readFromTag(spawnTag.getCompound("global"));
        if (spawnTag.contains("local"))
            spawnlib$localSpawn = SpawnModification.readFromTag(spawnTag.getCompound("local"));
    }
}
