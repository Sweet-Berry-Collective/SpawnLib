package dev.sweetberry.spawnlib.fabric;

import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

public class SpawnLibFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        SpawnLib.init(new FabricPlatformHelper());
    }
}
