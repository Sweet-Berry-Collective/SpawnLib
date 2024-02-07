package dev.sweetberry.spawnlib.fabric;

import dev.sweetberry.spawnlib.api.SpawnLib;
import net.fabricmc.api.ModInitializer;

public class SpawnLibFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        SpawnLib.init(SpawnLib.Platform.FABRIC);
    }
}
