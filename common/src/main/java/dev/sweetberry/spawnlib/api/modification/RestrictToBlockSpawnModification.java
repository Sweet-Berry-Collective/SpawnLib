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
            SpawnLibFieldCodec.codec(SpawnLibMetadataTypes.BLOCKS).fieldOf("blocks").forGetter(modification -> modification.blocks),
            ExtraCodecs.strictOptionalField(SpawnLibFieldCodec.codec(SpawnLibMetadataTypes.VEC3), "offset", new Field<>(SpawnLibCodecs.EMPTY_VEC3)).forGetter(modification -> modification.offset),
            ExtraCodecs.strictOptionalField(SpawnLibFieldCodec.codec(SpawnLibMetadataTypes.BOUNDING_BOX), "bounds", new Field<>(new BoundingBox(0, 0, 0, 0, 0, 0))).forGetter(modification -> modification.bounds),
            ExtraCodecs.strictOptionalField(SpawnLibFieldCodec.codec(SpawnLibMetadataTypes.INT), "amount").forGetter(modification -> modification.amount),
            ExtraCodecs.strictOptionalField(SpawnLibFieldCodec.codec(SpawnLibMetadataTypes.BOOLEAN), "inverted", new Field<>(false)).forGetter(modification -> modification.inverted)
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

    public HolderSet<Block> getBlocks() {
        return blocks.get();
    }

    public Vec3 getOffset() {
        return new Vec3(getOffset(offset.get().x), getOffset(offset.get().y), getOffset(offset.get().z));
    }

    public BoundingBox getBounds() {
        return this.bounds.get();
    }

    public int getRequiredAmount() {
        return this.amount.map(Field::get).orElse(bounds.get().getLength().getX() + bounds.get().getLength().getY() + bounds.get().getLength().getZ());
    }

    public boolean isInverted() {
        return inverted.get();
    }


    @Override
    public boolean modify(SpawnContext context) {
        ServerLevel level = context.getLevel();
        Vec3 spawnPos = context.getSpawnPos();

        long successful = BlockPos.betweenClosedStream(getBounds().moved((int)(spawnPos.x + getOffset().x), (int)(spawnPos.y + getOffset().y), (int)(spawnPos.z + getOffset().z))).filter(pos -> getBlocks().contains(level.getBlockState(pos).getBlockHolder())).count();

        return successful == getRequiredAmount() ^ isInverted();
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
