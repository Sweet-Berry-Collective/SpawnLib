package dev.sweetberry.spawnlib.api.metadata;

import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    public String getKey() {
        return metadataKey;
    }

    public MetadataType<T> getMetadataType() {
        return metadataType;
    }

    public void setMetadata(Metadata<T> metadata) {
        this.metadata = metadata;
    }

    public T get(SpawnContext context, List<MetadataProvider> providers) {
        if (metadata != null)
            for (int i = providers.size() - 1; i >= 0; --i) {
                String scope = null;
                String id = metadataKey;
                if (id.contains("$")) {
                    String[] params = id.split("\\$", 1);
                    scope = params[0];
                    id = params[1];
                }
                var value = providers.get(i).getData(context.getPriority(), scope, id, metadataType);
                if (value.isPresent())
                    return value.get();
            }
        return value;
    }

    @Nullable
    public T getDirect() {
        return value;
    }

}
