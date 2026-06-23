package com.optivisual.mixins;

import com.optivisual.render.OptiVisualRenderManager;
import com.optivisual.util.ModCompat;
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
        if (ModCompat.DISABLED_CULLING) return;
        if (!cir.getReturnValueZ()) return;
        if (MinecraftClient.getInstance().world == null) return;

        if (OptiVisualRenderManager.cullBehind && OptiVisualRenderManager.isSectionBehindCamera(x, z)) {
            cir.setReturnValue(false);
            return;
        }

        double distSq = OptiVisualRenderManager.getSectionDistanceSq(x, z);
        if (distSq > OptiVisualRenderManager.cullMaxDistSq) {
            cir.setReturnValue(false);
        }
    }
}
