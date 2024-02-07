package dev.sweetberry.spawnlib.neoforge;

import dev.sweetberry.spawnlib.api.SpawnLib;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(SpawnLib.MODID)
public class SpawnLibNeoForge {
    public SpawnLibNeoForge(IEventBus bus) {
        SpawnLib.init(SpawnLib.Platform.NEO_FORGE);
    }
}
