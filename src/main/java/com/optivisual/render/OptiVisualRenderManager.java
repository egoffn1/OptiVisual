package com.optivisual.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;

public class OptiVisualRenderManager {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    private static double camX, camY, camZ;
    private static double dirX, dirY, dirZ;
    private static boolean cameraReady = false;

    private static float lastYaw = Float.NaN;
    private static float lastPitch = Float.NaN;

    // Chunk culling
    public static boolean cullBehind = true;
    public static double cullMaxDistSq = (32 * 16) * (32 * 16);

    // Entity culling
    public static boolean cullEntityBehind = true;
    public static double maxEntityDistSq = 32 * 32;
    public static int renderedEntityCount = 0;

    public static void updateCameraFromClient(MinecraftClient mc) {
        if (mc == null || mc.gameRenderer == null) return;
        Camera camera = mc.gameRenderer.getCamera();
        if (camera == null) return;
        updateCamera(camera);
    }

    public static void updateCamera(Camera camera) {
        if (camera == null) return;
        camX = camera.getPos().x;
        camY = camera.getPos().y;
        camZ = camera.getPos().z;

        float yaw = camera.getYaw();
        float pitch = camera.getPitch();

        if (yaw == lastYaw && pitch == lastPitch && cameraReady) return;

        lastYaw = yaw;
        lastPitch = pitch;

        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);
        double cosPitch = Math.cos(pitchRad);
        dirX = -Math.sin(yawRad) * cosPitch;
        dirY = -Math.sin(pitchRad);
        dirZ = Math.cos(yawRad) * cosPitch;
        cameraReady = true;
    }

    // ==================== Chunk culling ====================

    public static boolean isSectionBehindCamera(int sectionX, int sectionZ) {
        if (!cameraReady) return false;

        double blockCenterX = sectionX * 16 + 8;
        double blockCenterZ = sectionZ * 16 + 8;

        double dx = blockCenterX - camX;
        double dz = blockCenterZ - camZ;

        return (dx * dirX + dz * dirZ) < -8.0;
    }

    public static double getSectionDistanceSq(int sectionX, int sectionZ) {
        double dx = (sectionX * 16 + 8) - camX;
        double dz = (sectionZ * 16 + 8) - camZ;
        return dx * dx + dz * dz;
    }

    // ==================== Entity culling ====================

    public static boolean isEntityBehindCamera(Entity entity) {
        if (!cameraReady) return false;
        double dx = entity.getX() - camX;
        double dz = entity.getZ() - camZ;
        return (dx * dirX + dz * dirZ) < -2.0;
    }

    public static double getEntityDistanceSq(Entity entity) {
        double dx = entity.getX() - camX;
        double dz = entity.getZ() - camZ;
        return dx * dx + dz * dz;
    }

    public static double getCamX() { return camX; }
    public static double getCamZ() { return camZ; }
}
