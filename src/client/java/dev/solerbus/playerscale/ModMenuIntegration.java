package dev.solerbus.playerscale;

import dev.solerbus.playerscale.screen.PlayerScaleScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return PlayerScaleScreen::new;
    }
}
