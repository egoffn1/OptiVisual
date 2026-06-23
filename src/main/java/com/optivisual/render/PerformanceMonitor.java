package com.optivisual.render;

import com.optivisual.config.ConfigData;
import com.optivisual.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceMonitor {
    private static final Logger LOGGER = LoggerFactory.getLogger("OptiVisualPerf");
    private static final MinecraftClient client = MinecraftClient.getInstance();

    private static float currentFPS = 60.0f;
    private static float smoothFPS = 60.0f;
    private static int fpsCounter = 0;
    private static int fpsFrames = 0;
    private static int adjustmentCooldown = 0;
    private static float lastChunkRenderTimeMs = 0.0f;

    private static boolean active = false;
    private static int targetFPS = 60;
    private static int minDist = 4;
    private static int maxDist = 16;

    public static void refreshConfig() {
        ConfigData cfg = ConfigManager.getConfig();
        if (cfg != null) {
            active = cfg.autoOptimize && cfg.dynamicRenderDistance;
            targetFPS = cfg.targetFPS;
            minDist = cfg.minRenderDistance;
            maxDist = cfg.maxRenderDistance;
        } else {
            active = false;
        }

        OptiVisualRenderManager.cullBehind = cfg != null && cfg.behindCulling;
        double maxBlockDist = (cfg != null ? cfg.maxRenderDistance : 32) * 16;
        OptiVisualRenderManager.cullMaxDistSq = maxBlockDist * maxBlockDist;
    }

    public static void tick() {
        if (client.world == null) return;
        if (!active) return;

        fpsCounter++;
        fpsFrames++;

        if (fpsFrames >= 50) {
            float elapsedSeconds = fpsFrames / 60.0f;
            float prevFPS = currentFPS;
            currentFPS = fpsCounter / elapsedSeconds;
            smoothFPS = smoothFPS * 0.9f + currentFPS * 0.1f;
            fpsCounter = 0;
            fpsFrames = 0;

            if (adjustmentCooldown > 0) {
                adjustmentCooldown--;
                return;
            }

            if (Math.abs(prevFPS - currentFPS) > 0.5f) {
                adjustRenderDistance();
            }
        }
    }

    private static void adjustRenderDistance() {
        int currentDistance = client.options.getViewDistance().getValue();

        if (smoothFPS < targetFPS * 0.7f && currentDistance > minDist) {
            int newDist = Math.max(currentDistance - 2, minDist);
            client.options.getViewDistance().setValue(newDist);
            LOGGER.info("FPS {} < {}, дистанция: {} → {}",
                (int) smoothFPS, targetFPS, currentDistance, newDist);
            adjustmentCooldown = 5;
        } else if (smoothFPS > targetFPS * 1.3f && currentDistance < maxDist) {
            int newDist = Math.min(currentDistance + 1, maxDist);
            client.options.getViewDistance().setValue(newDist);
            LOGGER.info("FPS {} > {}, дистанция: {} → {}",
                (int) smoothFPS, targetFPS, currentDistance, newDist);
            adjustmentCooldown = 5;
        }
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
}
