package dev.solerbus.playerscale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
            ScaleManager.setSelfScale(clampScale(data.selfScale));
            ScaleManager.setOthersScale(clampScale(data.othersScale));
            ScaleManager.setLastSelfScale(clampScale(data.lastSelfScale));
            ScaleManager.setShowCrosshairInThirdPerson(data.showCrosshairInThirdPerson);
            if (data.playerScales != null) {
                Map<UUID, Float> scales = new HashMap<>();
                data.playerScales.forEach((key, value) -> {
                    try {
                        scales.put(UUID.fromString(key), clampScale(value));
                    } catch (IllegalArgumentException e) {
                        LOGGER.warn("Ignoring invalid UUID in config: {}", key);
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
        data.lastSelfScale = ScaleManager.getLastSelfScale();
        data.showCrosshairInThirdPerson = ScaleManager.isShowCrosshairInThirdPerson();
        data.playerScales = new HashMap<>();
        ScaleManager.getPlayerScales().forEach((uuid, scale) ->
                data.playerScales.put(uuid.toString(), scale));
        try {
            Path tmp = CONFIG_PATH.resolveSibling(CONFIG_PATH.getFileName() + ".tmp");
            Files.writeString(tmp, GSON.toJson(data));
            Files.move(tmp, CONFIG_PATH, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            LOGGER.warn("Failed to save config", e);
        }
    }

    private static float clampScale(float value) {
        if (Float.isNaN(value) || Float.isInfinite(value)) return 1.0f;
        return Math.max(0.1f, Math.min(10.0f, value));
    }

    private static class ConfigData {
        float selfScale = 1.0f;
        float othersScale = 1.0f;
        float lastSelfScale = 1.0f;
        boolean showCrosshairInThirdPerson = false;
        Map<String, Float> playerScales = new HashMap<>();
    }
}
