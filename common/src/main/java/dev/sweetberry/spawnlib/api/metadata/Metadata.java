package dev.sweetberry.spawnlib.api.metadata;

import com.mojang.serialization.Codec;
import dev.sweetberry.spawnlib.internal.registry.SpawnLibRegistries;
import org.jetbrains.annotations.ApiStatus;

public class Metadata<T> {
    private String key;
    private final MetadataType<T> type;
    private final T defaultValue;

    public static final Codec<Metadata<?>> CODEC = SpawnLibRegistries.METADATA_TYPE.byNameCodec().dispatch(Metadata::getType, MetadataType::getInnerCodec);

    public Metadata(MetadataType<T> type, T defaultValue) {
        this.type = type;
        this.defaultValue = defaultValue;
    }

    /**
     * Sets the key of this metadata, primarily used to set the name
     * to the key of the object when loading.
     * @param key The new name.
     */
    @ApiStatus.Internal
    public void setKey(String key) {
        if (this.key != null)
            return;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public MetadataType<T> getType() {
        return type;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

}
