package dev.sweetberry.spawnlib.neoforge;

import dev.sweetberry.spawnlib.impl.SpawnLib;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(SpawnLib.MODID)
public class SpawnLibNeoForge {
    public SpawnLibNeoForge(IEventBus bus) {
        SpawnLib.init(new NeoForgePlatformHelper());
    }
}
