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
import dev.sweetberry.spawnlib.internal.registry.SpawnLibRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ModifiedSpawnCodec implements Codec<ModifiedSpawn> {
    @Override
    public <T> DataResult<Pair<ModifiedSpawn, T>> decode(DynamicOps<T> ops, T input) {
        List<Metadata<Object>> metadata = new ArrayList<>();
        List<Metadata<Object>> unused = new ArrayList<>();

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
                String key = ops.getStringValue(values.get(i).getFirst()).getOrThrow(false, s -> {});
                if (key.contains(".")) {
                    return DataResult.error(() -> "Metadata is not allowed to utilise '.' as it is reserved for built-in metadata.");
                }
                individualMetadata.setKey(key);
                unused.add((Metadata<Object>)individualMetadata);
            }
        }

        DataResult<T> functionsInput = ops.get(input, "functions");
        if (functionsInput.error().isPresent())
            return DataResult.error(() -> "Could not decode 'functions' field." + functionsInput.error().get().message());
        DataResult<Pair<List<SpawnModification>, T>> modificationResult = SpawnModification.CODEC.listOf().decode(ops, functionsInput.result().orElseThrow());
        Optional<Pair<List<SpawnModification>, T>> partialResult = modificationResult.resultOrPartial(s -> SpawnLib.LOGGER.error("Could not decode spawn condition. " + s));
        if (partialResult.isEmpty() || partialResult.get().getFirst().isEmpty())
            return DataResult.error(() -> "Could not decode spawn, no modifications were specified.");
        List<SpawnModification> modifications = getSpawnModifications(partialResult.get().getFirst(), metadata, unused);

        return DataResult.success(Pair.of(new ModifiedSpawn(metadata, modifications, unused.isEmpty() ? null : unused.stream().map(Metadata::getKey).toList()), input));
    }

    @NotNull
    private static List<SpawnModification> getSpawnModifications(List<SpawnModification> partialResult, List<Metadata<Object>> metadata, List<Metadata<Object>> unused) {
        handleInnerBuiltInMetadata(partialResult, metadata, "");
        partialResult.forEach(modification -> handleInnerFieldMetadata(modification, metadata, unused));
        return partialResult;
    }

    private static void handleInnerBuiltInMetadata(List<SpawnModification> partialResult, List<Metadata<Object>> metadata, String prefix) {
        Map<String, Integer> indexMap = new HashMap<>();
        for (SpawnModification modification : partialResult) {
            String id = prefix + modification.getId() + (indexMap.containsKey(prefix) ? "$" + indexMap.get(prefix) : "");
            if (!modification.getBuiltInMetadata().isEmpty()) {
                modification.getBuiltInMetadata().forEach(md -> {
                    if (md.getKey() == null)
                        throw new NullPointerException("Built-in metadata for " + modification.getId() + " must have a specified key.");
                    md.setKey(id + "." + md.getKey());
                    metadata.add((Metadata<Object>) md);
                    indexMap.put(prefix, indexMap.getOrDefault(prefix, 0) + 1);
                });
            }
            handleInnerBuiltInMetadata(modification.getInnerModifications(), metadata, id);
        }
    }

    private static void handleInnerFieldMetadata(SpawnModification modification, List<Metadata<Object>> metadata, List<Metadata<Object>> unused) {
        modification.getFields().forEach(field -> {
            if (field.getKey() == null)
                return;
            Optional<Metadata<Object>> md = unused.stream().filter(metadata1 -> metadata1.getKey().equals(field.getKey())).findAny();

            if (md.isEmpty() || !unused.contains(md.get())) {
                SpawnLib.LOGGER.error("Could not find metadata field '{}' in spawn.", field.getKey());
                return;
            }
            if (metadata.contains(md.get())) {
                SpawnLib.LOGGER.error("Could not add metadata '{}' to spawn because it is already defined.", field.getKey());
                return;
            }

            if (md.get().getType().isOfType(field)) {
                metadata.add(md.get());
                unused.remove(md.get());
                ((Field<Object>) field).setMetadata(md.get());
            } else
                SpawnLib.LOGGER.error("Could not add metadata to spawn because it is of the wrong type. Expected '{}' but got '{}'.", SpawnLibRegistries.METADATA_TYPE.getKey(md.get().getType()), SpawnLibRegistries.METADATA_TYPE.getKey(field.getMetadataType()));
        });
    }

    @Override
    public <T> DataResult<T> encode(ModifiedSpawn input, DynamicOps<T> ops, T prefix) {
        Map<T, T> finalMap = new HashMap<>();

        Map<T, T> metadataMap = new HashMap<>();
        for (Metadata<Object> entry : input.getMetadata()) {
            T key = ops.createString(entry.getKey());
            T value = Metadata.CODEC.encodeStart(ops, entry).getOrThrow(false, s -> {});

            metadataMap.put(key, value);
        }

        finalMap.put(ops.createString("metadata"), ops.createMap(metadataMap));
        finalMap.put(ops.createString("functions"), SpawnModification.CODEC.listOf().encodeStart(ops, input.getModifications()).getOrThrow(false, (s) -> {}));

        return DataResult.success(ops.createMap(finalMap));
    }
}
