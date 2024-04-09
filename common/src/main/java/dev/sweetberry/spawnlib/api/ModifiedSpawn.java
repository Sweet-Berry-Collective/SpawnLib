package dev.sweetberry.spawnlib.api;

import com.mojang.serialization.Codec;
import dev.sweetberry.spawnlib.api.metadata.Metadata;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import dev.sweetberry.spawnlib.internal.codec.ModifiedSpawnCodec;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModifiedSpawn {
    private final List<Metadata<Object>> metadata;
    private final List<SpawnModification> modifications;

    public static final Codec<ModifiedSpawn> DIRECT_CODEC = new ModifiedSpawnCodec();
    public static final Codec<Holder<ModifiedSpawn>> CODEC = RegistryFileCodec.create(SpawnLibRegistryKeys.SPAWN, DIRECT_CODEC, false);

    @Nullable
    List<String> unusedMetadata;

    @ApiStatus.Internal
    public ModifiedSpawn(List<Metadata<Object>> metadata, List<SpawnModification> modifications, List<String> unusedMetadata) {
        this.metadata = metadata;
        this.modifications = modifications;
        this.unusedMetadata = unusedMetadata;
    }

    public ModifiedSpawn(List<Metadata<Object>> metadata, List<SpawnModification> modifications) {
        this(metadata, modifications, List.of());
    }


    public List<Metadata<Object>> getMetadata() {
        return this.metadata;
    }

    public List<SpawnModification> getModifications() {
        return this.modifications;
    }

    public boolean modify(SpawnContext context, List<MetadataProvider> metadataProviders) {
        for (SpawnModification modification : this.modifications)
            if (!modification.modify(context, metadataProviders))
                return false;
        return true;
    }

    @ApiStatus.Internal
    public void logAndClearUnusedMetadata(ResourceLocation id) {
        if (unusedMetadata == null) return;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < unusedMetadata.size(); ++i) {
            stringBuilder.append(unusedMetadata.get(i));
            if (i < unusedMetadata.size() - 1)
                stringBuilder.append(", ");
        }
        SpawnLib.LOGGER.warn("Unused metadata: [ {} ] in modified spawn data with id '{}'", stringBuilder, id);
        this.unusedMetadata = null;
    }

}
