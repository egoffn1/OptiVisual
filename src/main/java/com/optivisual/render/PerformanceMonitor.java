package com.optivisual.render;

import com.optivisual.config.ConfigData;
import com.optivisual.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceMonitor {
    private static final Logger LOGGER = LoggerFactory.getLogger("OptiVisualPerf");
    private static final MinecraftClient client = MinecraftClient.getInstance();

    private static float smoothFPS = 60.0f;
    private static int adjustmentCooldown = 0;
    private static float lastChunkRenderTimeMs = 0.0f;

    private static boolean active = false;
    private static int targetFPS = 150;
    private static int minDist = 2;
    private static int maxDist = 12;

    private static float dynamicFogScale = 1.0f;
    private static boolean dynamicFogEnabled = false;

    private static int entityCount = 0;
    private static boolean dynamicEntityQuality = false;

    public static void refreshConfig() {
        ConfigData cfg = ConfigManager.getConfig();
        if (cfg != null) {
            active = cfg.autoOptimize;
            targetFPS = Math.max(30, cfg.targetFPS);
            minDist = cfg.minRenderDistance;
            maxDist = cfg.maxRenderDistance;
            dynamicFogEnabled = cfg.dynamicFogDistance;
            dynamicEntityQuality = cfg.dynamicEntityQuality;

            OptiVisualRenderManager.cullEntityBehind = cfg.entityCullingEnabled && cfg.entityBehindCulling;
            double maxEntityDist = cfg.entityCullingEnabled ? cfg.entityMaxRenderDistance : 256;
            OptiVisualRenderManager.maxEntityDistSq = maxEntityDist * maxEntityDist;
        } else {
            active = false;
        }

        OptiVisualRenderManager.cullBehind = cfg != null && cfg.behindCulling;
        double maxBlockDist = (cfg != null ? cfg.maxRenderDistance : 32) * 16;
        OptiVisualRenderManager.cullMaxDistSq = maxBlockDist * maxBlockDist;
    }

    public static void tick() {
        if (client.world == null) return;

        if (client.world != null && (client.getCurrentFps() % 20 == 0)) {
            entityCount = client.world.getRegularEntityCount();
        }

        if (!active) return;

        float fps;
        try {
            fps = client.getCurrentFps();
        } catch (Exception e) {
            fps = smoothFPS;
        }

        smoothFPS = smoothFPS * 0.9f + fps * 0.1f;

        if (adjustmentCooldown > 0) {
            adjustmentCooldown--;
            return;
        }

        boolean changed = false;
        changed |= adjustRenderDistance();
        changed |= adjustFogDistance();
        changed |= adjustEntityQuality();
        if (changed) adjustmentCooldown = 3;
    }

    private static boolean adjustRenderDistance() {
        int currentDistance = client.options.getViewDistance().getValue();

        if (smoothFPS < targetFPS * 0.75f && currentDistance > minDist) {
            int newDist = Math.max(currentDistance - 2, minDist);
            client.options.getViewDistance().setValue(newDist);
            LOGGER.info("FPS {} < {}, дист: {} → {}",
                (int) smoothFPS, targetFPS, currentDistance, newDist);
            return true;
        } else if (smoothFPS > targetFPS * 1.2f && currentDistance < maxDist) {
            int newDist = Math.min(currentDistance + 1, maxDist);
            client.options.getViewDistance().setValue(newDist);
            LOGGER.info("FPS {} > {}, дист: {} → {}",
                (int) smoothFPS, targetFPS, currentDistance, newDist);
            return true;
        }
        return false;
    }

    private static boolean adjustFogDistance() {
        if (!dynamicFogEnabled) {
            if (dynamicFogScale < 1.0f) {
                dynamicFogScale = Math.min(1.0f, dynamicFogScale + 0.1f);
                return true;
            }
            return false;
        }

        if (smoothFPS < targetFPS * 0.75f && dynamicFogScale > 0.15f) {
            float prev = dynamicFogScale;
            dynamicFogScale = Math.max(0.15f, dynamicFogScale - 0.08f);
            LOGGER.info("FPS {} < {}, туман: {:.2f} → {:.2f}",
                (int) smoothFPS, targetFPS, prev, dynamicFogScale);
            return true;
        } else if (smoothFPS > targetFPS * 1.2f && dynamicFogScale < 1.0f) {
            float prev = dynamicFogScale;
            dynamicFogScale = Math.min(1.0f, dynamicFogScale + 0.05f);
            LOGGER.info("FPS {} > {}, туман: {:.2f} → {:.2f}",
                (int) smoothFPS, targetFPS, prev, dynamicFogScale);
            return true;
        }
        return false;
    }

    private static boolean adjustEntityQuality() {
        if (!dynamicEntityQuality) return false;

        if (smoothFPS < targetFPS * 0.75f && entityCount > 20) {
            double maxEntityDist = Math.max(8, OptiVisualRenderManager.maxEntityDistSq > 0
                ? Math.sqrt(OptiVisualRenderManager.maxEntityDistSq) - 4
                : 32);
            OptiVisualRenderManager.maxEntityDistSq = maxEntityDist * maxEntityDist;
            LOGGER.info("FPS {} < {}, сущностей: {}, дист сущностей снижена",
                (int) smoothFPS, targetFPS, entityCount);
            return true;
        } else if (smoothFPS > targetFPS * 1.2f) {
            OptiVisualRenderManager.maxEntityDistSq = 32 * 32;
            return true;
        }
        return false;
    }

    public static void resetDynamicFog() {
        dynamicFogScale = 1.0f;
    }

    public static float getDynamicFogScale() {
        return active && dynamicFogEnabled ? dynamicFogScale : 1.0f;
    }

    public static int getEntityCount() {
        return entityCount;
    }

    public static void setLastChunkRenderTimeMs(float ms) {
        lastChunkRenderTimeMs = ms;
    }

    public static float getLastChunkRenderTimeMs() {
        return lastChunkRenderTimeMs;
    }

    public static float getSmoothFPS() {
        return smoothFPS;
    }

    public static int getTargetFPS() {
        return targetFPS;
    }
}
