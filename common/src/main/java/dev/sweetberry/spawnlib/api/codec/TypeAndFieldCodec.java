package dev.sweetberry.spawnlib.api.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.MetadataType;
import dev.sweetberry.spawnlib.internal.registry.SpawnLibRegistries;

import java.util.HashMap;
import java.util.Map;

public class TypeAndFieldCodec implements Codec<Field<Object>> {
    private final boolean allowsConstant;
    private final boolean allowsMetadata;

    private TypeAndFieldCodec(boolean allowsConstant, boolean allowsMetadata) {
        this.allowsConstant = allowsConstant;
        this.allowsMetadata = allowsMetadata;
        if (!allowsConstant && !allowsMetadata) {
            throw new UnsupportedOperationException("TypeAndField codec must support either metadata or a constant.");
        }
    }

    public static TypeAndFieldCodec codec() {
        return new TypeAndFieldCodec(true, true);
    }

    public static TypeAndFieldCodec metadataOnlyCodec() {
        return new TypeAndFieldCodec(true, false);
    }

    public static TypeAndFieldCodec constantOnlyCodec() {
        return new TypeAndFieldCodec(false, true);
    }

    @Override
    public <T> DataResult<Pair<Field<Object>, T>> decode(DynamicOps<T> ops, T input) {
        var mapResult = ops.getMap(input);
        if (mapResult.result().isEmpty())
            return DataResult.error(() -> "Should be an object with a 'type' and 'value' field.");
        var mapLike = mapResult.result().get();
        var type = SpawnLibRegistries.METADATA_TYPE.byNameCodec().decode(ops, mapLike.get("type"));
        if (type.result().isEmpty())
            return DataResult.error(() -> "Invalid metadata type '" + mapLike.get("type") + "'.");
        var potentialMetadata = ops.getStringValue(mapLike.get("value"));
        if (
                potentialMetadata.error().isPresent()
                        || potentialMetadata.result()
                        .map(s -> !s.startsWith("$"))
                        .orElse(true)
        ) return !allowsConstant ? DataResult.error(() -> "'value' field must reference metadata.") : type.result().get().getFirst().codec().decode(ops, input).map(pair -> new Field<>(pair.getFirst())).map(tField -> Pair.of((Field<Object>) tField, input));
        String metadataName = potentialMetadata.getOrThrow().substring(1);
        return !allowsMetadata ? DataResult.error(() -> "'value' field must be constant.") : DataResult.success(Pair.of(new Field<>(metadataName, (MetadataType<Object>) type.result().get().getFirst()), input));
    }

    @Override
    public <T> DataResult<T> encode(Field<Object> input, DynamicOps<T> ops, T prefix) {
        Map<T, T> map = new HashMap<>();
        map.put(ops.createString("type"), SpawnLibRegistries.METADATA_TYPE.byNameCodec().encodeStart(ops, input.getMetadataType()).getOrThrow());
        map.put(ops.createString("value"), ops.createString("$" + input.getKey()));
        return DataResult.success(ops.createMap(map));
    }
}
