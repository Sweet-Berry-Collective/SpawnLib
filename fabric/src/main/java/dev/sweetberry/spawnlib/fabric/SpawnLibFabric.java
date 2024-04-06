package dev.sweetberry.spawnlib.fabric;

import dev.sweetberry.spawnlib.api.ModifiedSpawn;
import dev.sweetberry.spawnlib.api.SpawnLibRegistryKeys;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import dev.sweetberry.spawnlib.internal.attachment.ModifiedSpawnsAttachment;
import dev.sweetberry.spawnlib.internal.registry.SpawnModificationCodecs;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;

public class SpawnLibFabric implements ModInitializer {

    public static final AttachmentType<ModifiedSpawnsAttachment> MODIFIED_SPAWNS_ATTACHMENT = AttachmentRegistry.<ModifiedSpawnsAttachment>builder()
            .persistent(ModifiedSpawnsAttachment.CODEC)
            .initializer(ModifiedSpawnsAttachment::new)
            .copyOnDeath()
            .buildAndRegister(ModifiedSpawnsAttachment.ID);

    private static MinecraftServer server;

    @Override
    public void onInitialize() {
        SpawnLib.init(new FabricPlatformHelper());
        ServerLifecycleEvents.SERVER_STARTED.register(server -> SpawnLibFabric.server = server);

        DynamicRegistries.register(SpawnLibRegistryKeys.SPAWN, ModifiedSpawn.DIRECT_CODEC);

        SpawnModificationCodecs.registerAll(Registry::register);
    }

    public static MinecraftServer getServer() {
        return server;
    }
}
