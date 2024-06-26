package dev.sweetberry.spawnlib.api;

import dev.sweetberry.spawnlib.internal.SpawnLib;
import dev.sweetberry.spawnlib.internal.mixin.Accessor_Entity;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Holds data about the spawn point of the player
 * <br>
 * Used for figuring out where they should spawn
 * <br>
 * TODO: Add fields for respawn sounds and actions for successful respawns
 * */
public class SpawnContext {
    private final ServerPlayer player;
    private SpawnPriority priority;
    private Vec3 spawnPos = Vec3.ZERO;
    private ServerLevel level;
    private boolean obstructed = false;

    public SpawnContext(ServerPlayer player) {
        this.player = player;
        reset();
    }

    public void copy(SpawnContext context) {
        priority = context.priority;
        spawnPos = context.spawnPos;
        level = context.level;
    }

    /**
     * Gets the player's spawn
     * */
    @Nullable
    public static SpawnContext getSpawn(ServerPlayer player) {
        var providers = SpawnLib.getHelper().getAttachment(player).getProviders();
        var context = new SpawnContext(player);

        Holder<ModifiedSpawn> spawn = SpawnExtensions.getLocalSpawn(player);
        if (spawn != null && spawn.isBound()) {
            context.priority = SpawnPriority.LOCAL_PLAYER;
            if (spawn.value().modify(context, providers))
                return context;
            context.obstructed = true;
            SpawnExtensions.clearLocalSpawn(player);
        }

        context.reset();
        spawn = SpawnExtensions.getGlobalSpawn(player);
        context.priority = SpawnPriority.GLOBAL_PLAYER;
        if (spawn != null && spawn.isBound() && spawn.value().modify(context, providers))
            return context;

        context.reset();
        spawn = SpawnExtensions.getGlobalSpawn(player.getServer());
        context.priority = SpawnPriority.GLOBAL_WORLD;
        if (spawn != null && spawn.isBound() && spawn.value().modify(context, providers)) {
            return context;
        }
        return null;
    }

    private void reset() {
        priority = null;
        spawnPos = Vec3.ZERO;
        level = player.getServer().overworld();
    }

    public SpawnPriority getPriority() {
        return priority;
    }

    /**
     * Whether the player local spawn was obstructed
     * @return true when the player had a local spawn set and failed to spawn there
     * */
    public boolean wasObstructed() {
        return obstructed;
    }

    /**
     * Sets the player's spawn pos for this context
     * */
    public void setSpawnPos(Vec3 pos) {
        spawnPos = pos;
    }

    /**
     * Gets the player's spawn pos for this context
     * */
    public Vec3 getSpawnPos() {
        return spawnPos;
    }

    /**
     * Gets the player
     * */
    public ServerPlayer getPlayer() {
        return player;
    }

    /**
     * Gets the server
     * */
    public MinecraftServer getServer() {
        return player.getServer();
    }

    /**
     * Gets the player's current bounding box
     * */
    public AABB getPlayerBoundingBox() {
        return player.getBoundingBox();
    }

    /**
     * Creates a bounding box following the player's dimensions at a given pos
     * */
    public AABB getPlayerBoundingBox(Vec3 pos) {
        return ((Accessor_Entity)player).getDimensions().makeBoundingBox(pos);
    }

    /**
     * Gets the current level for spawning
     * */
    public ServerLevel getLevel() {
        return level;
    }

    /**
     * Gets a level with a specific key
     * */
    public ServerLevel getLevel(ResourceKey<Level> dimension) {
        return getServer().getLevel(dimension);
    }

    /**
     * Sets the level for spawning
     * */
    public void setLevel(ServerLevel level) {
        this.level = level;
    }
}
