package com.optivisual.mixins;

import com.optivisual.config.ConfigData;
import com.optivisual.config.ConfigManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer {

    @Unique
    private static float optiVisual$lodMinScale = 0.25f;

    @Inject(method = "render", at = @At("HEAD"), require = 0)
    private void optiVisual$onRenderHead(LivingEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        ConfigData config = ConfigManager.getConfig();
        if (config == null || !config.entityLodEnabled) return;

        if (state.squaredDistanceToCamera <= 0) return;

        int threshold = config.entityLodThreshold;
        int maxDist = config.entityMaxRenderDistance;
        if (threshold >= maxDist) return;

        double dist = Math.sqrt(state.squaredDistanceToCamera);
        if (dist <= threshold) return;

        float lodScale = (float)(maxDist - dist) / (float)(maxDist - threshold);
        state.baseScale *= Math.max(optiVisual$lodMinScale, lodScale);

        if (dist > threshold * 1.5) {
            state.limbSwingAmplitude = 0;
            state.limbSwingAnimationProgress = 0;
        }
    }
}
