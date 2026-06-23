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
    private static long fpsTimer = 0L;
    private static int adjustmentCooldown = 0;
    private static float lastChunkRenderTimeMs = 0.0f;

    public static void tick() {
        if (client.world == null) return;

        ConfigData config = ConfigManager.getConfig();
        if (config == null || !config.autoOptimize || !config.dynamicRenderDistance) return;

        fpsCounter++;
        long now = System.currentTimeMillis();

        if (fpsTimer == 0) {
            fpsTimer = now;
            return;
        }

        long delta = now - fpsTimer;
        if (delta >= 1000) {
            float prevFPS = currentFPS;
            currentFPS = (fpsCounter * 1000.0f) / delta;
            smoothFPS = smoothFPS * 0.9f + currentFPS * 0.1f;
            fpsCounter = 0;
            fpsTimer = now;

            if (adjustmentCooldown > 0) {
                adjustmentCooldown--;
                return;
            }

            if (Math.abs(prevFPS - currentFPS) > 0.5f) {
                adjustRenderDistance(config);
            }
        }
    }

    private static void adjustRenderDistance(ConfigData config) {
        int currentDistance = client.options.getViewDistance().getValue();
        int targetFPS = config.targetFPS;

        if (smoothFPS < targetFPS * 0.7f && currentDistance > config.minRenderDistance) {
            int newDist = Math.max(currentDistance - 2, config.minRenderDistance);
            client.options.getViewDistance().setValue(newDist);
            LOGGER.info("FPS {} < {}, дистанция: {} → {}",
                (int) smoothFPS, targetFPS, currentDistance, newDist);
            adjustmentCooldown = 5;
        } else if (smoothFPS > targetFPS * 1.3f && currentDistance < config.maxRenderDistance) {
            int newDist = Math.min(currentDistance + 1, config.maxRenderDistance);
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

    public static float getCurrentFPS() {
        return currentFPS;
    }
}
