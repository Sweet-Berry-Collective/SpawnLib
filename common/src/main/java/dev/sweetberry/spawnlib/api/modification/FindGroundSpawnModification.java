package dev.sweetberry.spawnlib.api.modification;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.codec.SpawnLibFieldCodec;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.SpawnLibMetadataTypes;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.commands.arguments.HeightmapTypeArgument;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class FindGroundSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("find_ground");

    public static final Codec<FindGroundSpawnModification> CODEC = Codec.unit(FindGroundSpawnModification::new);

    public FindGroundSpawnModification() {}

    @Override
    public boolean modify(SpawnContext context) {
        Vec3 ground = findGround(context, context.getLevel(), context.getSpawnPos());
        // TODO: Remove this once a better method has been found.
        if (ground.y >= context.getLevel().getMinBuildHeight()) {
            context.setSpawnPos(ground);
            return true;
        }
        return false;
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

    public Vec3 findGround(SpawnContext context, ServerLevel level, Vec3 pos) {
        while (true) {
            // TODO: Come up with something better here.
            if (pos.y < level.getMinBuildHeight())
                return pos;
            var currValid = isValidForSpawning(context, level, pos);
            var downValid = isValidForSpawning(context, level, pos.subtract(0, 1, 0));
            if (currValid && !downValid)
                // This is the lowest valid spot.
                return pos;
            if (!currValid)
                // Move up to check block above
                pos = pos.add(0, 1, 0);
            else // Down is always valid here
                // Move down to check block below
                pos = pos.subtract(0, 1, 0);
        }
    }
}
