package dev.sweetberry.spawnlib.api;

import com.mojang.serialization.Codec;
import dev.sweetberry.spawnlib.internal.codec.SerializableDataCodec;
import dev.sweetberry.spawnlib.internal.codec.ModifiedSpawnCodec;
import dev.sweetberry.spawnlib.api.metadata.Metadata;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ModifiedSpawn {
    private final Map<String, Metadata<Object>> metadata;
    private final List<SpawnModification> modifications;

    public static final Codec<ModifiedSpawn> DIRECT_CODEC = new ModifiedSpawnCodec();
    public static final Codec<Holder<ModifiedSpawn>> CODEC = RegistryFileCodec.create(SpawnLibRegistryKeys.SPAWN, DIRECT_CODEC);

    @Nullable
    List<String> unusedMetadata;

    @ApiStatus.Internal
    public ModifiedSpawn(Map<String, Metadata<Object>> metadata, List<SpawnModification> modifications, List<String> unusedMetadata) {
        this.metadata = metadata;
        this.modifications = modifications;
        this.unusedMetadata = unusedMetadata;
    }

    public ModifiedSpawn(Map<String, Metadata<Object>> metadata, List<SpawnModification> modifications) {
        this(metadata, modifications, List.of());
    }


    public Map<String, Metadata<Object>> getMetadata() {
        return this.metadata;
    }

    public <T> void setMetadataValue(String key, T value) {
        this.metadata.get(key).setValue(value);
    }

    public List<SpawnModification> getModifications() {
        return this.modifications;
    }

    public CompoundTag toTag() {
        CompoundTag compoundTag = new CompoundTag();
        this.metadata.forEach((k, v) -> compoundTag.put(k, v.getType().codec().encodeStart(NbtOps.INSTANCE, v.get()).getOrThrow(false, s -> SpawnLib.LOGGER.error("Could not serialize metadata with key '" + k + "' to NBT. " + s))));
        return compoundTag;
    }

    public void fromTag(CompoundTag tag) {

    }

    public boolean modify(SpawnContext context) {
        for (SpawnModification modification : this.modifications) {
            if (!modification.modify(context)) {
                return false;
            }
        }
        return true;
    }

    @ApiStatus.Internal
    public void logAndClearUnusedMetadata(ResourceLocation id) {
        if (unusedMetadata == null) return;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < unusedMetadata.size(); ++i) {
            stringBuilder.append(unusedMetadata.get(i));
            if (i < unusedMetadata.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        SpawnLib.LOGGER.warn("Unused metadata: [ {} ] in modified spawn data with id '{}'", stringBuilder, id);
        this.unusedMetadata = null;
    }

}
