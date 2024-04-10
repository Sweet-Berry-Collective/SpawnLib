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
import java.util.stream.Collector;

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
            var mapLike = ops.getMap(input);
            if (mapLike.result().isEmpty())
                continue;
            var mapLikeResult = ops.getMap(mapLike.result().get().get(priority.getSerializedName()));
            if (mapLikeResult.result().isEmpty())
                // We don't want to error when a value isn't present.
                continue;
            var priorityMapLike = mapLikeResult.result().get();
            Set<T> metadataSet = new HashSet<>();
            handleMetadata(ops, priorityMapLike.get("metadata"), priorityMapLike, metadataSet, priority);
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

        metadataSet.add(ops.createMap(Map.of(ops.createString(priority.getSerializedName()), value)));
    }

    private static <T> T handleInnerMetadata(DynamicOps<T> ops, T existingMap, MapLike<T> mapLike) {
        for (Pair<T, T> entries : mapLike.entries().toList()) {
            T stringEntry = entries.getFirst();
            Map<T, T> innerMap = new HashMap<>();
            DataResult<MapLike<T>> entryMapLike = ops.getMap(mapLike.get(stringEntry));
            if (entryMapLike.result().isEmpty())
                return existingMap;
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
                final List<String>[] keys = new List[]{new ArrayList<>()};
                T string = ops.createString(priority.getSerializedName());
                if (input.stream().filter(provider -> {
                    if (!(provider instanceof DynamicMetadataProvider<?> dynamic))
                        return false;
                    T t = (T) dynamic.getInput();
                    var initialMap = ops.getMap(t);
                    if (initialMap.result().isEmpty())
                        return false;
                    var priorityMap = ops.getMap(ops.getMap(t).result().get().get(string));
                    if (priorityMap.result().isEmpty())
                        return false;
                    boolean bl = hasNewValue(keys[0], ops, priorityMap.result().get(), "");
                    keys[0] = getValues(ops, priorityMap.result().get(), "");
                    return bl;
                }).allMatch(provider -> ops.getMap(((DynamicMetadataProvider<T>)provider).getInput()).result().get().get(priority.getSerializedName()) == null))
                    continue;
                map.putAll(input.stream().filter(provider -> provider instanceof DynamicMetadataProvider<?>).map(provider -> {
                    DynamicMetadataProvider<T> newProvider = ((DynamicMetadataProvider<?>) provider).convert(ops);
                    return newProvider.getInput();
                }).collect(Collector.of(
                        () -> new HashMap<T, T>(),
                        (hashMap, object) -> {
                            T value = ops.getMap(object).getOrThrow(false, s -> SpawnLib.LOGGER.error("Failed to encode metadata providers for {}. {}", ops.getStringValue(string), s)).get(priority.getSerializedName());
                            if (value == null)
                                return;
                            hashMap.put(string, value);
                        },
                        (map1, map2) -> {
                            map1.putAll(map2);
                            return map1;
                        },
                        hashMap -> hashMap
                )));
            } catch (Exception ex) {
                SpawnLib.LOGGER.warn("Could not encode metadata providers for priority {}. {}", priority.getSerializedName(), ex.toString());
            }
        }
        return ops.mergeToMap(prefix, map);
    }

    private static <T> List<String> getValues(DynamicOps<T> ops, MapLike<T> input, String prefix) {
        List<String> list = new ArrayList<>();
        for (Pair<T, T> entry : input.entries().toList()) {
            var key = ops.getStringValue(entry.getFirst());
            if (key.result().isEmpty() || key.result().get().equals("value"))
                continue;
            list.add(prefix + key.result().get());
            if (ops.getMap(entry.getSecond()).result().isEmpty())
                continue;
            list.addAll(getValues(ops, ops.getMap(entry.getSecond()).result().get(), prefix + key.result().get() + "."));
        }
        return list;
    }

    private static <T> boolean hasNewValue(List<String> baseMapValues, DynamicOps<T> ops, MapLike<T> input, String prefix) {
        for (Pair<T, T> entry : input.entries().toList()) {
            var key = ops.getStringValue(entry.getFirst());
            if (key.result().isEmpty() || key.result().get().equals("value"))
                continue;
            if (baseMapValues.contains(prefix + key.result().get()))
                return true;
            if (ops.getMap(entry.getSecond()).result().isEmpty())
                continue;
            boolean bl = hasNewValue(baseMapValues, ops, ops.getMap(entry.getSecond()).result().get(), prefix + key.result().get() + ".");
            if (bl)
                return true;
        }
        return false;
    }
}
