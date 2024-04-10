package dev.sweetberry.spawnlib.internal.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.sweetberry.spawnlib.api.SpawnContext;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Mixin(PlayerList.class)
public abstract class Mixin_PlayerList {
    @Unique
    private SpawnContext spawnlib$spawn;

    @Shadow @Final
    private List<ServerPlayer> players;

    @Shadow @Final
    private MinecraftServer server;

    @Shadow @Final
    private Map<UUID, ServerPlayer> playersByUUID;

    @Shadow
    public abstract void sendLevelInfo(ServerPlayer $$0, ServerLevel $$1);

    @Shadow
    public abstract void sendPlayerPermissionLevel(ServerPlayer $$0);

    @Inject(
            method = "placeNewPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/bossevents/CustomBossEvents;onPlayerConnect(Lnet/minecraft/server/level/ServerPlayer;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void spawnlib$setSpawn(Connection $$0, ServerPlayer player, CommonListenerCookie $$2, CallbackInfo ci, @Local CompoundTag playerData) {
        if (playerData != null)
            return;
        var spawn = SpawnContext.getSpawn(player);
        if (spawn == null) return;
        var spawnPos = spawn.getSpawnPos();
        var level = spawn.getLevel();
        player.teleportTo(level, spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);
    }

    @Inject(
            method = "respawn",
            at = @At("HEAD")
    )
    private void spawnlib$calculateSpawn(ServerPlayer player, boolean stillAlive, CallbackInfoReturnable<ServerPlayer> cir) {
        spawnlib$spawn = SpawnContext.getSpawn(player);
    }

    @Inject(
            method = "respawn",
            at = @At("TAIL")
    )
    private void spawnlib$clearSpawn(ServerPlayer player, boolean stillAlive, CallbackInfoReturnable<ServerPlayer> cir) {
        spawnlib$spawn = null;
    }

    @WrapOperation(
            method = "respawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;findRespawnPositionAndUseSpawnBlock(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;FZZ)Ljava/util/Optional;"
            )
    )
    private Optional<Vec3> spawnlib$setSpawnLocation(ServerLevel $$0, BlockPos $$1, float $$2, boolean $$3, boolean $$4, Operation<Optional<Vec3>> original) {
        if (spawnlib$spawn == null)
            return original.call($$0, $$1, $$2, $$3, $$4);
        return Optional.of(spawnlib$spawn.getSpawnPos());
    }

    @WrapOperation(
            method = "respawn",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Optional;empty()Ljava/util/Optional;"
            )
    )
    private Optional<Vec3> spawnlib$setSpawnLocation(Operation<Optional<Vec3>> original) {
        if (spawnlib$spawn == null)
            return original.call();
        return Optional.of(spawnlib$spawn.getSpawnPos());
    }

    @WrapOperation(
            method = "respawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;noCollision(Lnet/minecraft/world/entity/Entity;)Z"
            )
    )
    private boolean spawnlib$setTrue(ServerLevel instance, Entity entity, Operation<Boolean> original) {
        if (spawnlib$spawn == null)
            return original.call(instance, entity);
        if (!(entity instanceof ServerPlayer player))
            return original.call(instance, entity);
        var spawnPos = spawnlib$spawn.getSpawnPos();
        var level = spawnlib$spawn.getLevel();
        player.teleportTo(level, spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);
        return true;
    }

    @WrapOperation(
            method = "respawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;getLevel(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/server/level/ServerLevel;"
            )
    )
    private ServerLevel spawnlib$getLevel(MinecraftServer instance, ResourceKey<Level> $$0, Operation<ServerLevel> original) {
        if (spawnlib$spawn == null)
            return original.call(instance, $$0);
        return spawnlib$spawn.getLevel();
    }

    @WrapOperation(
            method = "respawn",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Optional;isPresent()Z",
                    ordinal = 1
            )
    )
    private boolean spawnlib$setFalse(Optional<Vec3> instance, Operation<Boolean> original) {
        if (spawnlib$spawn == null)
            return original.call(instance);
        return false;
    }

    @WrapOperation(
            method = "respawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;teleport(DDDFF)V"
            )
    )
    private void spawnlib$setSpawn(ServerGamePacketListenerImpl instance, double $$0, double $$1, double $$2, float $$3, float $$4, Operation<Void> original) {
        if (spawnlib$spawn == null) {
            original.call(instance, $$0, $$1, $$2, $$3, $$4);
            return;
        }
        var pos = spawnlib$spawn.getSpawnPos();
        instance.teleport(pos.x, pos.y, pos.z, 0, 0);
    }
}
