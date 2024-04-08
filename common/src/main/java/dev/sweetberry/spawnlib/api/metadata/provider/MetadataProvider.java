package dev.sweetberry.spawnlib.api.metadata.provider;

import dev.sweetberry.spawnlib.api.SpawnPriority;
import dev.sweetberry.spawnlib.api.metadata.MetadataType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface MetadataProvider {
    <T> Optional<T> getData(SpawnPriority priority, @Nullable String scope, String id, MetadataType<T> metadataType);
}
