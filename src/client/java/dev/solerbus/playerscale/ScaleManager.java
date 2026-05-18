package dev.solerbus.playerscale;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ScaleManager {
    private static final Map<UUID, Float> SCALES = new ConcurrentHashMap<>();
    private static final Map<Integer, UUID> ID_TO_UUID = new ConcurrentHashMap<>();

    private static float selfScale = 1.0f;
    private static float othersScale = 1.0f;
    private static boolean showCrosshairInThirdPerson = false;
    private static int localPlayerEntityId = -1;

    public static void setSelfScale(float scale) {
        selfScale = scale;
    }

    public static float getSelfScale() {
        return selfScale;
    }

    public static void setOthersScale(float scale) {
        othersScale = scale;
    }

    public static float getOthersScale() {
        return othersScale;
    }

    public static boolean isShowCrosshairInThirdPerson() {
        return showCrosshairInThirdPerson;
    }

    public static void setShowCrosshairInThirdPerson(boolean value) {
        showCrosshairInThirdPerson = value;
    }

    public static void setLocalPlayerEntityId(int id) {
        localPlayerEntityId = id;
    }

    public static void setScale(UUID uuid, float scale) {
        if (scale == 1.0f) {
            SCALES.remove(uuid);
        } else {
            SCALES.put(uuid, scale);
        }
    }

    public static float getScale(UUID uuid) {
        return SCALES.getOrDefault(uuid, 1.0f);
    }

    public static void resetScale(UUID uuid) {
        SCALES.remove(uuid);
    }

    public static void resetAll() {
        SCALES.clear();
        ID_TO_UUID.clear();
        selfScale = 1.0f;
        othersScale = 1.0f;
        showCrosshairInThirdPerson = false;
    }

    public static void mapEntityId(int entityId, UUID uuid) {
        ID_TO_UUID.put(entityId, uuid);
    }

    public static Map<UUID, Float> getPlayerScales() {
        return Collections.unmodifiableMap(SCALES);
    }

    public static void loadPlayerScales(Map<UUID, Float> scales) {
        SCALES.clear();
        SCALES.putAll(scales);
    }

    public static float getScaleByEntityId(int entityId) {
        if (entityId == localPlayerEntityId) {
            return selfScale;
        }

        UUID uuid = ID_TO_UUID.get(entityId);
        if (uuid != null) {
            Float perPlayer = SCALES.get(uuid);
            if (perPlayer != null) return perPlayer;
        }

        return othersScale;
    }
}
