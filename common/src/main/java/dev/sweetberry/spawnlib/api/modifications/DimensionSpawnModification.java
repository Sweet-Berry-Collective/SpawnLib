package dev.sweetberry.spawnlib.api.modifications;

import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DimensionSpawnModification implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("dimension");

    public ResourceKey<Level> dimension;

    public DimensionSpawnModification(ResourceKey<Level> dimension) {
        this.dimension = dimension;
    }

    public DimensionSpawnModification() {
        this(Level.OVERWORLD);
    }

    @Override
    public boolean modify(SpawnContext context) {
        // TODO!
        context.setSpawnPos(findLowestValidSpawn(context, context.getLevel(dimension), Vec3.ZERO));
        return true;
    }

    @Override
    public void toTag(CompoundTag nbt) {
        nbt.putString("dimension", dimension.location().toString());
    }

    @Override
    public void fromTag(CompoundTag nbt) {
        if (!nbt.contains("dimension"))
            return;
        dimension = ResourceKey.create(
                Registries.DIMENSION,
                new ResourceLocation(nbt.getString("dimension"))
        );
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
