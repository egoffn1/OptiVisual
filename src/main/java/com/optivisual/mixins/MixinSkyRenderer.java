package com.optivisual.mixins;

import com.optivisual.config.ConfigManager;
import com.optivisual.config.ConfigData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.SkyRendering;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRendering.class)
public class MixinSkyRenderer {

    @Inject(method = "renderTopSky", at = @At("HEAD"))
    private void onRenderTopSky(float red, float green, float blue, CallbackInfo ci) {
        if (MinecraftClient.getInstance().world == null) return;
        ConfigData config = ConfigManager.getConfig();
        if (config == null) return;
    }

    @Inject(method = "renderSkyDark", at = @At("HEAD"))
    private void onRenderSkyDark(CallbackInfo ci) {
        if (MinecraftClient.getInstance().world == null) return;
        ConfigData config = ConfigManager.getConfig();
        if (config == null) return;
    }
}
