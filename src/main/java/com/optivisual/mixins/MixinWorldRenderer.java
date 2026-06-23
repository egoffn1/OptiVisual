package com.optivisual.mixins;

import com.optivisual.config.ConfigManager;
import com.optivisual.config.ConfigData;
import com.optivisual.render.OptiVisualRenderManager;
import com.optivisual.render.PerformanceMonitor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {

    @Unique
    private long optiVisual$renderStartTime = 0L;
    @Unique
    private int optiVisual$frameCount = 0;

    @Inject(method = "renderMain", at = @At("HEAD"))
    private void onRenderMainStart(FrameGraphBuilder frameGraphBuilder, Frustum frustum, Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix, Fog fog, boolean renderBlockOutline, boolean renderEntityOutlines, RenderTickCounter renderTickCounter, Profiler profiler, CallbackInfo ci) {
        if (MinecraftClient.getInstance().world == null) return;

        OptiVisualRenderManager.updateCamera(camera);
        PerformanceMonitor.tick();

        ConfigData config = ConfigManager.getConfig();
        if (config != null && config.showChunkRenderTime) {
            optiVisual$renderStartTime = System.nanoTime();
        }
    }

    @Inject(method = "renderMain", at = @At("RETURN"))
    private void onRenderMainEnd(FrameGraphBuilder frameGraphBuilder, Frustum frustum, Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix, Fog fog, boolean renderBlockOutline, boolean renderEntityOutlines, RenderTickCounter renderTickCounter, Profiler profiler, CallbackInfo ci) {
        if (optiVisual$renderStartTime == 0L) return;

        long elapsed = System.nanoTime() - optiVisual$renderStartTime;
        optiVisual$renderStartTime = 0L;
        optiVisual$frameCount++;

        if (optiVisual$frameCount % 30 == 0) {
            PerformanceMonitor.setLastChunkRenderTimeMs(elapsed / 1_000_000.0f);
        }
    }
}
