package dev.sweetberry.spawnlib.api.modification.logic;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.SpawnPriority;
import dev.sweetberry.spawnlib.api.codec.CasesCodec;
import dev.sweetberry.spawnlib.api.codec.TypeAndFieldCodec;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.Metadata;
import dev.sweetberry.spawnlib.api.metadata.MetadataType;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.api.util.Case;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record MatchSpawnModification<TInput, TOutput>(Field<TInput> value, Metadata<TOutput> output, List<Case<TInput, TOutput>> cases, List<SpawnModification> functions) implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("match");

    public static final MapCodec<MatchSpawnModification<Object, Object>> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            TypeAndFieldCodec.metadataOnlyCodec().fieldOf("input").forGetter(MatchSpawnModification::value),
            Metadata.CODEC.fieldOf("output").forGetter(MatchSpawnModification::output),
            CasesCodec.codec("cases", "input", "output").forGetter(MatchSpawnModification::cases),
            SpawnModification.CODEC.listOf().fieldOf("functions").forGetter(MatchSpawnModification::functions)
    ).apply(inst, (t1, t2, t3, t4) -> new MatchSpawnModification<>(t1, (Metadata<Object>) t2, t3, t4)));

    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
        var match = value.get(context, providers);
        var value = output.getDefaultValue();
        for (var _case : cases) {
            if (Objects.equals(match, _case.when())) {
                value = _case.value();
                break;
            }
        }

        var provider = new Provider(output.getKey(), value);
        providers.add(provider);
        var output = false;
        for (var modification : functions)
            output = modification.modify(context, providers);
        providers.remove(provider);
        return output;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends SpawnModification> getCodec() {
        return CODEC;
    }

    @Override
    public List<Metadata<?>> getBuiltInMetadata() {
        return List.of(output);
    }

    public record Provider(String id, Object value) implements MetadataProvider {
        @Override
        public <T> Optional<T> getData(SpawnPriority priority, @Nullable String scope, String id, MetadataType<T> metadataType) {
            if (Objects.equals(scope, "match") && Objects.equals(id, this.id))
                return Optional.of((T)value);
            return Optional.empty();
        }
    }
}
