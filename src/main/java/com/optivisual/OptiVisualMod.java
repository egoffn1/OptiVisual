package com.optivisual;

import com.optivisual.commands.CommandRegistry;
import com.optivisual.config.ConfigManager;
import com.optivisual.config.ConfigScreen;
import com.optivisual.render.HudOverlay;
import com.optivisual.util.HardwareDetector;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OptiVisualMod implements ClientModInitializer {
    public static final String MOD_ID = "optivisual";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyBinding openConfigKey;

    @Override
    public void onInitializeClient() {
        LOGGER.info("OptiVisualMod загружен!");

        ConfigManager.init();
        CommandRegistry.registerCommands();
        HardwareDetector.init();

        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.optivisual.open_config",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.optivisual"
        ));

        HudRenderCallback.EVENT.register(new HudOverlay());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openConfigKey.wasPressed() && client.currentScreen == null) {
                client.setScreen(ConfigScreen.create(client.currentScreen));
            }
        });
    }
}
