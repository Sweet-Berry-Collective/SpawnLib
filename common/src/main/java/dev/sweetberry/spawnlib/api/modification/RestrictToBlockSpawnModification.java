package dev.sweetberry.spawnlib.api.modification;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.codec.FieldCodec;
import dev.sweetberry.spawnlib.api.codec.SpawnLibCodecs;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.SpawnLibMetadataTypes;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RestrictToBlockSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("restrict_to_block");

    public static final Codec<RestrictToBlockSpawnModification> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            FieldCodec.codec(SpawnLibMetadataTypes.BLOCKS).fieldOf("blocks").forGetter(modification -> modification.blocks),
            ExtraCodecs.strictOptionalField(FieldCodec.codec(SpawnLibMetadataTypes.VEC3), "offset", new Field<>(SpawnLibCodecs.EMPTY_VEC3)).forGetter(modification -> modification.offset),
            ExtraCodecs.strictOptionalField(FieldCodec.codec(SpawnLibMetadataTypes.BOUNDING_BOX), "bounds", new Field<>(new BoundingBox(0, 0, 0, 0, 0, 0))).forGetter(modification -> modification.bounds),
            ExtraCodecs.strictOptionalField(FieldCodec.codec(SpawnLibMetadataTypes.INT), "amount").forGetter(modification -> modification.amount),
            ExtraCodecs.strictOptionalField(FieldCodec.codec(SpawnLibMetadataTypes.BOOLEAN), "inverted", new Field<>(false)).forGetter(modification -> modification.inverted)
    ).apply(inst, RestrictToBlockSpawnModification::new));

    private final Field<HolderSet<Block>> blocks;
    private final Field<Vec3> offset;
    private final Field<BoundingBox> bounds;
    private final Optional<Field<Integer>> amount;
    private final Field<Boolean> inverted;

    public RestrictToBlockSpawnModification(Field<HolderSet<Block>> blocks, Field<Vec3> offset, Field<BoundingBox> bounds, Optional<Field<Integer>> amount, Field<Boolean> inverted) {
        this.blocks = blocks;
        this.offset = offset;
        this.bounds = bounds;
        this.amount = amount;
        this.inverted = inverted;
    }

    public HolderSet<Block> getBlocks(SpawnContext context, List<MetadataProvider> providers) {
        return blocks.get(context, providers);
    }

    public Vec3 getOffset(SpawnContext context, List<MetadataProvider> providers) {
        return new Vec3(getOffset(offset.get(context, providers).x), getOffset(offset.get(context, providers).y), getOffset(offset.get(context, providers).z));
    }

    public BoundingBox getBounds(SpawnContext context, List<MetadataProvider> providers) {
        return this.bounds.get(context, providers);
    }

    public int getRequiredAmount(SpawnContext context, List<MetadataProvider> providers) {
        return this.amount.map(field -> field.get(context, providers)).orElse(bounds.get(context, providers).getXSpan() * bounds.get(context, providers).getYSpan() * bounds.get(context, providers).getZSpan());
    }

    public boolean isInverted(SpawnContext context, List<MetadataProvider> providers) {
        return inverted.get(context, providers);
    }


    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
        ServerLevel level = context.getLevel();
        Vec3 spawnPos = context.getSpawnPos();
        long successful = BlockPos.betweenClosedStream(getBounds(context, providers).moved((int)(spawnPos.x + getOffset(context, providers).x), (int)(spawnPos.y + getOffset(context, providers).y), (int)(spawnPos.z + getOffset(context, providers).z))).filter(pos -> getBlocks(context, providers).contains(level.getBlockState(pos).getBlockHolder())).count();
        return successful == getRequiredAmount(context, providers) ^ isInverted(context, providers);
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
        List<Field<?>> list = new ArrayList<>();
        list.add(blocks);
        list.add(offset);
        list.add(bounds);
        amount.ifPresent(list::add);
        list.add(inverted);
        return list;
    }
}
