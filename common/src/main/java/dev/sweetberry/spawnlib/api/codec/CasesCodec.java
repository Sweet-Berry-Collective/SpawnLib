package dev.sweetberry.spawnlib.api.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import dev.sweetberry.spawnlib.api.metadata.MetadataType;
import dev.sweetberry.spawnlib.api.util.Case;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import dev.sweetberry.spawnlib.internal.registry.SpawnLibRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CasesCodec extends MapCodec<List<Case<Object, Object>>> {
    private final String key;
    private final String inputKey;
    private final String outputKey;

    private CasesCodec(String key, String inputKey, String outputKey) {
        this.key = key;
        this.inputKey = inputKey;
        this.outputKey = outputKey;
    }

    public static CasesCodec codec(String key, String inputKey, String outputKey) {
        return new CasesCodec(key, inputKey, outputKey);
    }

    @Override
    public <T> DataResult<List<Case<Object, Object>>> decode(DynamicOps<T> ops, MapLike<T> input) {
        var inputType = decodeType(inputKey, ops, input);
        if (inputType.error().isEmpty())
            return inputType.error().map(result -> DataResult.<List<Case<Object, Object>>>error(result::message)).orElse(DataResult.error(() -> "Could not decode input type from field '" + inputKey + "'."));
        var outputType = decodeType(outputKey, ops, input);
        if (outputType.error().isEmpty())
            return outputType.error().map(result -> DataResult.<List<Case<Object, Object>>>error(result::message)).orElse(DataResult.error(() -> "Could not decode output type from field '" + outputKey + "'."));
        List<Case<Object, Object>> caseList = new ArrayList<>();
        var streamResult = ops.getStream(input.get(key));
        if (streamResult.error().isPresent()) {
            return DataResult.error(() -> key + " is not present or not an array.");
        }
        var stream = streamResult.result();
        stream.orElse(Stream.empty()).forEachOrdered(t1 -> {
            var mapResult = ops.getMap(t1).resultOrPartial(s -> SpawnLib.LOGGER.error("Failed to decode case. " + s + "."));
            if (mapResult.isEmpty()) {
                return;
            }
            var when = inputType.result().get().getFirst().codec().decode(ops, mapResult.get().get("when"));
            var value = outputType.result().get().getFirst().codec().decode(ops, mapResult.get().get("value"));
            caseList.add(new Case<>(when, value));
        });
        return DataResult.success(caseList);
    }

    private static <T> DataResult<Pair<MetadataType<?>, T>> decodeType(String key, DynamicOps<T> ops, MapLike<T> mapLike) {
        var inputMap = ops.getMap(mapLike.get(key));
        if (inputMap.result().isEmpty()) {
            return DataResult.error(() -> "Field '" + key + "' is not present.");
        }
        var inputMapLike = inputMap.result().get();
        var inputType = SpawnLibRegistries.METADATA_TYPE.byNameCodec().decode(ops, inputMapLike.get("type"));
        if (inputType.result().isEmpty()) {
            return DataResult.error(() -> "Invalid metadata type '" + mapLike.get("type") + "'.");
        }
        return inputType;
    }

    @Override
    public <T> RecordBuilder<T> encode(List<Case<Object, Object>> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        return null;
    }

    @Override
    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return Stream.of(ops.createString(key));
    }

}
