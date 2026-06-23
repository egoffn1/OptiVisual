package com.optivisual.mixins;

import com.optivisual.config.ConfigManager;
import com.optivisual.config.ConfigData;
import com.optivisual.util.ModCompat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.world.ClientWorld;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer {

    @Unique
    private static ConfigData lastConfig = null;
    @Unique
    private static Fog cachedFog = null;
    @Unique
    private static Vector4f cachedFogColor = null;
    @Unique
    private static int cachedFogFlags = 0;
    @Unique
    private static int cachedColorFlags = 0;

    @Unique
    private static float clamp(float v) {
        return Math.max(0.0f, Math.min(1.0f, v));
    }

    @Inject(method = "applyFog", at = @At("RETURN"), cancellable = true)
    private static void onApplyFog(Camera camera, BackgroundRenderer.FogType fogType, Vector4f color, float viewDistance, boolean thickFog, float tickDelta, CallbackInfoReturnable<Fog> cir) {
        if (ModCompat.DISABLED_FOG) return;
        if (MinecraftClient.getInstance().world == null) return;
        ConfigData config = ConfigManager.getConfig();
        if (config == null || !config.customFog) return;

        int flags = Float.floatToIntBits(config.fogDistance) ^ Float.floatToIntBits(config.fogDensity) ^
                    (config.fogColorBoost ? 1 : 0) ^ Float.floatToIntBits(config.brightness);
        if (config == lastConfig && cachedFog != null && flags == cachedFogFlags) {
            cir.setReturnValue(cachedFog);
            return;
        }

        Fog original = cir.getReturnValue();

        float start = original.start() * config.fogDistance;
        float end = original.end() * config.fogDistance * config.fogDensity;

        start = Math.max(0.0f, start);
        end = Math.max(start + 0.1f, end);

        float bright = config.fogColorBoost ? config.brightness : 1.0f;

        cachedFog = new Fog(
            start, end, original.shape(),
            clamp(original.red() * bright),
            clamp(original.green() * bright),
            clamp(original.blue() * bright),
            clamp(original.alpha())
        );
        cachedFogFlags = flags;
        lastConfig = config;
        cir.setReturnValue(cachedFog);
    }

    @Inject(method = "getFogColor", at = @At("RETURN"), cancellable = true)
    private static void onGetFogColor(Camera camera, float tickDelta, ClientWorld world, int skyColor, float rainGradient, CallbackInfoReturnable<Vector4f> cir) {
        if (ModCompat.DISABLED_FOG) return;
        if (MinecraftClient.getInstance().world == null) return;
        ConfigData config = ConfigManager.getConfig();
        if (config == null || !config.customFog) return;

        int flags = (config.fogColorBoost ? 1 : 0) ^ Float.floatToIntBits(config.brightness);
        if (config == lastConfig && cachedFogColor != null && flags == cachedColorFlags) {
            cir.setReturnValue(cachedFogColor);
            return;
        }

        Vector4f original = cir.getReturnValue();
        float bright = config.fogColorBoost ? config.brightness : 1.0f;
        cachedFogColor = new Vector4f(
            clamp(original.x * bright),
            clamp(original.y * bright),
            clamp(original.z * bright),
            clamp(original.w)
        );
        cachedColorFlags = flags;
        lastConfig = config;
        cir.setReturnValue(cachedFogColor);
    }
}
