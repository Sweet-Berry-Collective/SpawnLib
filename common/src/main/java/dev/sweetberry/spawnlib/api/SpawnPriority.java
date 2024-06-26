package dev.sweetberry.spawnlib.api;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * This acts as different levels for spawning
 *
 * @author Oliver-makes-code
 * */
public enum SpawnPriority implements StringRepresentable {
    /**
     * Represents the "world spawn" for all players
     * Any unset spawn for the other two priorities will default to this
     * */
    GLOBAL_WORLD,

    /**
     * Represents the "world spawn" for a specific player
     * An example of this would be Origins' Nether Spawn power
     */
    GLOBAL_PLAYER,

    /**
     * Represents a set spawn point for a player
     * An example of this would be a bed
     */
    LOCAL_PLAYER;

    @Override
    @NotNull
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
