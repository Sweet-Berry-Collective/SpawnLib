package dev.sweetberry.spawnlib.api.modification.logic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.codec.CasesCodec;
import dev.sweetberry.spawnlib.api.codec.TypeAndFieldCodec;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.Metadata;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.api.util.Case;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class MatchSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("match");

    public static final Codec<MatchSpawnModification> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            TypeAndFieldCodec.metadataOnlyCodec().fieldOf("input").forGetter(modification -> modification.value),
            Metadata.CODEC.fieldOf("output").forGetter(modification -> modification.output),
            CasesCodec.codec("cases", "input", "output").forGetter(modification -> modification.cases),
            SpawnModification.CODEC.listOf().fieldOf("functions").forGetter(modification -> modification.functions)
    ).apply(inst, (t1, t2, t3, t4) -> new MatchSpawnModification(t1, (Metadata<Object>) t2, t3, t4)));

    private final Field<Object> value;
    private final Metadata<Object> output;
    private final List<Case<Object, Object>> cases;
    private final List<SpawnModification> functions;

    public MatchSpawnModification(Field<Object> value, Metadata<Object> output, List<Case<Object, Object>> cases, List<SpawnModification> functions) {
        this.value = value;
        this.output = output;
        this.cases = cases;
        this.functions = functions;
    }

    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Codec<? extends SpawnModification> getCodec() {
        return CODEC;
    }

    @Override
    public List<Metadata<?>> getBuiltInMetadata() {
        return List.of(output);
    }
}
