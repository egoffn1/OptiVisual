package com.optivisual.render;

import com.optivisual.config.ConfigData;
import com.optivisual.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class OptiVisualRenderManager {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static Vec3d lastCameraPos = Vec3d.ZERO;
    private static Vec3d cameraDirection = new Vec3d(0, 0, -1);
    private static boolean cameraReady = false;

    public static void updateCamera(Camera camera) {
        if (camera == null) return;
        lastCameraPos = camera.getPos();
        float yaw = camera.getYaw();
        float pitch = camera.getPitch();
        double dirX = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
        double dirY = -Math.sin(Math.toRadians(pitch));
        double dirZ = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
        cameraDirection = new Vec3d(dirX, dirY, dirZ);
        cameraReady = true;
    }

    public static Vec3d getCameraPos() {
        return lastCameraPos;
    }

    public static Vec3d getCameraDirection() {
        return cameraDirection;
    }

    public static boolean isSectionBehindCamera(int sectionX, int sectionZ) {
        if (!cameraReady) return false;

        double blockCenterX = sectionX * 16 + 8;
        double blockCenterZ = sectionZ * 16 + 8;

        double dx = blockCenterX - lastCameraPos.x;
        double dz = blockCenterZ - lastCameraPos.z;

        double dot = dx * cameraDirection.x + dz * cameraDirection.z;
        return dot < -8.0;
    }

    public static double getSectionDistanceSq(int sectionX, int sectionZ) {
        double dx = (sectionX * 16 + 8) - lastCameraPos.x;
        double dz = (sectionZ * 16 + 8) - lastCameraPos.z;
        return dx * dx + dz * dz;
    }
}
