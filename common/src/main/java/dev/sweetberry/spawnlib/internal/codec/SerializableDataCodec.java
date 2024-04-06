package dev.sweetberry.spawnlib.internal.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.sweetberry.spawnlib.api.ModifiedSpawn;
import dev.sweetberry.spawnlib.api.SpawnLibRegistryKeys;
import dev.sweetberry.spawnlib.api.modification.SerializableSpawnModification;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.Map;

public class SerializableDataCodec implements Codec<Map<ResourceKey<ModifiedSpawn>, Object>> {
    public static final SerializableDataCodec INSTANCE = new SerializableDataCodec();

    private SerializableDataCodec() {

    }

    @Override
    public <T> DataResult<Pair<Map<ResourceKey<ModifiedSpawn>, Object>, T>> decode(DynamicOps<T> ops, T input) {
        if (ops instanceof RegistryOps<T> registryOps) {
            Map<ResourceKey<ModifiedSpawn>, Object> map = new HashMap<>();
            for (Pair<T, T> mapLike : registryOps.getMap(input).getOrThrow(false, s -> {}).entries().toList()) {
                try {
                    Holder<ModifiedSpawn> holder = ModifiedSpawn.CODEC.decode(registryOps, mapLike.getFirst()).getOrThrow(false, s -> {}).getFirst();
                    if (!holder.isBound() || holder.unwrapKey().isEmpty() || !(holder.value() instanceof SerializableSpawnModification<?> serializable)) {
                        continue;
                    }
                    map.put(holder.unwrapKey().get(), serializable.getSerializableCodec().decode(registryOps, mapLike.getSecond()).getOrThrow(false, s -> {}).getFirst());
                } catch (Exception ignored) {
                }
            }

            return DataResult.success(Pair.of(map, input));
        }

        return DataResult.error(() -> "Decoding SerializableDataCodec requires RegistryOps.");
    }

    @Override
    public <T> DataResult<T> encode(Map<ResourceKey<ModifiedSpawn>, Object> input, DynamicOps<T> ops, T prefix) {
        if (ops instanceof RegistryOps<T> registryOps) {
            Map<T, T> finalMap = new HashMap<>();
            HolderGetter<ModifiedSpawn> getter = registryOps.getter(SpawnLibRegistryKeys.SPAWN).get();
            for (Map.Entry<ResourceKey<ModifiedSpawn>, Object> entry : input.entrySet()) {
                Holder<ModifiedSpawn> holder = getter.getOrThrow(entry.getKey());
                if (!holder.isBound() || !(holder.value() instanceof SerializableSpawnModification<?> serializable))
                    continue;
                finalMap.put(ops.createString(entry.getKey().location().toString()), ((Codec<Object>)serializable.getSerializableCodec()).encodeStart(registryOps, entry.getValue()).getOrThrow(false, s -> {}));
            }
            return DataResult.success(ops.createMap(finalMap));
        }
        return DataResult.error(() -> "Encoding SerializableDataCodec requires RegistryOps.");
    }
}
