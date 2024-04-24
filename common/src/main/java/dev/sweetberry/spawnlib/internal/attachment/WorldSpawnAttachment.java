package dev.sweetberry.spawnlib.internal.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.ModifiedSpawn;
import dev.sweetberry.spawnlib.api.SpawnLibRegistryKeys;
import dev.sweetberry.spawnlib.api.SpawnPriority;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import dev.sweetberry.spawnlib.internal.codec.MetadataProviderCodec;
import dev.sweetberry.spawnlib.internal.codec.RegistryOpsCodec;
import dev.sweetberry.spawnlib.internal.util.MetadataUtil;
import net.minecraft.core.Holder;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WorldSpawnAttachment {
    public static final ResourceLocation ID = SpawnLib.id("modified_spawns");

    public static final Codec<WorldSpawnAttachment> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RegistryOpsCodec.codec(ModifiedSpawn.CODEC).optionalFieldOf("spawn").forGetter(attachment -> {
                if (attachment.spawn.unwrapKey().get() == SpawnLibRegistryKeys.DEFAULT_SPAWN)
                    return Optional.empty();
                return Optional.ofNullable(attachment.spawn);
            }),
            MetadataProviderCodec.SERVER_INSTANCE.optionalFieldOf("metadata", new ArrayList<>()).forGetter(attachment -> attachment.providers)
    ).apply(inst, WorldSpawnAttachment::new));

    private Holder<ModifiedSpawn> spawn;
    private final List<MetadataProvider> providers;

    public WorldSpawnAttachment(Optional<Holder<ModifiedSpawn>> spawn, List<MetadataProvider> providers) {
        // TODO: Fix server being null when loading world.
        this.spawn = spawn.orElseGet(() -> SpawnLib.getHelper().getServer().registryAccess().registry(SpawnLibRegistryKeys.SPAWN).orElseThrow().getHolderOrThrow(SpawnLibRegistryKeys.DEFAULT_SPAWN));
        this.providers = getOrCreateProviders(providers);
    }

    public WorldSpawnAttachment() {
        this(Optional.empty(), new ArrayList<>());
    }

    private List<MetadataProvider> getOrCreateProviders(List<MetadataProvider> providers) {
        if (providers.isEmpty()) {
            MetadataUtil.createProvidersForPriority(providers, SpawnPriority.GLOBAL_WORLD, spawn);
        }
        validateMetadata(providers);
        return providers;
    }

    private void createMetadataProviders(Holder<ModifiedSpawn> spawn, Tag tag) {
        List<MetadataProvider> providers = new MetadataProviderCodec(SpawnPriority.GLOBAL_WORLD).decode(NbtOps.INSTANCE, tag).getOrThrow().getFirst();
        MetadataUtil.createProvidersForPriority(providers, SpawnPriority.GLOBAL_WORLD, spawn);
    }

    // TODO: This.
    private List<MetadataProvider> validateMetadata(List<MetadataProvider> providers) {
        return providers;
    }

    public Holder<ModifiedSpawn> getSpawn() {
        return spawn;
    }

    public void setSpawn(Holder<ModifiedSpawn> spawn, Tag tag) {
        this.spawn = spawn;
        createMetadataProviders(this.spawn, tag);
    }
}
