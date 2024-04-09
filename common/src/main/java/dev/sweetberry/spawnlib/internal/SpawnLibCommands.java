package dev.sweetberry.spawnlib.internal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.sweetberry.spawnlib.api.ModifiedSpawn;
import dev.sweetberry.spawnlib.api.SpawnExtensions;
import dev.sweetberry.spawnlib.api.SpawnLibRegistryKeys;
import dev.sweetberry.spawnlib.api.SpawnPriority;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class SpawnLibCommands {
    public static void init(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext commandBuildContext,
            Commands.CommandSelection selection
    ) {
        dispatcher.register(Commands
                .literal("spawnlib")
                .then(Commands
                        .literal("global")
                        .then(buildNode(SpawnPriority.GLOBAL_WORLD))
                        .then(buildNode(SpawnPriority.GLOBAL_PLAYER))
                        .executes(context -> get(context, SpawnPriority.GLOBAL_WORLD))
                ).then(Commands
                        .literal("local")
                        .then(buildNode(SpawnPriority.LOCAL_PLAYER))
                )
        );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> buildNode(SpawnPriority priority) {
        if (priority == SpawnPriority.GLOBAL_WORLD) {
            return Commands
                    .literal("world")
                    .then(spawnNode(priority, false));
        }
        return Commands
                .argument("players", EntityArgument.players())
                .then(spawnNode(priority, true))
                .executes(context -> get(context, priority));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> spawnNode(SpawnPriority priority, boolean specifiedPlayers) {
        return Commands
                .argument("id", ResourceKeyArgument.key(SpawnLibRegistryKeys.SPAWN))
                .executes(context -> set(context, priority, false, specifiedPlayers))
                .then(Commands
                        .argument("data", NbtTagArgument.nbtTag())
                        .executes(context -> set(context, priority, true, specifiedPlayers))
                );
    }

    private static int set(CommandContext<CommandSourceStack> context, SpawnPriority priority, boolean specifiedData, boolean specifiedPlayers) throws CommandSyntaxException {
        Holder<ModifiedSpawn> spawnHolder = resolveKey(context, "id");
        Tag tag = specifiedData ? NbtTagArgument.getNbtTag(context, "data") : new CompoundTag();
        Collection<ServerPlayer> players = specifiedPlayers ? EntityArgument.getPlayers(context, "players") : List.of();
        switch (priority) {
            case GLOBAL_WORLD -> {
                SpawnExtensions.setGlobalSpawn(context.getSource().getServer(), spawnHolder, tag);
                logSetSuccess(context, players, spawnHolder, specifiedPlayers);
                return players.size();
            }
            case GLOBAL_PLAYER -> {
                players.forEach(player -> SpawnExtensions.setGlobalSpawn(player, spawnHolder, tag));
                logSetSuccess(context, players, spawnHolder, specifiedPlayers);
                return players.size();
            }
            case LOCAL_PLAYER -> {
                players.forEach(player -> SpawnExtensions.setLocalSpawn(player, spawnHolder, tag));
                logSetSuccess(context, players, spawnHolder, specifiedPlayers);
                return players.size();
            }
        }
        return 0;
    }

    private static int get(CommandContext<CommandSourceStack> context, SpawnPriority priority) {
        return 0;
    }

    private static void logSetSuccess(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> players, Holder<ModifiedSpawn> spawn, boolean specifiedPlayers) {
        if (specifiedPlayers) {
            if (players.size() > 1)
                context.getSource().sendSuccess(() -> Component.translatableWithFallback("commands.spawnlib.set.success.multiple", "Set spawn to " + spawn.unwrapKey().get().location() + " for " + players.size() + " players.", players.size(), spawn.unwrapKey().get().location()), true);
            else
                context.getSource().sendSuccess(() -> Component.translatableWithFallback("commands.spawnlib.set.success.single", "Set spawn to " + spawn.unwrapKey().get().location() + " for player " + players.stream().findAny().get().getScoreboardName() + "." , players.stream().findAny().get().getScoreboardName(), spawn.unwrapKey().get().location()), true);
        } else
            context.getSource().sendSuccess(() -> Component.translatableWithFallback("commands.spawnlib.set.success.world", "Set spawn to " + spawn.unwrapKey().get().location() + " globally.", players.size(), spawn.unwrapKey().get().location()), true);
    }

    private static Holder<ModifiedSpawn> resolveKey(CommandContext<CommandSourceStack> context, String value) throws CommandSyntaxException {
        ResourceKey<?> key = context.getArgument(value, ResourceKey.class);
        Optional<ResourceKey<ModifiedSpawn>> optionalKey = key.cast(SpawnLibRegistryKeys.SPAWN);
        ResourceKey<ModifiedSpawn> spawnKey = optionalKey.orElseThrow(() -> new DynamicCommandExceptionType(
                s -> Component.translatableWithFallback("commands.spawnlib.spawn.invalid", "There is no spawn with type \"" + s + "\"", s)
        ).create(key));
        return context.getSource().registryAccess().registryOrThrow(SpawnLibRegistryKeys.SPAWN).getHolderOrThrow(spawnKey);
    }
}
