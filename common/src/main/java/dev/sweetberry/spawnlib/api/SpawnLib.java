package dev.sweetberry.spawnlib.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpawnLib {
    public static final String MODID = "spawnlib";
    public static final Logger LOGGER = LoggerFactory.getLogger("SpawnLib");
    private static Platform platform;
    private static boolean wasInit = false;

    public static void init(Platform platform) {
        if (wasInit)
            throw new IllegalStateException("SpawnLib was already initialized!");
        wasInit = true;
        SpawnLib.platform = platform;

        LOGGER.info("SpawnLib is initializing. Platform: {}", platform.name);
    }

    public static Platform getPlatform() {
        return platform;
    }

    public enum Platform {
        FABRIC("Fabric"),
        NEO_FORGE("NeoForge");

        public final String name;

        Platform(String name) {
            this.name = name;
        }
    }
}
