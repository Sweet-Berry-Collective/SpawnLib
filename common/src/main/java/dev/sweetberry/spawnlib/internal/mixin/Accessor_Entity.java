package dev.sweetberry.spawnlib.internal.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface Accessor_Entity {
    /**
     * Get the dimensions to create a bounding box in {@link dev.sweetberry.spawnlib.api.SpawnContext}
     * */
    @Accessor
    EntityDimensions getDimensions();
}
