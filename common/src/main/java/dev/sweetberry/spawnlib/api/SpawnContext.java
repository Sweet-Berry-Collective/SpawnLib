package dev.sweetberry.spawnlib.api;

import dev.sweetberry.spawnlib.internal.mixin.Accessor_Entity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Holds data about the spawn point of the player
 * <br>
 * Used for figuring out where they should spawn
 * <br>
 * TODO: Add fields for respawn sounds and actions for successful respawns
 * */
public class SpawnContext {
    private final ServerPlayer player;
    private Vec3 spawnPos = Vec3.ZERO;
    private ServerLevel level;
    private boolean obstructed = false;

    public SpawnContext(ServerPlayer player) {
        this.player = player;
        reset();
    }

    /**
     * Gets the player's spawn
     * */
    @NotNull
    public static SpawnContext getSpawn(ServerPlayer player) {
        var context = new SpawnContext(player);

        ModifiedSpawn spawn = SpawnExtensions.getLocalSpawn(player);
        if (spawn != null) {
            if (spawn.modify(context))
                return context;
            context.obstructed = true;
            // TODO: set player local spawn to null
        }

        context.reset();
        spawn = SpawnExtensions.getGlobalSpawn(player);
        if (spawn != null && spawn.modify(context))
            return context;

        context.reset();
        spawn = SpawnExtensions.getGlobalSpawn(player.getServer());
        spawn.modify(context);
        return context;
    }

    private void reset() {
        spawnPos = Vec3.ZERO;
        level = player.getServer().overworld();
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
