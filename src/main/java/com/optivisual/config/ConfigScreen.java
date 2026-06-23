package com.optivisual.config;

import com.optivisual.util.ModCompat;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen {
    public static Screen create(Screen parent) {
        ConfigData config = ConfigManager.getConfig();
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Text.literal("OptiVisual — Настройки"));

        ConfigEntryBuilder e = builder.entryBuilder();

        ConfigCategory visual = builder.getOrCreateCategory(Text.literal("Визуал"));

        visual.addEntry(e.startFloatField(Text.literal("Яркость"), config.brightness)
            .setDefaultValue(1.0f).setMin(0.1f).setMax(2.0f)
            .setTooltip(Text.literal("Общая яркость изображения (0.1–2.0)"))
            .setSaveConsumer(v -> { config.brightness = v; config.preset = "custom"; }).build());

        visual.addEntry(e.startFloatField(Text.literal("Контраст"), config.contrast)
            .setDefaultValue(1.0f).setMin(0.1f).setMax(2.0f)
            .setTooltip(Text.literal("Контрастность изображения"))
            .setSaveConsumer(v -> { config.contrast = v; config.preset = "custom"; }).build());

        visual.addEntry(e.startFloatField(Text.literal("Насыщенность"), config.saturation)
            .setDefaultValue(1.0f).setMin(0.0f).setMax(3.0f)
            .setTooltip(Text.literal("Насыщенность цветов"))
            .setSaveConsumer(v -> { config.saturation = v; config.preset = "custom"; }).build());

        visual.addEntry(e.startFloatField(Text.literal("Гамма"), config.gamma)
            .setDefaultValue(1.0f).setMin(0.1f).setMax(5.0f)
            .setTooltip(Text.literal("Гамма-коррекция"))
            .setSaveConsumer(v -> { config.gamma = v; config.preset = "custom"; }).build());

        visual.addEntry(e.startBooleanToggle(Text.literal("Сглаживание"), config.smoothLighting)
            .setDefaultValue(true)
            .setTooltip(Text.literal("Сглаженное освещение блоков"))
            .setSaveConsumer(v -> { config.smoothLighting = v; config.preset = "custom"; }).build());

        ConfigCategory fog = builder.getOrCreateCategory(Text.literal("Туман"));

        if (ModCompat.DISABLED_FOG) {
            fog.addEntry(e.startTextDescription(
                Text.literal("⚠ Sodium/Iris: настройки тумана отключены для совместимости")
            ).build());
        }

        fog.addEntry(e.startBooleanToggle(Text.literal("Кастомный туман"), config.customFog)
            .setDefaultValue(false)
            .setTooltip(Text.literal("Включить свои настройки тумана"))
            .setSaveConsumer(v -> { config.customFog = v; config.preset = "custom"; }).build());

        fog.addEntry(e.startFloatField(Text.literal("Дальность тумана"), config.fogDistance)
            .setDefaultValue(0.7f).setMin(0.0f).setMax(3.0f)
            .setTooltip(Text.literal("Множитель дальности тумана"))
            .setSaveConsumer(v -> { config.fogDistance = v; config.preset = "custom"; }).build());

        fog.addEntry(e.startFloatField(Text.literal("Плотность тумана"), config.fogDensity)
            .setDefaultValue(1.0f).setMin(0.0f).setMax(3.0f)
            .setTooltip(Text.literal("Плотность тумана (выше = гуще)"))
            .setSaveConsumer(v -> { config.fogDensity = v; config.preset = "custom"; }).build());

        fog.addEntry(e.startBooleanToggle(Text.literal("Усиление цвета тумана"), config.fogColorBoost)
            .setDefaultValue(false)
            .setTooltip(Text.literal("Делает цвет тумана более насыщенным"))
            .setSaveConsumer(v -> { config.fogColorBoost = v; config.preset = "custom"; }).build());

        ConfigCategory culling = builder.getOrCreateCategory(Text.literal("Оптимизация рендера"));

        if (ModCompat.DISABLED_CULLING) {
            culling.addEntry(e.startTextDescription(
                Text.literal("⚠ Sodium: каллинг управляется Sodium. Опции ниже могут не работать.")
            ).build());
        }

        culling.addEntry(e.startBooleanToggle(Text.literal("Каллинг сзади камеры"), config.behindCulling)
            .setDefaultValue(true)
            .setTooltip(Text.literal("Не рендерить чанки позади камеры"))
            .setSaveConsumer(v -> { config.behindCulling = v; config.preset = "custom"; }).build());

        culling.addEntry(e.startIntField(Text.literal("Макс. дистанция рендера (чанки)"), config.maxRenderDistance)
            .setDefaultValue(16).setMin(2).setMax(32)
            .setTooltip(Text.literal("Максимальная дистанция (в чанках)"))
            .setSaveConsumer(v -> { config.maxRenderDistance = v; config.preset = "custom"; }).build());

        ConfigCategory perf = builder.getOrCreateCategory(Text.literal("Производительность"));

        perf.addEntry(e.startBooleanToggle(Text.literal("Авто-оптимизация"), config.autoOptimize)
            .setDefaultValue(true)
            .setTooltip(Text.literal("Автоматически подбирать настройки под железо"))
            .setSaveConsumer(v -> { config.autoOptimize = v; config.preset = "custom"; }).build());

        perf.addEntry(e.startIntField(Text.literal("Целевой FPS"), config.targetFPS)
            .setDefaultValue(60).setMin(15).setMax(999)
            .setTooltip(Text.literal("Целевое значение FPS для авто-настроек"))
            .setSaveConsumer(v -> { config.targetFPS = v; config.preset = "custom"; }).build());

        perf.addEntry(e.startBooleanToggle(Text.literal("Динам. дистанция рендера"), config.dynamicRenderDistance)
            .setDefaultValue(true)
            .setTooltip(Text.literal("Авто-подстройка дистанции прорисовки под FPS"))
            .setSaveConsumer(v -> { config.dynamicRenderDistance = v; config.preset = "custom"; }).build());

        perf.addEntry(e.startIntField(Text.literal("Мин. дистанция рендера"), config.minRenderDistance)
            .setDefaultValue(4).setMin(2).setMax(32)
            .setTooltip(Text.literal("Минимальная дистанция (в чанках)"))
            .setSaveConsumer(v -> { config.minRenderDistance = v; config.preset = "custom"; }).build());

        perf.addEntry(e.startIntField(Text.literal("Макс. дистанция рендера"), config.maxRenderDistance)
            .setDefaultValue(16).setMin(2).setMax(32)
            .setTooltip(Text.literal("Максимальная дистанция (в чанках)"))
            .setSaveConsumer(v -> { config.maxRenderDistance = v; config.preset = "custom"; }).build());

        perf.addEntry(e.startBooleanToggle(Text.literal("Показать FPS"), config.showFps)
            .setDefaultValue(false)
            .setTooltip(Text.literal("Показывать счётчик FPS на экране"))
            .setSaveConsumer(v -> { config.showFps = v; config.preset = "custom"; }).build());

        perf.addEntry(e.startBooleanToggle(Text.literal("Время прорисовки чанков"), config.showChunkRenderTime)
            .setDefaultValue(false)
            .setTooltip(Text.literal("Показывать время рендера чанков (мс)"))
            .setSaveConsumer(v -> { config.showChunkRenderTime = v; config.preset = "custom"; }).build());

        ConfigCategory presets = builder.getOrCreateCategory(Text.literal("Пресеты"));

        presets.addEntry(e.startSelector(
            Text.literal("Пресет качества"),
            new String[]{"low", "mid", "high", "ultra", "balanced", "custom"},
            config.preset
        ).setDefaultValue("balanced")
            .setTooltip(Text.literal("low — макс. FPS\nmid — сбалансированно\nhigh — красиво\nultra — максимальное качество\nbalanced — авто\ncustom — свои настройки"))
            .setSaveConsumer(v -> {
                ConfigManager.applyPreset(v);
            }).build());

        presets.addEntry(e.startTextDescription(
            Text.literal("Правый Shift — быстрое открытие меню")
        ).build());

        builder.setSavingRunnable(ConfigManager::save);

        return builder.build();
    }
}
