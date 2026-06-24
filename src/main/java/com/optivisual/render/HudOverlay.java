package com.optivisual.render;

import com.optivisual.config.ConfigData;
import com.optivisual.config.ConfigManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class HudOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        ConfigData config = ConfigManager.getConfig();
        if (config == null) return;

        TextRenderer textRenderer = client.textRenderer;
        int y = 4;

        if (config.showFps) {
            float fps = PerformanceMonitor.getSmoothFPS();
            String fpsText = String.format("FPS: %.0f", fps);
            int color = fps >= 60 ? 0xFF55FF55 : fps >= 30 ? 0xFFFFFF55 : 0xFFFF5555;
            drawContext.drawText(textRenderer, fpsText, 4, y, color, true);
            y += 10;
        }

        if (config.showChunkRenderTime) {
            float ms = PerformanceMonitor.getLastChunkRenderTimeMs();
            String chunkText = String.format("Чанки: %.1f ms", ms);
            int color = ms < 5.0f ? 0xFF55FF55 : ms < 15.0f ? 0xFFFFFF55 : 0xFFFF5555;
            drawContext.drawText(textRenderer, chunkText, 4, y, color, true);
            y += 10;
        }

        if (config.showEntityCount) {
            int count = PerformanceMonitor.getEntityCount();
            String text = String.format("Сущности: %d", count);
            drawContext.drawText(textRenderer, text, 4, y, 0xFF55AAFF, true);
            y += 10;
        }
    }
}
