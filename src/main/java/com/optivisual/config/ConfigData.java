package com.optivisual.config;

public class ConfigData {
    public float brightness = 1.0f;
    public float contrast = 1.0f;
    public float saturation = 1.0f;
    public float gamma = 1.0f;
    public float fogDistance = 0.7f;
    public float fogDensity = 1.0f;
    public boolean customFog = false;
    public boolean customSky = false;
    public float skyBrightness = 1.0f;
    public String preset = "balanced";
    public boolean autoOptimize = true;
    public int targetFPS = 150;
    public float renderDistanceScale = 1.0f;
    public boolean smoothLighting = true;
    public boolean showFps = false;
    public boolean showChunkRenderTime = false;
    public boolean showEntityCount = false;
    public boolean fogColorBoost = false;

    public boolean smartCulling = true;
    public boolean behindCulling = true;
    public int lodDistance = 64;
    public boolean dynamicRenderDistance = true;
    public boolean dynamicFogDistance = true;
    public int minRenderDistance = 2;
    public int maxRenderDistance = 12;

    // Entity culling
    public boolean entityCullingEnabled = true;
    public boolean entityBehindCulling = true;
    public int entityMaxRenderDistance = 32;
    public boolean dynamicEntityQuality = true;
    public boolean entityLodEnabled = true;
    public int entityLodThreshold = 16;

    // Lazy HUD
    public boolean lazyHud = true;
}
