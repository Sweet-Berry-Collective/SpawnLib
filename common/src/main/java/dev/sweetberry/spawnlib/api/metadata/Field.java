package dev.sweetberry.spawnlib.api.metadata;

public class Field<T> {
    private final String metadataKey;
    private T value;
    private MetadataType<T> metadataType;
    private Metadata<T> metadata;


    public Field(String metadataKey, MetadataType<T> metadataType) {
        this.metadataKey = metadataKey;
        this.metadataType = metadataType;
    }

    public Field(T value) {
        this.metadataKey = null;
        this.value = value;
    }

    public void setMetadata(Metadata<T> metadata) {
        this.metadata = metadata;
    }

    public String getKey() {
        return metadataKey;
    }

    public MetadataType<T> getMetadataType() {
        return metadataType;
    }

    public T get() {
        if (metadata != null) {
            return metadata.get();
        }
        return value;
    }

}
