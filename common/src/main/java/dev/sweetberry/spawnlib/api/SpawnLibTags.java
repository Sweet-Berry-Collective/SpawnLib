package dev.sweetberry.spawnlib.api;

import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class SpawnLibTags {
    public static final TagKey<Fluid> SPAWN_BLOCKING = TagKey.create(Registries.FLUID, SpawnLib.id("spawn_blocking"));
}
