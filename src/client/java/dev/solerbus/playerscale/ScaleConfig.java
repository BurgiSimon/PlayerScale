package dev.solerbus.playerscale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ScaleConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("PlayerScale");
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("playerscale.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) return;
        try {
            String json = Files.readString(CONFIG_PATH);
            ConfigData data = GSON.fromJson(json, ConfigData.class);
            if (data == null) return;
            ScaleManager.setSelfScale(data.selfScale);
            ScaleManager.setOthersScale(data.othersScale);
            ScaleManager.setShowCrosshairInThirdPerson(data.showCrosshairInThirdPerson);
            if (data.playerScales != null) {
                Map<UUID, Float> scales = new HashMap<>();
                data.playerScales.forEach((key, value) -> {
                    try {
                        scales.put(UUID.fromString(key), value);
                    } catch (IllegalArgumentException ignored) {
                    }
                });
                ScaleManager.loadPlayerScales(scales);
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to load config", e);
        }
    }

    public static void save() {
        ConfigData data = new ConfigData();
        data.selfScale = ScaleManager.getSelfScale();
        data.othersScale = ScaleManager.getOthersScale();
        data.showCrosshairInThirdPerson = ScaleManager.isShowCrosshairInThirdPerson();
        data.playerScales = new HashMap<>();
        ScaleManager.getPlayerScales().forEach((uuid, scale) ->
                data.playerScales.put(uuid.toString(), scale));
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(data));
        } catch (IOException e) {
            LOGGER.warn("Failed to save config", e);
        }
    }

    private static class ConfigData {
        float selfScale = 1.0f;
        float othersScale = 1.0f;
        boolean showCrosshairInThirdPerson = false;
        Map<String, Float> playerScales = new HashMap<>();
    }
}
