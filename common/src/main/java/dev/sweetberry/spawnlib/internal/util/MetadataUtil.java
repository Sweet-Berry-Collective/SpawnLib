package dev.sweetberry.spawnlib.internal.util;

import dev.sweetberry.spawnlib.api.ModifiedSpawn;
import dev.sweetberry.spawnlib.api.SpawnPriority;
import dev.sweetberry.spawnlib.api.metadata.Metadata;
import dev.sweetberry.spawnlib.api.metadata.provider.DynamicMetadataProvider;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetadataUtil {
    public static void createProvidersForPriority(List<MetadataProvider> providers, SpawnPriority priority, @Nullable Holder<ModifiedSpawn> spawn) {
        if (spawn == null || !spawn.isBound() || spawn.value().getMetadata().isEmpty())
            return;

        Map<String, CompoundTag> metadataTags = new HashMap<>();
        for (var metadata : spawn.value().getMetadata()) {
            String[] innerKeys = metadata.getKey().split("\\.");
            String prefix = metadata.getKey().split("\\.", 1)[0];
            metadataTags.put(prefix, createInnerKeyNbt(getPreviousNbt(metadataTags, prefix), metadata, prefix, innerKeys, metadataTags, priority));
        }
        metadataTags.values().forEach(tag -> providers.add(new DynamicMetadataProvider<>(NbtOps.INSTANCE, tag)));
    }

    private static CompoundTag getPreviousNbt(Map<String, CompoundTag> metadataTags, String prefix) {
        return metadataTags.containsKey(prefix) ? metadataTags.get(prefix) : new CompoundTag();
    }

    private static CompoundTag createInnerKeyNbt(CompoundTag oldTag, Metadata<Object> metadata, String prefix, String[] innerKeys,
                                                 Map<String, CompoundTag> metadataTags, SpawnPriority priority) {
        if (!metadataTags.containsKey(prefix))
            metadataTags.put(prefix, new CompoundTag());
        var innerTag = metadataTags.get(prefix);
        var metadataTag = metadata.getType().codec().encodeStart(NbtOps.INSTANCE, metadata.getDefaultValue()).map(tag1 -> {
            var tempTag = oldTag.copy();
            for (int i = 0; i < innerKeys.length; ++i) {
                if (i == innerKeys.length - 1) {
                    var addedTempTag = new CompoundTag();
                    addedTempTag.put("value", tag1);
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
        innerTag.put(priority.getSerializedName(), metadataTag);
        return metadataTags.get(prefix);
    }
}
