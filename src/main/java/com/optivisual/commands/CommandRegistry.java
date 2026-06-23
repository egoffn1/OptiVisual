package com.optivisual.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.optivisual.config.ConfigManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class CommandRegistry {
    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("optivisual")
                .then(literal("reload")
                    .executes(ctx -> {
                        ConfigManager.load();
                        ctx.getSource().sendFeedback(Text.literal("§a[OptiVisual] Конфиг перезагружен"));
                        return 1;
                    })
                )
                .then(literal("save")
                    .executes(ctx -> {
                        ConfigManager.save();
                        ctx.getSource().sendFeedback(Text.literal("§a[OptiVisual] Конфиг сохранён"));
                        return 1;
                    })
                )
                .then(literal("preset")
                    .then(argument("name", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            builder.suggest("low");
                            builder.suggest("mid");
                            builder.suggest("high");
                            builder.suggest("ultra");
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            String preset = StringArgumentType.getString(ctx, "name");
                            ConfigManager.applyPreset(preset);
                            ctx.getSource().sendFeedback(
                                Text.literal("§a[OptiVisual] Применён пресет: §e" + preset)
                            );
                            return 1;
                        })
                    )
                )
                .then(literal("status")
                    .executes(ctx -> {
                        var config = ConfigManager.getConfig();
                        ctx.getSource().sendFeedback(
                            Text.literal("§e[OptiVisual] Статус:")
                                .copy().append("\n§7 Пресет: §f" + config.preset)
                                .append("\n§7 Яркость: §f" + config.brightness)
                                .append("\n§7 Контраст: §f" + config.contrast)
                                .append("\n§7 Туман: §f" + config.fogDistance)
                                .append("\n§7 Авто-оптимизация: §f" + config.autoOptimize)
                        );
                        return 1;
                    })
                )
                .then(literal("auto")
                    .then(literal("on")
                        .executes(ctx -> {
                            ConfigManager.getConfig().autoOptimize = true;
                            ConfigManager.save();
                            ctx.getSource().sendFeedback(Text.literal("§a[OptiVisual] Авто-оптимизация включена"));
                            return 1;
                        })
                    )
                    .then(literal("off")
                        .executes(ctx -> {
                            ConfigManager.getConfig().autoOptimize = false;
                            ConfigManager.save();
                            ctx.getSource().sendFeedback(Text.literal("§c[OptiVisual] Авто-оптимизация выключена"));
                            return 1;
                        })
                    )
                )
            );
        });
    }
}
