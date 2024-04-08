package dev.sweetberry.spawnlib.api.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.MetadataType;

public class SpawnLibFieldCodec<T> implements Codec<Field<T>> {

    private final MetadataType<T> metadataType;

    private SpawnLibFieldCodec(MetadataType<T> metadataType) {
        this.metadataType = metadataType;
    }

    public static <T> SpawnLibFieldCodec<T> codec(MetadataType<T> metadataType) {
        return new SpawnLibFieldCodec<>(metadataType);
    }

    @Override
    public <T1> DataResult<Pair<Field<T>, T1>> decode(DynamicOps<T1> ops, T1 input) {
        var potentialMetadata = ops.getStringValue(input);
        if (
                potentialMetadata.error().isPresent()
                || potentialMetadata.get()
                        .mapLeft(s -> !s.startsWith("$"))
                        .left()
                        .orElse(true)
        ) return metadataType.codec().decode(ops, input).map(pair -> new Field<>(pair.getFirst())).map(tField -> Pair.of(tField, input));
        // There shouldn't be an error as we we have already checked for errors above.
        String metadataName = potentialMetadata.getOrThrow(false, s -> {}).substring(1);
        return DataResult.success(Pair.of(new Field<>(metadataName, this.metadataType), input));
    }

    @Override
    public <T1> DataResult<T1> encode(Field<T> input, DynamicOps<T1> ops, T1 prefix) {
        if (input.getKey() != null)
            return DataResult.success(ops.createString("$" + input.getKey()));
        return DataResult.success(this.metadataType.codec().encode(input.getDirect(),  ops, prefix).getOrThrow(false, (s) -> {}));
    }
}
