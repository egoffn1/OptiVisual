package com.optivisual.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("OptiVisualConfig");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("optivisual.json");
    private static ConfigData config;

    public static void init() {
        load();
        save();
    }

    public static ConfigData getConfig() {
        return config;
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                config = GSON.fromJson(reader, ConfigData.class);
            } catch (IOException e) {
                LOGGER.error("Ошибка загрузки конфига", e);
            }
        }
        if (config == null) {
            config = new ConfigData();
        }
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            LOGGER.error("Ошибка сохранения конфига", e);
        }
    }

    public static void applyPreset(String name) {
        switch (name.toLowerCase()) {
            case "low" -> {
                config.brightness = 0.85f;
                config.contrast = 0.9f;
                config.saturation = 0.8f;
                config.gamma = 0.8f;
                config.fogDistance = 0.15f;
                config.fogDensity = 1.5f;
                config.customFog = true;
                config.renderDistanceScale = 0.3f;
                config.smoothLighting = false;
                config.fogColorBoost = false;
                config.smartCulling = true;
                config.behindCulling = true;
                config.lodDistance = 16;
                config.dynamicRenderDistance = true;
                config.dynamicFogDistance = true;
                config.minRenderDistance = 2;
                config.maxRenderDistance = 6;
                config.entityCullingEnabled = true;
                config.entityBehindCulling = true;
                config.entityMaxRenderDistance = 16;
                config.dynamicEntityQuality = true;
                config.targetFPS = 150;
                config.autoOptimize = true;
            }
            case "mid" -> {
                config.brightness = 1.0f;
                config.contrast = 1.0f;
                config.saturation = 1.0f;
                config.gamma = 1.0f;
                config.fogDistance = 0.5f;
                config.fogDensity = 1.0f;
                config.customFog = true;
                config.renderDistanceScale = 0.6f;
                config.smoothLighting = true;
                config.fogColorBoost = false;
                config.smartCulling = true;
                config.behindCulling = true;
                config.lodDistance = 32;
                config.dynamicRenderDistance = true;
                config.dynamicFogDistance = true;
                config.minRenderDistance = 4;
                config.maxRenderDistance = 10;
                config.entityCullingEnabled = true;
                config.entityBehindCulling = true;
                config.entityMaxRenderDistance = 24;
                config.dynamicEntityQuality = true;
                config.targetFPS = 120;
                config.autoOptimize = true;
            }
            case "high" -> {
                config.brightness = 1.0f;
                config.contrast = 1.05f;
                config.saturation = 1.1f;
                config.gamma = 1.05f;
                config.fogDistance = 1.0f;
                config.fogDensity = 0.9f;
                config.customFog = true;
                config.renderDistanceScale = 1.0f;
                config.smoothLighting = true;
                config.fogColorBoost = false;
                config.smartCulling = true;
                config.behindCulling = true;
                config.lodDistance = 64;
                config.dynamicRenderDistance = false;
                config.dynamicFogDistance = false;
                config.minRenderDistance = 8;
                config.maxRenderDistance = 18;
                config.entityCullingEnabled = true;
                config.entityBehindCulling = false;
                config.entityMaxRenderDistance = 48;
                config.dynamicEntityQuality = false;
                config.targetFPS = 60;
                config.autoOptimize = false;
            }
            case "ultra" -> {
                config.brightness = 1.0f;
                config.contrast = 1.1f;
                config.saturation = 1.2f;
                config.gamma = 1.1f;
                config.fogDistance = 1.5f;
                config.fogDensity = 0.6f;
                config.customFog = true;
                config.renderDistanceScale = 1.0f;
                config.smoothLighting = true;
                config.fogColorBoost = false;
                config.smartCulling = false;
                config.behindCulling = false;
                config.lodDistance = 128;
                config.dynamicRenderDistance = false;
                config.dynamicFogDistance = false;
                config.minRenderDistance = 12;
                config.maxRenderDistance = 32;
                config.entityCullingEnabled = false;
                config.entityBehindCulling = false;
                config.entityMaxRenderDistance = 64;
                config.dynamicEntityQuality = false;
                config.targetFPS = 30;
                config.autoOptimize = false;
            }
            case "custom" -> {
            }
            default -> {
                config.brightness = 1.0f;
                config.contrast = 1.0f;
                config.saturation = 1.0f;
                config.gamma = 1.0f;
                config.fogDistance = 0.7f;
                config.fogDensity = 1.0f;
                config.customFog = false;
                config.renderDistanceScale = 1.0f;
                config.smoothLighting = true;
                config.fogColorBoost = false;
                config.smartCulling = true;
                config.behindCulling = true;
                config.lodDistance = 64;
                config.dynamicRenderDistance = true;
                config.dynamicFogDistance = true;
                config.minRenderDistance = 2;
                config.maxRenderDistance = 12;
                config.entityCullingEnabled = true;
                config.entityBehindCulling = true;
                config.entityMaxRenderDistance = 32;
                config.dynamicEntityQuality = true;
                config.targetFPS = 150;
                config.autoOptimize = true;
            }
        }
        config.preset = name;
        save();
        LOGGER.info("Применён пресет: {}", name);
    }
}
