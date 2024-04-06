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
            SerializableDataCodec.INSTANCE.fieldOf("data").forGetter(attachment -> attachment.serializedData)
    ).apply(inst, ModifiedSpawnsAttachment::new));

    @Nullable
    private Optional<Holder<ModifiedSpawn>> globalSpawn;
    @Nullable
    private Optional<Holder<ModifiedSpawn>> localSpawn;
    private Map<ResourceKey<ModifiedSpawn>, Object> serializedData;

    public ModifiedSpawnsAttachment(Optional<Holder<ModifiedSpawn>> globalSpawn, Optional<Holder<ModifiedSpawn>> localSpawn, Map<ResourceKey<ModifiedSpawn>, Object> serializedData) {
        this.globalSpawn = globalSpawn;
        this.localSpawn = localSpawn;
        this.serializedData = serializedData;
    }

    public ModifiedSpawnsAttachment() {
        this(Optional.empty(), Optional.empty(), Map.of());
    }

    public Map<ResourceKey<ModifiedSpawn>, Object> createSerializedData(RegistryOps<?> registryOps, Map<ResourceKey<ModifiedSpawn>, Object> loaded) {
        Map<ResourceKey<ModifiedSpawn>, Object> map = new HashMap<>();

        Holder<ModifiedSpawn> worldSpawn = ((Duck_MinecraftServer)SpawnLib.getHelper().getServer()).spawnlib$getGlobalSpawn();
        if (isSpawnSerializable(worldSpawn)) {
            map.put(worldSpawn.unwrapKey().get(), ((SerializableSpawnModification<Object>)worldSpawn.value()).getDefaultSerializableValue());
        }
        if (this.globalSpawn.isPresent() && isSpawnSerializable(this.globalSpawn.get())) {
            map.put(this.globalSpawn.get().unwrapKey().get(), ((SerializableSpawnModification<Object>)this.globalSpawn.get()).getDefaultSerializableValue());
        }
        if (this.localSpawn.isPresent() && isSpawnSerializable(this.localSpawn.get())) {
            map.put(this.localSpawn.get().unwrapKey().get(), ((SerializableSpawnModification<Object>)localSpawn.get()).getDefaultSerializableValue());
        }

        for (Map.Entry<ResourceKey<ModifiedSpawn>, Object> entry : loaded.entrySet()) {
            if (!isSpawnSerializable(registryOps.getter(SpawnLibRegistryKeys.SPAWN).get().getOrThrow(entry.getKey()))) {
                continue;
            }
            map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }

    private boolean isSpawnSerializable(Holder<ModifiedSpawn> spawn) {
        return spawn.isBound() && spawn.unwrapKey().isPresent() && spawn.value() instanceof SerializableSpawnModification<?>;
    }

    @Nullable
    public ModifiedSpawn getGlobalSpawn() {
        if (globalSpawn.isEmpty() || !globalSpawn.get().isBound()) {
            return null;
        }
        return globalSpawn.get().value();
    }

    @Nullable
    public ModifiedSpawn getLocalSpawn() {
        if (localSpawn.isEmpty() || !localSpawn.get().isBound()) {
            return null;
        }
        return localSpawn.get().value();
    }

    public <T> T getData(Holder<ModifiedSpawn> priority) {
        if (!this.serializedData.containsKey(priority)) {
            return null;
        }
        return (T) this.serializedData.get(priority);
    }

}
