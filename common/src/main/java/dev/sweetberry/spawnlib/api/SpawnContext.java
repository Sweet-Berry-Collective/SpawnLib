package dev.sweetberry.spawnlib.api;

import dev.sweetberry.spawnlib.internal.SpawnLib;
import dev.sweetberry.spawnlib.internal.mixin.Accessor_Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Holds data about the spawn point of the player
 * Used for figuring out where they should spawn
 * */
public class SpawnContext {
    private final ServerPlayer player;
    private Vec3 spawnPos = Vec3.ZERO;
    private ServerLevel level;

    public SpawnContext(ServerPlayer player) {
        this.player = player;
        reset();
    }

    /**
     * Gets the player's spawn
     * <br>
     * TODO: Keep track of which were present and which failed, so we can provide chat feedback
     * */
    public static SpawnContext getSpawn(ServerPlayer player) {
        var helper = SpawnLib.getHelper();
        var context = new SpawnContext(player);

        var spawn = helper.getLocalSpawn(player);
        if (spawn != null && spawn.modify(context))
            return context;

        context.reset();
        spawn = helper.getGlobalSpawn(player);
        if (spawn != null && spawn.modify(context))
            return context;

        context.reset();
        spawn = helper.getGlobalSpawn(player.getServer());
        spawn.modify(context);
        return context;
    }

    private void reset() {
        spawnPos = Vec3.ZERO;
        level = player.getServer().overworld();
    }

    /**
     * Sets the player's spawn pos for this context
     * */
    public void setSpawnPos(Vec3 pos) {
        spawnPos = pos;
    }

    /**
     * Gets the player
     * */
    public ServerPlayer getPlayer() {
        return player;
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
     * Sets the level for spawning
     * */
    public void setLevel(ServerLevel level) {
        this.level = level;
    }
}
