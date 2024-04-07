package dev.sweetberry.spawnlib.neoforge;

import dev.sweetberry.spawnlib.api.ModifiedSpawn;
import dev.sweetberry.spawnlib.api.SpawnLibRegistryKeys;
import dev.sweetberry.spawnlib.api.metadata.SpawnLibMetadataTypes;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import dev.sweetberry.spawnlib.internal.attachment.ModifiedSpawnsAttachment;
import dev.sweetberry.spawnlib.internal.duck.Duck_MinecraftServer;
import dev.sweetberry.spawnlib.internal.registry.RegistrationCallback;
import dev.sweetberry.spawnlib.internal.registry.SpawnLibRegistries;
import dev.sweetberry.spawnlib.internal.registry.SpawnModificationCodecs;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.Consumer;

@Mod(SpawnLib.MODID)
public class SpawnLibNeoForge {
    public static final AttachmentType<ModifiedSpawnsAttachment> MODIFIED_SPAWNS_ATTACHMENT = AttachmentType.builder(ModifiedSpawnsAttachment::new)
            .serialize(ModifiedSpawnsAttachment.CODEC)
            .copyOnDeath()
            .build();

    public SpawnLibNeoForge(IEventBus bus) {
        SpawnLib.init(new NeoForgePlatformHelper());
    }

    @Mod.EventBusSubscriber(modid = SpawnLib.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void registerRegistries(NewRegistryEvent event) {
            event.register(SpawnLibRegistries.SPAWN_MODIFICATION_CODECS);
            event.register(SpawnLibRegistries.METADATA_TYPE);
        }

        @SubscribeEvent
        public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
            event.dataPackRegistry(SpawnLibRegistryKeys.SPAWN, ModifiedSpawn.DIRECT_CODEC);
        }

        @SubscribeEvent
        public static void registerContent(RegisterEvent event) {
            if (event.getRegistryKey() == NeoForgeRegistries.Keys.ATTACHMENT_TYPES) {
                event.register(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ModifiedSpawnsAttachment.ID, () -> MODIFIED_SPAWNS_ATTACHMENT);
            } else if (event.getRegistryKey() == SpawnLibRegistryKeys.METADATA_TYPE) {
                register(event, SpawnLibMetadataTypes::registerAll);
            } else if (event.getRegistryKey() == SpawnLibRegistryKeys.SPAWN_MODIFICATION_CODEC) {
                register(event, SpawnModificationCodecs::registerAll);
            }
        }

        private static <T> void register(RegisterEvent event, Consumer<RegistrationCallback<T>> consumer) {
            consumer.accept((registry, id, value) -> event.register(registry.key(), id, () -> value));
        }

        @SubscribeEvent
        public static void onGatherData(GatherDataEvent event) {
            for (Holder.Reference<ModifiedSpawn> entry : event.getLookupProvider().join().lookupOrThrow(SpawnLibRegistryKeys.SPAWN).listElements().toList()) {
                if (entry.isBound()) {
                    entry.value().logAndClearUnusedMetadata(entry.key().location());
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = SpawnLib.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void handleSpawnRegistryValues(ServerStartedEvent event) {
            // TODO: Testing code, remove later.
            ((Duck_MinecraftServer)event.getServer()).spawnlib$setGlobalSpawn(event.getServer().registryAccess().registry(SpawnLibRegistryKeys.SPAWN).orElseThrow().getHolderOrThrow(ResourceKey.create(SpawnLibRegistryKeys.SPAWN, SpawnLib.id("metadata"))));
        }
    }
}
