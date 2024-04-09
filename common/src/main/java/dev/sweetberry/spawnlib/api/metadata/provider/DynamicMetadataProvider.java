package dev.sweetberry.spawnlib.api.metadata.provider;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import dev.sweetberry.spawnlib.api.SpawnPriority;
import dev.sweetberry.spawnlib.api.metadata.MetadataType;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.RegistryOps;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DynamicMetadataProvider<TOps> implements MetadataProvider {
    private final RegistryOps<TOps> ops;
    private final TOps input;

    public DynamicMetadataProvider(DynamicOps<TOps> ops, TOps input) {
        this.ops = ops instanceof RegistryOps<TOps> registryOps
                ? registryOps
                : RegistryOps.create(ops, SpawnLib.getHelper().getServer().registryAccess());
        this.input = input;
    }

    public <T> DynamicMetadataProvider<T> convert(DynamicOps<T> newOps) {
        return new DynamicMetadataProvider<>(newOps, ops.convertTo(newOps, input));
    }

    @Override
    public <T> Optional<T> getData(SpawnPriority priority, @Nullable String scope, String id, MetadataType<T> metadataType) {
        if (scope != null)
            return Optional.empty();
        Optional<T> value = Optional.empty();
        var baseMap = ops.getMap(input).getOrThrow(false, s -> {});
        var priorityMap = ops.getMap(baseMap.get(priority.getSerializedName())).getOrThrow(false, s -> {});
        var base = priorityMap.get("metadata");
        if (base == null)
            return Optional.empty();
        // FIXME: Fix scoped values not working.
        for (var innerEntry : ops.getMap(base).getOrThrow(false, s -> {}).entries().toList()) {
            String idToCheck = ops.getStringValue(innerEntry.getFirst()).getOrThrow(false, s -> {});
            if (idToCheck.equals(id)) {
                MapLike<TOps> mapLike = ops.getMap(innerEntry.getSecond()).getOrThrow(false, s -> {});
                value = metadataType.codec().decode(ops, mapLike.get("value")).result().map(Pair::getFirst);
            }
        }
        return value;
    }

    public TOps getInput() {
        return input;
    }
}
