package dev.sweetberry.spawnlib.api.metadata;

import com.mojang.serialization.Codec;
import dev.sweetberry.spawnlib.internal.codec.SerializableDataCodec;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.function.Supplier;

public class Metadata<T> implements Supplier<T> {
    private String key;
    private final MetadataType<T> type;
    private final T defaultValue;
    private T value;

    public static final Codec<Metadata<?>> CODEC = SpawnLibMetadataTypes.METADATA_TYPE.byNameCodec().dispatch(Metadata::getType, MetadataType::getInnerCodec);

    public Metadata(String key, MetadataType<T> type, T defaultValue) {
        this.key = key;
        this.type = type;
        this.defaultValue = defaultValue;
    }

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
        this.key = key;
    }

    public void setValue(T value) {
        this.value = value;
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

    public T get() {
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

}
