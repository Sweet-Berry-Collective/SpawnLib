package dev.sweetberry.spawnlib.internal.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import dev.sweetberry.spawnlib.api.SpawnPriority;
import dev.sweetberry.spawnlib.api.metadata.provider.DynamicMetadataProvider;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.internal.SpawnLib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MetadataProviderCodec implements Codec<List<MetadataProvider>> {
    public static final MetadataProviderCodec SERVER_INSTANCE = new MetadataProviderCodec(SpawnPriority.GLOBAL_WORLD);
    public static final MetadataProviderCodec PLAYER_INSTANCE = new MetadataProviderCodec(new SpawnPriority[]{SpawnPriority.GLOBAL_PLAYER, SpawnPriority.LOCAL_PLAYER});

    private final SpawnPriority[] priorities;

    public MetadataProviderCodec(SpawnPriority priority) {
        this(new SpawnPriority[]{priority});
    }


    public MetadataProviderCodec(SpawnPriority[] priorities) {
        this.priorities = priorities;
    }

    @Override
    public <T> DataResult<Pair<List<MetadataProvider>, T>> decode(DynamicOps<T> ops, T input) {
        List<MetadataProvider> metadata = new ArrayList<>();
        for (SpawnPriority priority : priorities) {
            var mapLikeResult = ops.getMap(ops.getMap(input).getOrThrow(false, SpawnLib.LOGGER::error).get(priority.getSerializedName()));
            if (mapLikeResult.result().isEmpty())
                // We don't want to error when a value isn't present.
                continue;
            var mapLike = mapLikeResult.result().get();
            Set<T> metadataSet = new HashSet<>();
            handleMetadata(ops, mapLike.get("metadata"), mapLike, metadataSet, priority);
            metadataSet.forEach(t -> metadata.add(new DynamicMetadataProvider<>(ops, t)));
        }
        return DataResult.success(Pair.of(metadata, input));
    }

    private static <T> void handleMetadata(DynamicOps<T> ops, T mapValue,
                                           MapLike<T> baseMapLike, Set<T> metadataSet,
                                           SpawnPriority priority) {
        if (baseMapLike.get("spawn") == null)
            return;

        MapLike<T> mapLikeValue = ops.getMap(mapValue).getOrThrow(false, SpawnLib.LOGGER::error);
        T value = handleInnerMetadata(ops, mapValue, mapLikeValue);

        Map<T, T> newMap = new HashMap<>();
        newMap.put(ops.createString("metadata"), value);
        newMap.put(ops.createString("spawn"), baseMapLike.get("spawn"));
        metadataSet.add(ops.createMap(Map.of(ops.createString(priority.getSerializedName()), ops.createMap(newMap))));
    }

    private static <T> T handleInnerMetadata(DynamicOps<T> ops, T existingMap, MapLike<T> mapLike) {
        for (Pair<T, T> entries : mapLike.entries().toList()) {
            T stringEntry = entries.getFirst();
            String id = ops.getStringValue(stringEntry).getOrThrow(false, SpawnLib.LOGGER::error);
            Map<T, T> innerMap = new HashMap<>();
            DataResult<MapLike<T>> entryMapLike = ops.getMap(mapLike.get(stringEntry));
            if (entryMapLike.result().isEmpty())
                return existingMap;
            innerMap.put(ops.createString("type"), entryMapLike.result().get().get("type"));
            innerMap.put(ops.createString("value"), entryMapLike.result().get().get("value"));
            ops.mergeToMap(existingMap, stringEntry, ops.createMap(innerMap));
            DataResult<MapLike<T>> newMapLike = ops.getMap(mapLike.get(stringEntry));
            if (newMapLike.result().isPresent()) {
                handleInnerMetadata(ops, existingMap, newMapLike.result().get());
            }
        }
        return existingMap;
    }

    @Override
    public <T> DataResult<T> encode(List<MetadataProvider> input, DynamicOps<T> ops, T prefix) {
        Map<T, T> map = new HashMap<>();
        for (SpawnPriority priority : priorities) {
            try {
                if (input.stream().filter(provider -> provider instanceof DynamicMetadataProvider<?>).allMatch(provider -> ops.getMap(((DynamicMetadataProvider<T>)provider).getInput()).result().get().get(priority.getSerializedName()) == null))
                    continue;
                T string = ops.createString(priority.getSerializedName());
                map.putAll(input.stream().filter(provider -> provider instanceof DynamicMetadataProvider<?>).map(provider -> {
                    DynamicMetadataProvider<T> newProvider = ((DynamicMetadataProvider<?>) provider).convert(ops);
                    return newProvider.getInput();
                }).collect(Collectors.toMap(t -> string, t -> ops.getMap(t).result().get().get(string))));
            } catch (Exception ex) {
                SpawnLib.LOGGER.warn("Could not encode metadata providers for priority {}. {}", priority.getSerializedName(), ex.toString());
            }
        }
        return DataResult.success(ops.createMap(map));
    }
}
