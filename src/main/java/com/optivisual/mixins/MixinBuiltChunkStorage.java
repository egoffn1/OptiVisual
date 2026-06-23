package com.optivisual.mixins;

import com.optivisual.config.ConfigData;
import com.optivisual.config.ConfigManager;
import com.optivisual.render.OptiVisualRenderManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BuiltChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BuiltChunkStorage.class)
public class MixinBuiltChunkStorage {

    @Inject(method = "isSectionWithinViewDistance", at = @At("RETURN"), cancellable = true)
    private void onIsSectionWithinViewDistance(int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) return;
        if (MinecraftClient.getInstance().world == null) return;

        ConfigData config = ConfigManager.getConfig();
        if (config == null) return;

        if (config.behindCulling && OptiVisualRenderManager.isSectionBehindCamera(x, z)) {
            cir.setReturnValue(false);
            return;
        }

        if (config.smartCulling) {
            double distSq = OptiVisualRenderManager.getSectionDistanceSq(x, z);
            double maxDist = config.maxRenderDistance * 16;
            if (distSq > maxDist * maxDist) {
                cir.setReturnValue(false);
            }
        }
    }
}
