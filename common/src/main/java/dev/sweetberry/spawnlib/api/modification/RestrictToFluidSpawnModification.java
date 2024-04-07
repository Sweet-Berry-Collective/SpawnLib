package dev.sweetberry.spawnlib.api.modification;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.codec.SpawnLibCodecs;
import dev.sweetberry.spawnlib.api.codec.SpawnLibFieldCodec;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.SpawnLibMetadataTypes;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RestrictToFluidSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("restrict_to_fluid");

    public static final Codec<RestrictToFluidSpawnModification> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SpawnLibFieldCodec.codec(SpawnLibMetadataTypes.FLUIDS).fieldOf("fluids").forGetter(modification -> modification.fluids),
            ExtraCodecs.strictOptionalField(SpawnLibFieldCodec.codec(SpawnLibMetadataTypes.VEC3), "offset", new Field<>(SpawnLibCodecs.EMPTY_VEC3)).forGetter(modification -> modification.offset),
            ExtraCodecs.strictOptionalField(SpawnLibFieldCodec.codec(SpawnLibMetadataTypes.BOOLEAN), "inverted", new Field<>(false)).forGetter(modification -> modification.inverted)
    ).apply(inst, RestrictToFluidSpawnModification::new));

    private final Field<HolderSet<Fluid>> fluids;
    private final Field<Vec3> offset;
    private final Field<Boolean> inverted;

    public RestrictToFluidSpawnModification(Field<HolderSet<Fluid>> fluids, Field<Vec3> offset, Field<Boolean> inverted) {
        this.fluids = fluids;
        this.offset = offset;
        this.inverted = inverted;
    }

    public HolderSet<Fluid> getBlocks() {
        return fluids.get();
    }

    public Vec3 getOffset() {
        return new Vec3(getOffset(offset.get().x), getOffset(offset.get().y), getOffset(offset.get().z));
    }

    public boolean isInverted() {
        return inverted.get();
    }

    @Override
    public boolean modify(SpawnContext context) {
        return getBlocks().contains(context.getLevel().getFluidState(BlockPos.containing(context.getSpawnPos().add(getOffset(getOffset().x), getOffset(getOffset().y), getOffset(getOffset().z)))).holder()) ^ isInverted();
    }

    private double getOffset(double value) {
        return Double.isNaN(value) ? 0.0 : value;
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
    public List<Field<?>> getFields() {
        return List.of();
    }
}
