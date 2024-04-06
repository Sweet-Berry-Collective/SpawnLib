package dev.sweetberry.spawnlib.api.modification;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;

public interface SerializableSpawnModification<D> extends SpawnModification {
    @Nullable
    Codec<D> getSerializableCodec();

    D getDefaultSerializableValue();
}
