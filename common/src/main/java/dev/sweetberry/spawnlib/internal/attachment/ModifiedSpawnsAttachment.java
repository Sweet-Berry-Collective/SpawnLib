package dev.sweetberry.spawnlib.internal.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.ModifiedSpawn;
import dev.sweetberry.spawnlib.api.SpawnPriority;
import dev.sweetberry.spawnlib.api.metadata.Metadata;
import dev.sweetberry.spawnlib.api.metadata.provider.DynamicMetadataProvider;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import dev.sweetberry.spawnlib.internal.codec.MetadataProviderCodec;
import dev.sweetberry.spawnlib.internal.codec.RegistryOpsCodec;
import dev.sweetberry.spawnlib.internal.registry.SpawnLibRegistries;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ModifiedSpawnsAttachment {
    public static final ResourceLocation ID = SpawnLib.id("modified_spawns");

    public static final Codec<ModifiedSpawnsAttachment> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RegistryOpsCodec.codec(ModifiedSpawn.CODEC).optionalFieldOf("global").forGetter(attachment -> attachment.globalSpawn),
            RegistryOpsCodec.codec(ModifiedSpawn.CODEC).optionalFieldOf("local").forGetter(attachment -> attachment.localSpawn),
            MetadataProviderCodec.PLAYER_INSTANCE.optionalFieldOf("metadata", new ArrayList<>()).forGetter(attachment -> attachment.providers)
    ).apply(inst, ModifiedSpawnsAttachment::new));

    private Optional<Holder<ModifiedSpawn>> globalSpawn;
    private Optional<Holder<ModifiedSpawn>> localSpawn;
    private final List<MetadataProvider> providers;

    public ModifiedSpawnsAttachment(Optional<Holder<ModifiedSpawn>> globalSpawn, Optional<Holder<ModifiedSpawn>> localSpawn, List<MetadataProvider> providers) {
        this.globalSpawn = globalSpawn;
        this.localSpawn = localSpawn;
        this.providers = getOrCreateProviders(providers);
    }

    public ModifiedSpawnsAttachment() {
        this(Optional.empty(), Optional.empty(), new ArrayList<>());
    }

    private List<MetadataProvider> getOrCreateProviders(List<MetadataProvider> providers) {
        if (providers.isEmpty()) {
            globalSpawn.ifPresent(spawnHolder -> createProvidersForPriority(providers, SpawnPriority.LOCAL_PLAYER, spawnHolder));
            localSpawn.ifPresent(spawnHolder -> createProvidersForPriority(providers, SpawnPriority.LOCAL_PLAYER, spawnHolder));
        }
        validateMetadata(providers);
        return providers;
    }


    // TODO: This.
    private List<MetadataProvider> validateMetadata(List<MetadataProvider> providers) {
        return providers;
    }

    public void createMetadataProviders(Holder<ModifiedSpawn> spawn, SpawnPriority priority, Tag tag) {
        List<MetadataProvider> providers = new MetadataProviderCodec(priority).decode(NbtOps.INSTANCE, tag).getOrThrow(false, s -> {}).getFirst();
        createProvidersForPriority(providers, priority, spawn);
    }

    private void createProvidersForPriority(List<MetadataProvider> providers, SpawnPriority priority, @Nullable Holder<ModifiedSpawn> spawn) {
        if (spawn == null || !spawn.isBound() || spawn.value().getMetadata().isEmpty())
            return;

        Map<String, CompoundTag> metadataTags = new HashMap<>();
        for (var metadata : spawn.value().getMetadata()) {
            String[] innerKeys = metadata.getKey().split("\\$");
            String prefix = metadata.getKey().split("\\$", 1)[0];
            metadataTags.put(prefix, createInnerKeyNbt(getPreviousNbt(metadataTags, prefix), metadata, prefix, innerKeys, metadataTags, priority, spawn));
        }
        metadataTags.values().forEach(tag -> providers.add(new DynamicMetadataProvider<>(NbtOps.INSTANCE, tag)));
    }

    private static CompoundTag getPreviousNbt(Map<String, CompoundTag> metadataTags, String prefix) {
        return metadataTags.containsKey(prefix) ? metadataTags.get(prefix) : new CompoundTag();
    }

    private static CompoundTag createInnerKeyNbt(CompoundTag oldTag, Metadata<Object> metadata, String prefix, String[] innerKeys,
                                          Map<String, CompoundTag> metadataTags,
                                          SpawnPriority priority, Holder<ModifiedSpawn> spawn) {
        if (!metadataTags.containsKey(prefix)) {
            metadataTags.put(prefix, new CompoundTag());
            var innerTag = metadataTags.get(prefix);
            innerTag.put(priority.getSerializedName(), new CompoundTag());
            var priorityTag = innerTag.getCompound(priority.getSerializedName());
            priorityTag.putString("spawn", spawn.unwrapKey().get().location().toString());
        }
        var innerTag = metadataTags.get(prefix);
        var priorityTag = innerTag.getCompound(priority.getSerializedName());
        var metadataTag = metadata.getType().codec().encodeStart(NbtOps.INSTANCE, metadata.getDefaultValue()).map(tag1 -> {
            var tempTag = oldTag.copy();
            for (int i = 0; i < innerKeys.length; ++i) {
                if (i == innerKeys.length - 1) {
                    var addedTempTag = new CompoundTag();
                    addedTempTag.put("value", tag1);
                    addedTempTag.putString("type", SpawnLibRegistries.METADATA_TYPE.getKey(metadata.getType()).toString());
                    tempTag.put(innerKeys[i], addedTempTag);
                } else if (tempTag.contains(innerKeys[i])) {
                    tempTag = tempTag.copy();
                } else {
                    tempTag.put(innerKeys[i], new CompoundTag());
                    tempTag = tempTag.copy();
                }
            }
            return tempTag;
        }).getOrThrow(false, s -> SpawnLib.LOGGER.warn("Failed to encode value '{}' to attachment. {}", metadata.getKey(), s));
        priorityTag.put("metadata", metadataTag);
        return metadataTags.get(prefix);
    }

    @Nullable
    public ModifiedSpawn getGlobalSpawn() {
        if (globalSpawn.isEmpty() || !globalSpawn.get().isBound())
            return null;
        return globalSpawn.get().value();
    }

    public void setGlobalSpawn(Holder<ModifiedSpawn> spawn) {
        this.globalSpawn = Optional.of(spawn);
    }

    public boolean clearGlobalSpawn() {
        if (this.globalSpawn.isEmpty())
            return false;
        this.globalSpawn = Optional.empty();
        validateMetadata(this.providers);
        return true;
    }

    @Nullable
    public ModifiedSpawn getLocalSpawn() {
        if (localSpawn.isEmpty() || !localSpawn.get().isBound())
            return null;
        return localSpawn.get().value();
    }

    public void setLocalSpawn(Holder<ModifiedSpawn> spawn) {
        this.localSpawn = Optional.of(spawn);
    }

    public boolean clearLocalSpawn() {
        if (this.localSpawn.isEmpty())
            return false;
        this.localSpawn = Optional.empty();
        validateMetadata(this.providers);
        return true;
    }

    public List<MetadataProvider> getProviders() {
        return providers;
    }

}
