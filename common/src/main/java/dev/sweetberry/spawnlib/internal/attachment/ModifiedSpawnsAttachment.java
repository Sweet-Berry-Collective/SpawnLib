package dev.sweetberry.spawnlib.internal.attachment;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.ModifiedSpawn;
import dev.sweetberry.spawnlib.api.SpawnLibRegistryKeys;
import dev.sweetberry.spawnlib.api.SpawnPriority;
import dev.sweetberry.spawnlib.api.modification.SerializableSpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import dev.sweetberry.spawnlib.internal.codec.SerializableDataCodec;
import dev.sweetberry.spawnlib.internal.duck.Duck_MinecraftServer;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModifiedSpawnsAttachment {
    public static final ResourceLocation ID = SpawnLib.id("modified_spawns");

    public static final Codec<ModifiedSpawnsAttachment> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ModifiedSpawn.CODEC.optionalFieldOf("global").forGetter(attachment -> attachment.globalSpawn),
            ModifiedSpawn.CODEC.optionalFieldOf("local").forGetter(attachment -> attachment.localSpawn),
            SerializableDataCodec.INSTANCE.fieldOf("data").forGetter(attachment -> attachment.data)
    ).apply(inst, ModifiedSpawnsAttachment::new));

    @Nullable
    private Optional<Holder<ModifiedSpawn>> globalSpawn;
    @Nullable
    private Optional<Holder<ModifiedSpawn>> localSpawn;

    // TODO: Merge data and metadata.
    private Map<ResourceKey<ModifiedSpawn>, Object> data;

    public ModifiedSpawnsAttachment(Optional<Holder<ModifiedSpawn>> globalSpawn, Optional<Holder<ModifiedSpawn>> localSpawn, Map<ResourceKey<ModifiedSpawn>, Object> data) {
        this.globalSpawn = globalSpawn;
        this.localSpawn = localSpawn;
        this.data = data;
    }

    public ModifiedSpawnsAttachment() {
        this(Optional.empty(), Optional.empty(), Map.of());
    }

    private void setIfexists(Map<ResourceKey<ModifiedSpawn>, Object> map, @Nullable Holder<ModifiedSpawn> spawn) {
        if (spawn == null)
            return;
        if (isSpawnSerializable(spawn))
            map.put(spawn.unwrapKey().get(), ((SerializableSpawnModification<Object>)spawn.value()).getDefaultSerializableValue());
    }

    public Map<ResourceKey<ModifiedSpawn>, Object> createSerializedData(RegistryOps<?> registryOps, Map<ResourceKey<ModifiedSpawn>, Object> loaded) {
        Map<ResourceKey<ModifiedSpawn>, Object> map = new HashMap<>();

        Holder<ModifiedSpawn> worldSpawn = ((Duck_MinecraftServer)SpawnLib.getHelper().getServer()).spawnlib$getGlobalSpawn();
        setIfexists(map, worldSpawn);
        setIfexists(map, globalSpawn.orElse(null));
        setIfexists(map, localSpawn.orElse(null));

        for (Map.Entry<ResourceKey<ModifiedSpawn>, Object> entry : loaded.entrySet()) {
            if (!isSpawnSerializable(registryOps.getter(SpawnLibRegistryKeys.SPAWN).get().getOrThrow(entry.getKey())))
                continue;
            map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }

    private boolean isSpawnSerializable(Holder<ModifiedSpawn> spawn) {
        return spawn.isBound() && spawn.unwrapKey().isPresent() && spawn.value() instanceof SerializableSpawnModification<?>;
    }

    @Nullable
    public ModifiedSpawn getGlobalSpawn() {
        if (globalSpawn.isEmpty() || !globalSpawn.get().isBound())
            return null;
        return globalSpawn.get().value();
    }

    @Nullable
    public ModifiedSpawn getLocalSpawn() {
        if (localSpawn.isEmpty() || !localSpawn.get().isBound())
            return null;
        return localSpawn.get().value();
    }

    public <T> T getData(Holder<ModifiedSpawn> spawnHolder) {
        if (!this.data.containsKey(spawnHolder))
            return null;
        return (T) this.data.get(spawnHolder);
    }

}
