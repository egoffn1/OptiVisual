package com.optivisual.util;

import com.optivisual.config.ConfigManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HardwareDetector {
    private static final Logger LOGGER = LoggerFactory.getLogger("OptiVisualHW");
    private static String gpuInfo = "unknown";
    private static String gpuVendor = "unknown";
    private static int cpuCores = Runtime.getRuntime().availableProcessors();
    private static boolean detected = false;

    public static void init() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            detectGL();
            if (ConfigManager.getConfig().autoOptimize) {
                applyAutoConfig();
            }
        });
    }

    private static void detectGL() {
        if (detected) return;
        try {
            gpuInfo = org.lwjgl.opengl.GL11.glGetString(org.lwjgl.opengl.GL11.GL_RENDERER);
            gpuVendor = org.lwjgl.opengl.GL11.glGetString(org.lwjgl.opengl.GL11.GL_VENDOR);
            detected = true;
            LOGGER.info("GPU: {} | Vendor: {}", gpuInfo, gpuVendor);
        } catch (Exception e) {
            LOGGER.warn("Не удалось определить GPU: {}", e.getMessage());
            detected = true;
        }
    }

    public static String getGpuInfo() {
        return gpuInfo;
    }

    public static String getGpuVendor() {
        return gpuVendor;
    }

    public static int getCpuCores() {
        return cpuCores;
    }

    public static boolean isLowEndGPU() {
        String gpu = gpuInfo.toLowerCase();
        String vendor = gpuVendor.toLowerCase();

        if (vendor.contains("intel") && !vendor.contains("arc")) return true;
        if (gpu.contains("gt ") || gpu.contains("gtx 9") || gpu.contains("gtx 10")) return true;
        if (gpu.contains("hd graphics") || gpu.contains("uhd graphics")) return true;
        if (gpu.contains("radeon") && (gpu.contains("r5") || gpu.contains("r7")) && !gpu.contains("rx")) return true;

        return false;
    }

    public static boolean isMidRangeGPU() {
        String gpu = gpuInfo.toLowerCase();

        if (gpu.contains("gtx 16") || gpu.contains("rtx 20") || gpu.contains("gtx 1070") || gpu.contains("gtx 1080")) return true;
        if (gpu.contains("rx 5") || gpu.contains("rx 6")) return true;
        if (gpu.contains("arc")) return true;

        return false;
    }

    public static boolean isHighEndGPU() {
        String gpu = gpuInfo.toLowerCase();

        if (gpu.contains("rtx 30") || gpu.contains("rtx 40") || gpu.contains("rtx 50")) return true;
        if (gpu.contains("rx 6") && (gpu.contains("xt") || gpu.contains("6900") || gpu.contains("6800"))) return true;
        if (gpu.contains("rx 7")) return true;

        return false;
    }

    public static String suggestPreset() {
        if (!detected) return "balanced";

        if (isHighEndGPU()) return "ultra";
        if (isMidRangeGPU()) return "high";
        if (isLowEndGPU()) return "low";

        if (cpuCores <= 4) return "low";
        if (cpuCores <= 6) return "mid";

        return "balanced";
    }

    public static void applyAutoConfig() {
        String preset = suggestPreset();
        LOGGER.info("Авто-настройка: выбран пресет {}", preset);
        ConfigManager.applyPreset(preset);
    }
}
