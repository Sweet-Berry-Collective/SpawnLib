package dev.sweetberry.spawnlib.internal.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.sweetberry.spawnlib.api.ModifiedSpawn;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.Metadata;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ModifiedSpawnCodec implements Codec<ModifiedSpawn> {
    @Override
    public <T> DataResult<Pair<ModifiedSpawn, T>> decode(DynamicOps<T> ops, T input) {
        Map<String, Metadata<Object>> metadata = new HashMap<>();
        Map<String, Metadata<Object>> unused = new HashMap<>();

        DataResult<T> metadataInput = ops.get(input, "metadata");

        if (metadataInput.error().isEmpty()) {
            List<Pair<T, T>> values = ops.getMapValues(metadataInput.getOrThrow(false, (s) -> {})).getOrThrow(false, (s) -> {}).toList();
            for (int i = 0; i < values.size(); ++i) {
                DataResult<Pair<Metadata<?>, T>> metadataResult = Metadata.CODEC.decode(ops, values.get(i).getSecond());
                if (metadataResult.error().isPresent()) {
                    int finalI = i;
                    return DataResult.error(() -> "Could not decode metadata at index [" + finalI + "]. " + metadataResult.error().get().message());
                }
                Metadata<?> individualMetadata = metadataResult.result().get().getFirst();
                individualMetadata.setKey(ops.getStringValue(values.get(i).getFirst()).getOrThrow(false, s -> {}));
                unused.put(individualMetadata.getKey(), (Metadata<Object>)individualMetadata);
            }
        }

        DataResult<T> functionsInput = ops.get(input, "functions");
        if (functionsInput.error().isPresent())
            return DataResult.error(() -> "Could not decode 'functions' field." + functionsInput.error().get().message());
        DataResult<Pair<List<SpawnModification>, T>> modificationResult = SpawnModification.CODEC.listOf().decode(ops, functionsInput.result().orElseThrow());
        Optional<Pair<List<SpawnModification>, T>> partialResult = modificationResult.resultOrPartial(s -> SpawnLib.LOGGER.error("Could not decode spawn function. " + s));
        if (partialResult.isEmpty() || partialResult.get().getFirst().isEmpty())
            return DataResult.error(() -> "Could not decode spawn, no modifications were specified.");
        List<SpawnModification> modifications = getSpawnModifications(partialResult, unused, metadata);

        return DataResult.success(Pair.of(new ModifiedSpawn(metadata, modifications, unused.isEmpty() ? null : unused.keySet().stream().toList()), input));
    }

    @NotNull
    private static <T> List<SpawnModification> getSpawnModifications(Optional<Pair<List<SpawnModification>, T>> partialResult, Map<String, Metadata<Object>> unused, Map<String, Metadata<Object>> metadata) {
        List<SpawnModification> modifications = partialResult.get().getFirst();
        modifications.forEach(modification -> modification.getFields().forEach(field -> {
            if (field.getKey() == null)
                return;
            Metadata<Object> md = unused.getOrDefault(field.getKey(), null);
            if (md != null && md.getType().isOfType(field)) {
                metadata.put(md.getKey(), md);
                unused.remove(md.getKey());
                ((Field<Object>)field).setMetadata(md);
            }
        }));
        return modifications;
    }

    @Override
    public <T> DataResult<T> encode(ModifiedSpawn input, DynamicOps<T> ops, T prefix) {
        Map<T, T> finalMap = new HashMap<>();

        Map<T, T> metadataMap = new HashMap<>();
        for (Map.Entry<String, Metadata<Object>> entry : input.getMetadata().entrySet()) {
            T key = ops.createString(entry.getKey());
            T value = Metadata.CODEC.encodeStart(ops, entry.getValue()).getOrThrow(false, s -> {});

            metadataMap.put(key, value);
        }

        finalMap.put(ops.createString("metadata"), ops.createMap(metadataMap));
        finalMap.put(ops.createString("functions"), SpawnModification.CODEC.listOf().encodeStart(ops, input.getModifications()).getOrThrow(false, (s) -> {}));

        return DataResult.success(ops.createMap(finalMap));
    }
}
