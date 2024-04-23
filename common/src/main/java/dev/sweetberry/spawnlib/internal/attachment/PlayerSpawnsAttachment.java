package dev.sweetberry.spawnlib.internal.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.ModifiedSpawn;
import dev.sweetberry.spawnlib.api.SpawnPriority;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import dev.sweetberry.spawnlib.internal.codec.MetadataProviderCodec;
import dev.sweetberry.spawnlib.internal.codec.RegistryOpsCodec;
import dev.sweetberry.spawnlib.internal.util.MetadataUtil;
import net.minecraft.core.Holder;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerSpawnsAttachment {
    public static final ResourceLocation ID = SpawnLib.id("modified_spawns");

    public static final Codec<PlayerSpawnsAttachment> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RegistryOpsCodec.codec(ModifiedSpawn.CODEC).optionalFieldOf("global").forGetter(attachment -> attachment.globalSpawn),
            RegistryOpsCodec.codec(ModifiedSpawn.CODEC).optionalFieldOf("local").forGetter(attachment -> attachment.localSpawn),
            MetadataProviderCodec.PLAYER_INSTANCE.optionalFieldOf("metadata", new ArrayList<>()).forGetter(attachment -> attachment.providers)
    ).apply(inst, PlayerSpawnsAttachment::new));

    private Optional<Holder<ModifiedSpawn>> globalSpawn;
    private Optional<Holder<ModifiedSpawn>> localSpawn;
    private final List<MetadataProvider> providers;

    public PlayerSpawnsAttachment(Optional<Holder<ModifiedSpawn>> globalSpawn, Optional<Holder<ModifiedSpawn>> localSpawn, List<MetadataProvider> providers) {
        this.globalSpawn = globalSpawn;
        this.localSpawn = localSpawn;
        this.providers = getOrCreateProviders(providers);
    }

    public PlayerSpawnsAttachment() {
        this(Optional.empty(), Optional.empty(), new ArrayList<>());
    }

    private List<MetadataProvider> getOrCreateProviders(List<MetadataProvider> providers) {
        if (providers.isEmpty()) {
            globalSpawn.ifPresent(spawnHolder -> MetadataUtil.createProvidersForPriority(providers, SpawnPriority.GLOBAL_PLAYER, spawnHolder));
            localSpawn.ifPresent(spawnHolder -> MetadataUtil.createProvidersForPriority(providers, SpawnPriority.LOCAL_PLAYER, spawnHolder));
        }
        validateMetadata(providers);
        return providers;
    }

    // TODO: This.
    private List<MetadataProvider> validateMetadata(List<MetadataProvider> providers) {
        return providers;
    }

    private void createMetadataProviders(Holder<ModifiedSpawn> spawn, SpawnPriority priority, Tag tag) {
        List<MetadataProvider> providers = new MetadataProviderCodec(priority).decode(NbtOps.INSTANCE, tag).getOrThrow().getFirst();
        MetadataUtil.createProvidersForPriority(providers, priority, spawn);
    }

    @Nullable
    public Holder<ModifiedSpawn> getGlobalSpawn() {
        return globalSpawn.orElse(null);
    }

    public void setGlobalSpawn(Holder<ModifiedSpawn> spawn, Tag metadata) {
        globalSpawn = Optional.of(spawn);
        createMetadataProviders(spawn, SpawnPriority.LOCAL_PLAYER, metadata);
    }

    public boolean clearGlobalSpawn() {
        if (this.globalSpawn.isEmpty())
            return false;
        this.globalSpawn = Optional.empty();
        validateMetadata(this.providers);
        return true;
    }

    @Nullable
    public Holder<ModifiedSpawn> getLocalSpawn() {
        return localSpawn.orElse(null);
    }

    public void setLocalSpawn(Holder<ModifiedSpawn> spawn, Tag metadata) {
        localSpawn = Optional.of(spawn);
        createMetadataProviders(spawn, SpawnPriority.LOCAL_PLAYER, metadata);
    }

    public boolean clearLocalSpawn() {
        if (localSpawn.isEmpty())
            return false;
        localSpawn = Optional.empty();
        validateMetadata(providers);
        return true;
    }

    public List<MetadataProvider> getProviders() {
        return providers;
    }

}
