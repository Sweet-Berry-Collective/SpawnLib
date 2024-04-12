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
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

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
                    .then(setNode(priority))
                    .then(getNode(priority));
        }
        return Commands
                .literal("player")
                .then(getNode(priority))
                .then(setNode(priority))
                .then(clearNode(priority));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setNode(SpawnPriority priority) {
        var command = Commands
                .literal("set");

        if (priority != SpawnPriority.GLOBAL_WORLD)
            return command.then(Commands
                    .argument("players", EntityArgument.players())
                    .then(Commands
                            .argument("id", ResourceKeyArgument.key(SpawnLibRegistryKeys.SPAWN))
                            .executes(context -> set(context, priority, false))
                            .then(Commands
                                    .argument("data", NbtTagArgument.nbtTag())
                                    .executes(context -> set(context, priority, true))
                            )));

        return command.then(Commands
                    .argument("id", ResourceKeyArgument.key(SpawnLibRegistryKeys.SPAWN))
                    .executes(context -> set(context, priority, false))
                    .then(Commands
                        .argument("data", NbtTagArgument.nbtTag())
                        .executes(context -> set(context, priority, true))
                    ));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> getNode(SpawnPriority priority) {
        var command = Commands
                .literal("get");

        if (priority != SpawnPriority.GLOBAL_WORLD)
            return command.then(Commands
                    .argument("player", EntityArgument.player())
                    .executes(context -> get(context, priority)));

        return command.executes(context -> get(context, priority));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> clearNode(SpawnPriority priority) {
        return Commands
                .literal("clear")
                .then(Commands
                        .argument("players", EntityArgument.players())
                        .executes(context -> clear(context, priority)));

    }

    private static int set(CommandContext<CommandSourceStack> context, SpawnPriority priority, boolean specifiedData) throws CommandSyntaxException {
        Holder<ModifiedSpawn> holder = resolveKey(context, "id");
        Tag tag = specifiedData ? NbtTagArgument.getNbtTag(context, "data") : new CompoundTag();
        Collection<ServerPlayer> players = priority != SpawnPriority.GLOBAL_WORLD ? EntityArgument.getPlayers(context, "players") : List.of();
        switch (priority) {
            case GLOBAL_WORLD -> {
                SpawnExtensions.setGlobalSpawn(context.getSource().getServer(), holder, tag);
                logSetSuccess(context, players, holder, priority);
                return 1;
            }
            case GLOBAL_PLAYER -> {
                players.forEach(player -> SpawnExtensions.setGlobalSpawn(player, holder, tag));
                logSetSuccess(context, players, holder, priority);
                return players.size();
            }
            case LOCAL_PLAYER -> {
                players.forEach(player -> SpawnExtensions.setLocalSpawn(player, holder, tag));
                logSetSuccess(context, players, holder, priority);
                return players.size();
            }
        }
        return 0;
    }

    private static int get(CommandContext<CommandSourceStack> context, SpawnPriority priority) throws CommandSyntaxException {
        ServerPlayer player = priority != SpawnPriority.GLOBAL_WORLD ? EntityArgument.getPlayer(context, "player") : null;
        switch (priority) {
            case GLOBAL_WORLD -> {
                Holder<ModifiedSpawn> holder = SpawnExtensions.getGlobalSpawn(context.getSource().getServer());
                logGetSuccess(context, null, holder, priority);
                return holder != null && holder.isBound() ? 1 : 0;
            }
            case GLOBAL_PLAYER -> {
                Holder<ModifiedSpawn> holder = SpawnExtensions.getGlobalSpawn(player);
                logGetSuccess(context, player, holder, priority);
                return holder != null && holder.isBound() ? 1 : 0;
            }
            case LOCAL_PLAYER -> {
                Holder<ModifiedSpawn> holder = SpawnExtensions.getLocalSpawn(player);
                logGetSuccess(context, player, holder, priority);
                return holder != null && holder.isBound() ? 1 : 0;
            }
        }
        return 0;
    }

    private static int clear(CommandContext<CommandSourceStack> context, SpawnPriority priority) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "players");
        switch (priority) {
            case GLOBAL_PLAYER -> {
                int successful = 0;
                for (ServerPlayer player : players) {
                    if (SpawnExtensions.clearGlobalSpawn(player))
                        successful += 1;
                }
                logClearSuccess(context, players.stream().findAny().get().getScoreboardName(), successful, players.size() == 1, priority);
                return successful;
            }
            case LOCAL_PLAYER -> {
                int successful = 0;
                for (ServerPlayer player : players) {
                    if (SpawnExtensions.clearLocalSpawn(player))
                        successful += 1;
                }
                logClearSuccess(context, players.stream().findAny().get().getScoreboardName(), successful, players.size() == 1, priority);
                return successful;
            }
        }
        return 0;
    }

    private static void logSetSuccess(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> players, Holder<ModifiedSpawn> spawn, SpawnPriority priority) {
        String priorityName = priority == SpawnPriority.LOCAL_PLAYER ? "local" : "global";
        if (players.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.translatableWithFallback("commands.spawnlib.set.success.world", "Set world spawn to " + spawn.unwrapKey().get().location() + ".", spawn.unwrapKey().get().location()), true);
            return;
        }
        if (players.size() > 1)
            context.getSource().sendSuccess(() -> Component.translatableWithFallback("commands.spawnlib.set.success.multiple", "Set " + priorityName + " spawn to " + spawn.unwrapKey().get().location() + " for " + players.size() + " players.", priorityName, spawn.unwrapKey().get().location(), players.size()), true);
        else
            context.getSource().sendSuccess(() -> Component.translatableWithFallback("commands.spawnlib.set.success.single", "Set " + priorityName + " spawn to " + spawn.unwrapKey().get().location() + " for " + players.stream().findAny().get().getScoreboardName() + ".", priorityName, spawn.unwrapKey().get().location(), players.stream().findAny().get().getScoreboardName()), true);
    }

    private static void logGetSuccess(CommandContext<CommandSourceStack> context, @Nullable ServerPlayer player, Holder<ModifiedSpawn> spawn, SpawnPriority priority) {
        String priorityName = priority == SpawnPriority.LOCAL_PLAYER ? "local" : "global";
        if (player == null) {
            if (spawn != null && spawn.isBound())
                context.getSource().sendSuccess(() -> Component.translatableWithFallback("commands.spawnlib.get.success.world", "The current world spawn is " + spawn.unwrapKey().get().location() + ".", spawn.unwrapKey().get().location()), true);
            else
                context.getSource().sendFailure(Component.translatableWithFallback("commands.spawnlib.get.fail.world", "The current world does not have a spawn."));
            return;
        }
        if (spawn != null && spawn.isBound())
            context.getSource().sendSuccess(() -> Component.translatableWithFallback("commands.spawnlib.get.success.player", "The current " + priorityName + " spawn for " + player.getScoreboardName() + " is " + spawn.unwrapKey().get().location() + ".", priorityName, player.getScoreboardName(), spawn.unwrapKey().get().location()), true);
        else
            context.getSource().sendFailure(Component.translatableWithFallback("commands.spawnlib.get.fail.player",  player.getScoreboardName() + " does not have a " + priorityName + "spawn.", player.getScoreboardName(), priorityName));
    }

    private static void logClearSuccess(CommandContext<CommandSourceStack> context, @Nullable String playerName, int amount, boolean isSingular, SpawnPriority priority) {
        String priorityName = priority == SpawnPriority.LOCAL_PLAYER ? "local" : "global";
        if (amount == 0) {
            if (isSingular)
                context.getSource().sendFailure(Component.translatableWithFallback("commands.spawnlib.clear.fail.single", playerName + " does not have a " + priorityName + " spawn.", priorityName));
            else
                context.getSource().sendFailure(Component.translatableWithFallback("commands.spawnlib.clear.fail.multiple", "All of the specified players do not have a " + priorityName + " spawn.", priorityName));
            return;
        }
        if (amount > 1)
            context.getSource().sendSuccess(() -> Component.translatableWithFallback("commands.spawnlib.clear.success.multiple", "Cleared " + priorityName + " spawn for " + amount + " players.", priorityName, amount), true);
        else
            context.getSource().sendSuccess(() -> Component.translatableWithFallback("commands.spawnlib.clear.success.single", "Cleared " + priorityName + " spawn for player " + playerName + ".", priorityName, playerName), true);
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
