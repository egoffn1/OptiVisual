package com.optivisual.mixins;

import com.optivisual.render.OptiVisualRenderManager;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher {

    @Inject(method = "shouldRender", at = @At("RETURN"), cancellable = true, require = 0)
    private void onShouldRender(Entity entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) return;

        if (OptiVisualRenderManager.cullEntityBehind && OptiVisualRenderManager.isEntityBehindCamera(entity)) {
            cir.setReturnValue(false);
            return;
        }

        double distSq = OptiVisualRenderManager.getEntityDistanceSq(entity);
        if (distSq > OptiVisualRenderManager.maxEntityDistSq) {
            cir.setReturnValue(false);
        }
    }
}
