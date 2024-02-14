package dev.sweetberry.spawnlib.api;

import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Holds data about the spawn point of the player
 * Used for figuring out where they should spawn
 * */
public class SpawnContext {
    private Vec3 spawnPos = Vec3.ZERO;
    private Player player;

    public SpawnContext(Player player) {
        this.player = player;
    }

    public static Vec3 getSpawnPos(Player player) {
        var context = new SpawnContext(player);
        var spawn = SpawnLib.getHelper().getLocalSpawn(player);
        if (spawn != null && spawn.modify(context))
            return context.spawnPos;
        spawn = SpawnLib.getHelper().getGlobalSpawn(player);
        if (spawn != null && spawn.modify(context))
            return context.spawnPos;
        spawn = SpawnLib.getHelper().getGlobalSpawn(player.level());
        spawn.modify(context);
        return context.spawnPos;
    }

    public void setSpawnPos(Vec3 pos) {
        spawnPos = pos;
    }

    public Player getPlayer() {
        return player;
    }

    public AABB getPlayerBoundingBox() {
        return player.getBoundingBox();
    }
}
