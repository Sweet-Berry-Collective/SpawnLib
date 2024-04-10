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
        var priorityMap = ops.getMap(baseMap.get(priority.getSerializedName()));
        if (priorityMap.result().isEmpty())
            return Optional.empty();
        for (var innerEntry : priorityMap.result().get().entries().toList()) {
            // TODO: Test if inner data works.
            getInnerData(innerEntry, id, metadataType);
        }
        return value;
    }

    private <T> Optional<T> getInnerData(Pair<TOps, TOps> entry, String id, MetadataType<T> metadataType) {
        String[] splitId = id.split("\\.");
        String idToCheck = ops.getStringValue(entry.getFirst()).getOrThrow(false, s -> {});
        if (splitId.length == 1 && idToCheck.equals(id)) {
            MapLike<TOps> mapLike = ops.getMap(entry.getSecond()).getOrThrow(false, s -> {});
            return metadataType.codec().decode(ops, mapLike.get("value")).result().map(Pair::getFirst);
        }
        var innerMap = ops.getMap(entry.getSecond());
        if (innerMap.result().isEmpty())
            return Optional.empty();
        for (Pair<TOps, TOps> innerEntry : innerMap.result().get().entries().toList()) {
            Optional<T> innerData = getInnerData(innerEntry, id.split("\\.", 1)[1], metadataType);
            if (innerData.isPresent())
                return innerData;
        }
        return Optional.empty();
    }

    public TOps getInput() {
        return input;
    }
}
