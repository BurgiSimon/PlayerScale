package dev.solerbus.playerscale.command;

import dev.solerbus.playerscale.ScaleConfig;
import dev.solerbus.playerscale.ScaleManager;
import dev.solerbus.playerscale.screen.PlayerScaleScreen;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public final class PlayerScaleCommand {

    private static final SuggestionProvider<FabricClientCommandSource> PLAYER_SUGGESTIONS = (ctx, builder) -> {
        ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
        if (handler != null) {
            return CommandSource.suggestMatching(
                    handler.getPlayerList().stream().map(entry -> entry.getProfile().name()),
                    builder);
        }
        return builder.buildFuture();
    };

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
                        .suggests(PLAYER_SUGGESTIONS)
                        .then(ClientCommandManager.argument("scale", FloatArgumentType.floatArg(0.1f, 10.0f))
                            .executes(ctx -> {
                                String playerName = StringArgumentType.getString(ctx, "player");
                                float scale = FloatArgumentType.getFloat(ctx, "scale");
                                return resolveAndExecute(ctx.getSource(), playerName, entry -> {
                                    ScaleManager.setScale(entry.getProfile().id(), scale);
                                    ScaleConfig.save();
                                    ctx.getSource().sendFeedback(Text.literal("Set " + playerName + " scale to " + scale));
                                });
                            })
                        )
                    )
                )
                .then(ClientCommandManager.literal("reset")
                    .then(ClientCommandManager.argument("player", StringArgumentType.word())
                        .suggests(PLAYER_SUGGESTIONS)
                        .then(ClientCommandManager.literal("all")
                            .executes(ctx -> {
                                ScaleManager.resetAll();
                                ScaleConfig.save();
                                ctx.getSource().sendFeedback(Text.literal("Reset all player scales."));
                                return 1;
                            })
                        )
                        .executes(ctx -> {
                            String playerName = StringArgumentType.getString(ctx, "player");
                            return resolveAndExecute(ctx.getSource(), playerName, entry -> {
                                ScaleManager.resetScale(entry.getProfile().id());
                                ScaleConfig.save();
                                ctx.getSource().sendFeedback(Text.literal("Reset " + playerName + " scale."));
                            });
                        })
                    )
                )
                .then(ClientCommandManager.literal("resetall")
                    .executes(ctx -> {
                        ScaleManager.resetAll();
                        ScaleConfig.save();
                        ctx.getSource().sendFeedback(Text.literal("Reset all player scales."));
                        return 1;
                    })
                )
        );
    }

    private static int resolveAndExecute(FabricClientCommandSource source, String playerName,
                                          java.util.function.Consumer<PlayerListEntry> action) {
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
        action.accept(entry);
        return 1;
    }
}
