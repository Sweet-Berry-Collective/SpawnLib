package dev.sweetberry.spawnlib.internal.mixin;

import com.mojang.datafixers.DataFixer;
import dev.sweetberry.spawnlib.api.ModifiedSpawn;
import dev.sweetberry.spawnlib.api.SpawnLibRegistryKeys;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import dev.sweetberry.spawnlib.internal.duck.Duck_MinecraftServer;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.*;
import java.net.Proxy;

@Mixin(MinecraftServer.class)
public abstract class Mixin_MinecraftServer implements Duck_MinecraftServer {
    @Shadow @Final protected LevelStorageSource.LevelStorageAccess storageSource;

    @Unique
    @Nullable
    private Holder<ModifiedSpawn> spawnlib$globalSpawn;


    @Override
    public Holder<ModifiedSpawn> spawnlib$getGlobalSpawn() {
        return spawnlib$globalSpawn;
    }

    @Override
    public void spawnlib$setGlobalSpawn(Holder<ModifiedSpawn> value) {
        spawnlib$globalSpawn = value;
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void spawnlib$readSpawnModifications(Thread thread, LevelStorageSource.LevelStorageAccess storageAccess, PackRepository repository, WorldStem worldStem, Proxy proxy, DataFixer fixer, Services services, ChunkProgressListenerFactory chunkProgressListenerFactory, CallbackInfo ci) {
        var dir = ((Accessor_LevelStorageAccess)storageSource).getLevelDirectory();
        var file = dir.path().resolve("spawnlib.dat").toFile();
        if (!file.exists())
            return;
        try {
            var tag = CompoundTag.TYPE.load(
                    new DataInputStream(
                            new FileInputStream(file)
                    ),
                    NbtAccounter.unlimitedHeap()
            );
            spawnlib$globalSpawn = ModifiedSpawn.CODEC.decode(NbtOps.INSTANCE, tag.getCompound("")).getOrThrow(false, (s) -> SpawnLib.LOGGER.error("Could not resolve global spawn from spawnlib.dat")).getFirst();
        } catch (Exception ignored) {}
    }

    @Inject(
            method = "saveEverything",
            at = @At("TAIL")
    )
    private void spawnlib$addSpawnModifications(boolean $$0, boolean $$1, boolean $$2, CallbackInfoReturnable<Boolean> cir) {
        if (spawnlib$globalSpawn == null)
            return;
        var dir = ((Accessor_LevelStorageAccess)storageSource).getLevelDirectory();
        var file = dir.path().resolve("spawnlib.dat").toFile();
        try {
            file.createNewFile();
            var tag = new CompoundTag();
            tag.put("", ModifiedSpawn.CODEC.encodeStart(NbtOps.INSTANCE, spawnlib$globalSpawn).getOrThrow(false, (s) -> SpawnLib.LOGGER.error("Failed to encode global spawn to spawnlib.dat")));
            tag.write(
                    new DataOutputStream(
                            new FileOutputStream(file)
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
