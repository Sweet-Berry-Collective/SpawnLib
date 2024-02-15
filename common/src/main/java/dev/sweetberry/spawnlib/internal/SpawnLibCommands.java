package dev.sweetberry.spawnlib.internal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.sweetberry.spawnlib.api.SpawnPriority;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;

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

    private static RequiredArgumentBuilder<CommandSourceStack, ?> buildNode(SpawnPriority priority) {
        var command = Commands
                .argument("id", ResourceLocationArgument.id()).then(Commands
                        .argument("data", NbtTagArgument.nbtTag())
                        .executes(context -> set(context, priority))
                );
        if (priority == SpawnPriority.GLOBAL_WORLD)
            return command;
        return Commands
                .argument("player", EntityArgument.player())
                .then(command)
                .executes(context -> get(context, priority));
    }

    private static int set(CommandContext<CommandSourceStack> context, SpawnPriority priority) {
        return 0;
    }

    private static int get(CommandContext<CommandSourceStack> context, SpawnPriority priority) {
        return 0;
    }
}
