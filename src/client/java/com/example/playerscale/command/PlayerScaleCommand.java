package com.example.playerscale.command;

import com.example.playerscale.ScaleManager;
import com.example.playerscale.screen.PlayerScaleScreen;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

public final class PlayerScaleCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess) {
        dispatcher.register(
            ClientCommandManager.literal("playerscale")
                .executes(ctx -> {
                    MinecraftClient.getInstance().send(() ->
                            MinecraftClient.getInstance().setScreen(new PlayerScaleScreen(null)));
                    return 1;
                })
                .then(ClientCommandManager.literal("set")
                    .then(ClientCommandManager.argument("player", StringArgumentType.word())
                        .then(ClientCommandManager.argument("scale", FloatArgumentType.floatArg(0.1f, 10.0f))
                            .executes(ctx -> {
                                String playerName = StringArgumentType.getString(ctx, "player");
                                float scale = FloatArgumentType.getFloat(ctx, "scale");
                                return executeSet(ctx.getSource(), playerName, scale);
                            })
                        )
                    )
                )
                .then(ClientCommandManager.literal("reset")
                    .then(ClientCommandManager.argument("player", StringArgumentType.word())
                        .executes(ctx -> {
                            String playerName = StringArgumentType.getString(ctx, "player");
                            return executeReset(ctx.getSource(), playerName);
                        })
                    )
                )
                .then(ClientCommandManager.literal("resetall")
                    .executes(ctx -> {
                        ScaleManager.resetAll();
                        ctx.getSource().sendFeedback(Text.literal("Reset all player scales."));
                        return 1;
                    })
                )
        );
    }

    private static int executeSet(FabricClientCommandSource source, String playerName, float scale) {
        ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
        if (handler == null) {
            source.sendError(Text.literal("Not connected to a server."));
            return 0;
        }
        PlayerListEntry entry = handler.getPlayerListEntry(playerName);
        if (entry == null) {
            source.sendError(Text.literal("Player not found: " + playerName));
            return 0;
        }
        ScaleManager.setScale(entry.getProfile().id(), scale);
        source.sendFeedback(Text.literal("Set " + playerName + " scale to " + scale));
        return 1;
    }

    private static int executeReset(FabricClientCommandSource source, String playerName) {
        ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
        if (handler == null) {
            source.sendError(Text.literal("Not connected to a server."));
            return 0;
        }
        PlayerListEntry entry = handler.getPlayerListEntry(playerName);
        if (entry == null) {
            source.sendError(Text.literal("Player not found: " + playerName));
            return 0;
        }
        ScaleManager.resetScale(entry.getProfile().id());
        source.sendFeedback(Text.literal("Reset " + playerName + " scale."));
        return 1;
    }
}
