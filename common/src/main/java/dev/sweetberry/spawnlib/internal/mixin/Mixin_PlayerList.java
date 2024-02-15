package dev.sweetberry.spawnlib.internal.mixin;

import com.mojang.authlib.GameProfile;
import dev.sweetberry.spawnlib.api.SpawnContext;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.LevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(PlayerList.class)
public abstract class Mixin_PlayerList {
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

    // Why does mixin require you to include captures you don't need??? That's stupid.
    @Inject(
            method = "placeNewPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;<init>(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/network/Connection;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/network/CommonListenerCookie;)V",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void spawnlib$setSpawn(Connection $$0, ServerPlayer player, CommonListenerCookie $$2, CallbackInfo ci, GameProfile $$3, GameProfileCache $$4, String $$7, CompoundTag playerData, ResourceKey $$9, ServerLevel $$10, ServerLevel $$12, String $$13, LevelData $$14) {
        if (playerData != null)
            return;
        var spawn = SpawnContext.getSpawn(player);
        var spawnPos = spawn.getSpawnPos();
        var level = spawn.getLevel();
        player.teleportTo(level, spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);
    }

    /**
     * @author Oliver-makes-code
     * @reason We're replacing this with our own resolver
     */
    @Overwrite
    public ServerPlayer respawn(ServerPlayer playerToRespawn, boolean stillAlive) {
        var spawn = SpawnContext.getSpawn(playerToRespawn);
        var spawnPos = spawn.getSpawnPos();
        var level = spawn.getLevel();
        var levelData = level.getLevelData();

        // Remove old player
        players.remove(playerToRespawn);
        playerToRespawn.serverLevel().removePlayerImmediately(playerToRespawn, Entity.RemovalReason.DISCARDED);

        // Create new player
        var player = new ServerPlayer(server, spawn.getLevel(), playerToRespawn.getGameProfile(), playerToRespawn.clientInformation());
        var connection = playerToRespawn.connection;
        player.connection = connection;
        player.restoreFrom(playerToRespawn, stillAlive);
        player.setId(playerToRespawn.getId());
        player.setMainArm(playerToRespawn.getMainArm());
        for (var tag : playerToRespawn.getTags())
            player.addTag(tag);

        // Notify of missing block
        if (spawn.wasObstructed())
            connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0F));

        // Send updates
        connection.send(new ClientboundRespawnPacket(player.createCommonSpawnInfo(spawn.getLevel()), (byte) (stillAlive ? 1 : 0)));
        player.teleportTo(level, spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);
        connection.send(new ClientboundSetDefaultSpawnPositionPacket(BlockPos.containing(spawnPos.x, spawnPos.y, spawnPos.z), 0));
        connection.send(new ClientboundChangeDifficultyPacket(levelData.getDifficulty(), levelData.isDifficultyLocked()));
        connection.send(new ClientboundSetExperiencePacket(player.experienceProgress, player.totalExperience, player.experienceLevel));
        sendLevelInfo(player, level);
        sendPlayerPermissionLevel(player);
        level.addRespawnedPlayer(player);
        players.add(player);
        playersByUUID.put(player.getUUID(), player);
        player.initInventoryMenu();
        player.setHealth(player.getHealth());

        return player;
    }
}
