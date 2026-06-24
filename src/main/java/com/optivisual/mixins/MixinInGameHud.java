package com.optivisual.mixins;

import com.optivisual.config.ConfigData;
import com.optivisual.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Unique
    private int optiVisual$lazyTicker = 0;
    @Unique
    private float optiVisual$lastHealth = -1;
    @Unique
    private int optiVisual$lastFood = -1;
    @Unique
    private int optiVisual$lastArmor = -1;
    @Unique
    private int optiVisual$lastXpLevel = -1;
    @Unique
    private int optiVisual$healthCooldown = 0;
    @Unique
    private int optiVisual$foodCooldown = 0;
    @Unique
    private int optiVisual$armorCooldown = 0;
    @Unique
    private int optiVisual$xpCooldown = 0;

    @Inject(method = "render", at = @At("HEAD"), require = 0)
    private void optiVisual$onRenderHead(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        optiVisual$lazyTicker++;

        ConfigData config = ConfigManager.getConfig();
        if (config == null || !config.lazyHud) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        float health = client.player.getHealth();
        if (health != optiVisual$lastHealth) {
            optiVisual$lastHealth = health;
            optiVisual$healthCooldown = 20;
        } else if (optiVisual$healthCooldown > 0) {
            optiVisual$healthCooldown--;
        }

        int food = client.player.getHungerManager().getFoodLevel();
        if (food != optiVisual$lastFood) {
            optiVisual$lastFood = food;
            optiVisual$foodCooldown = 15;
        } else if (optiVisual$foodCooldown > 0) {
            optiVisual$foodCooldown--;
        }

        int armor = client.player.getArmor();
        if (armor != optiVisual$lastArmor) {
            optiVisual$lastArmor = armor;
            optiVisual$armorCooldown = 15;
        } else if (optiVisual$armorCooldown > 0) {
            optiVisual$armorCooldown--;
        }

        int xpLevel = client.player.experienceLevel;
        if (xpLevel != optiVisual$lastXpLevel) {
            optiVisual$lastXpLevel = xpLevel;
            optiVisual$xpCooldown = 10;
        } else if (optiVisual$xpCooldown > 0) {
            optiVisual$xpCooldown--;
        }
    }

    @Inject(method = "renderHealthBar", at = @At("HEAD"), cancellable = true, require = 0)
    private void optiVisual$onRenderHealthBar(DrawContext ctx, PlayerEntity player, int x, int y, int width, int height, float health, int regen, int food, int armor, boolean frozen, CallbackInfo ci) {
        ConfigData config = ConfigManager.getConfig();
        if (config != null && config.lazyHud && optiVisual$healthCooldown == 0 && optiVisual$lazyTicker % 3 != 0) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true, require = 0)
    private void optiVisual$onRenderFood(DrawContext ctx, PlayerEntity player, int x, int y, CallbackInfo ci) {
        ConfigData config = ConfigManager.getConfig();
        if (config != null && config.lazyHud && optiVisual$foodCooldown == 0 && optiVisual$lazyTicker % 3 != 1) {
            ci.cancel();
        }
    }

    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true, require = 0)
    private void optiVisual$onRenderArmor(DrawContext ctx, PlayerEntity player, int x, int y, int width, int height, CallbackInfo ci) {
        ConfigData config = ConfigManager.getConfig();
        if (config != null && config.lazyHud && optiVisual$armorCooldown == 0 && optiVisual$lazyTicker % 3 != 0) {
            ci.cancel();
        }
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true, require = 0)
    private void optiVisual$onRenderExperienceBar(DrawContext ctx, int x, CallbackInfo ci) {
        ConfigData config = ConfigManager.getConfig();
        if (config != null && config.lazyHud && optiVisual$xpCooldown == 0 && optiVisual$lazyTicker % 5 != 0) {
            ci.cancel();
        }
    }
}
