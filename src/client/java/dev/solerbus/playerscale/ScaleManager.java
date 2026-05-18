package dev.solerbus.playerscale;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ScaleManager {
    private static final Map<UUID, Float> SCALES = new ConcurrentHashMap<>();
    private static final Map<Integer, UUID> ID_TO_UUID = new ConcurrentHashMap<>();
    private static final Map<Integer, Float> DISPLAY_SCALES = new ConcurrentHashMap<>();

    private static volatile float selfScale = 1.0f;
    private static volatile float othersScale = 1.0f;
    private static volatile float lastSelfScale = 1.0f;
    private static volatile boolean showCrosshairInThirdPerson = false;
    private static volatile int localPlayerEntityId = -1;

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

    public static void setLastSelfScale(float scale) {
        lastSelfScale = scale;
    }

    public static float getLastSelfScale() {
        return lastSelfScale;
    }

    public static void toggleSelfScale() {
        if (selfScale == 1.0f) {
            float target = lastSelfScale != 1.0f ? lastSelfScale : 0.5f;
            selfScale = target;
        } else {
            lastSelfScale = selfScale;
            selfScale = 1.0f;
        }
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

    public static synchronized void resetAll() {
        selfScale = 1.0f;
        othersScale = 1.0f;
        lastSelfScale = 1.0f;
        showCrosshairInThirdPerson = false;
        SCALES.clear();
        ID_TO_UUID.clear();
        DISPLAY_SCALES.clear();
    }

    public static void clearEntityMappings() {
        ID_TO_UUID.clear();
        DISPLAY_SCALES.clear();
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

    public static float getDisplayScale(int entityId) {
        float target = getScaleByEntityId(entityId);
        Float current = DISPLAY_SCALES.get(entityId);
        if (current == null || Math.abs(current - target) < 0.001f) {
            DISPLAY_SCALES.put(entityId, target);
            return target;
        }
        float lerped = current + (target - current) * 0.25f;
        DISPLAY_SCALES.put(entityId, lerped);
        return lerped;
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
