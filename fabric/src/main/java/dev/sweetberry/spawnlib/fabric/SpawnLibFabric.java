package dev.sweetberry.spawnlib.fabric;

import dev.sweetberry.spawnlib.api.ModifiedSpawn;
import dev.sweetberry.spawnlib.api.SpawnLibRegistryKeys;
import dev.sweetberry.spawnlib.api.metadata.SpawnLibMetadataTypes;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import dev.sweetberry.spawnlib.internal.SpawnLibCommands;
import dev.sweetberry.spawnlib.internal.attachment.PlayerSpawnsAttachment;
import dev.sweetberry.spawnlib.internal.attachment.WorldSpawnAttachment;
import dev.sweetberry.spawnlib.internal.registry.SpawnModificationCodecs;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;

public class SpawnLibFabric implements ModInitializer {

    public static final AttachmentType<PlayerSpawnsAttachment> PLAYER_ATTACHMENT = AttachmentRegistry.<PlayerSpawnsAttachment>builder()
            .persistent(PlayerSpawnsAttachment.CODEC)
            .initializer(PlayerSpawnsAttachment::new)
            .copyOnDeath()
            .buildAndRegister(PlayerSpawnsAttachment.ID);
    public static final AttachmentType<WorldSpawnAttachment> WORLD_ATTACHMENT = AttachmentRegistry.<WorldSpawnAttachment>builder()
            .persistent(WorldSpawnAttachment.CODEC)
            .initializer(WorldSpawnAttachment::new)
            .buildAndRegister(WorldSpawnAttachment.ID);

    private static MinecraftServer server;

    @Override
    public void onInitialize() {
        SpawnLib.init(new FabricPlatformHelper());
        ServerLifecycleEvents.SERVER_STARTED.register(server -> SpawnLibFabric.server = server);

        DynamicRegistrySetupCallback.EVENT.register(registryView ->
                registryView.registerEntryAdded(SpawnLibRegistryKeys.SPAWN, (rawId, id, object) -> SpawnLib.postInitModifiedSpawn(id, object)));
        CommandRegistrationCallback.EVENT.register(SpawnLibCommands::init);

        DynamicRegistries.register(SpawnLibRegistryKeys.SPAWN, ModifiedSpawn.DIRECT_CODEC);

        SpawnModificationCodecs.registerAll(Registry::register);
        SpawnLibMetadataTypes.registerAll(Registry::register);
    }

    public static MinecraftServer getServer() {
        return server;
    }
}
