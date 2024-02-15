package dev.sweetberry.spawnlib.internal.mixin;

import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelStorageSource.LevelStorageAccess.class)
public interface Accessor_LevelStorageAccess {
    @Accessor
    LevelStorageSource.LevelDirectory getLevelDirectory();
}
