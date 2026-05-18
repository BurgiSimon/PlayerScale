package dev.solerbus.playerscale;

import dev.solerbus.playerscale.command.PlayerScaleCommand;
import dev.solerbus.playerscale.screen.PlayerScaleScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class PlayerScaleMod implements ClientModInitializer {

    public static KeyBinding openConfigKey;
    public static KeyBinding toggleScaleKey;

    @Override
    public void onInitializeClient() {
        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.playerscale.open_config",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                KeyBinding.Category.create(net.minecraft.util.Identifier.of("playerscale", "keybinds"))
        ));

        toggleScaleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.playerscale.toggle_scale",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                KeyBinding.Category.create(net.minecraft.util.Identifier.of("playerscale", "keybinds"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openConfigKey.wasPressed()) {
                client.setScreen(new PlayerScaleScreen(null));
            }
            while (toggleScaleKey.wasPressed()) {
                ScaleManager.toggleSelfScale();
                ScaleConfig.save();
            }
        });

        ClientCommandRegistrationCallback.EVENT.register(PlayerScaleCommand::register);

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ScaleManager.clearEntityMappings());

        ScaleConfig.load();
    }
}
