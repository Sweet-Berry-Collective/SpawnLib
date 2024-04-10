package dev.sweetberry.spawnlib.internal;

import dev.sweetberry.spawnlib.api.ModifiedSpawn;
import dev.sweetberry.spawnlib.internal.registry.SpawnLibRegistries;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpawnLib {
    public static final String MODID = "spawnlib";
    public static final Logger LOGGER = LoggerFactory.getLogger("SpawnLib");
    private static boolean wasInit = false;
    private static PlatformHelper helper;

    public static void init(PlatformHelper helper) {
        if (wasInit)
            throw new IllegalStateException("SpawnLib was already initialized!");
        wasInit = true;
        SpawnLib.helper = helper;

        LOGGER.info("SpawnLib is initializing. Platform: {}", helper.getPlatform().name);

        SpawnLibRegistries.init();

    }

    public static PlatformHelper getHelper() {
        return helper;
    }

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MODID, name);
    }

    public static void postInitModifiedSpawn(ResourceLocation id, ModifiedSpawn object) {
        object.logAndClearUnusedMetadata(id);
    }
}
