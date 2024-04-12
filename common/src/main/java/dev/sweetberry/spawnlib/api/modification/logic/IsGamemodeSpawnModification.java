package dev.sweetberry.spawnlib.api.modification.logic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.sweetberry.spawnlib.api.SpawnContext;
import dev.sweetberry.spawnlib.api.codec.SpawnLibFieldCodec;
import dev.sweetberry.spawnlib.api.metadata.Field;
import dev.sweetberry.spawnlib.api.metadata.SpawnLibMetadataTypes;
import dev.sweetberry.spawnlib.api.metadata.provider.MetadataProvider;
import dev.sweetberry.spawnlib.api.modification.SpawnModification;
import dev.sweetberry.spawnlib.internal.SpawnLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;

import java.util.List;

public record IsGamemodeSpawnModification(Field<GameType> type) implements SpawnModification {
    public static final ResourceLocation ID = SpawnLib.id("is_gamemode");

    public static final Codec<IsGamemodeSpawnModification> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SpawnLibFieldCodec.codec(SpawnLibMetadataTypes.GAME_TYPE).fieldOf("gamemode").forGetter(modification -> modification.type)
    ).apply(inst, IsGamemodeSpawnModification::new));

    @Override
    public boolean modify(SpawnContext context, List<MetadataProvider> providers) {
        return context.getPlayer().gameMode.getGameModeForPlayer() == type.get(context, providers);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Codec<? extends SpawnModification> getCodec() {
        return CODEC;
    }
}