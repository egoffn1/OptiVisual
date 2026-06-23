package com.optivisual.util;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModCompat {
    private static final Logger LOGGER = LoggerFactory.getLogger("OptiVisualCompat");

    public static final boolean HAS_SODIUM;
    public static final boolean HAS_IRIS;
    public static final boolean HAS_IMMEDIATELYFAST;
    public static final boolean HAS_ENTITYCULLING;

    public static final boolean DISABLED_CULLING;
    public static final boolean DISABLED_FOG;
    public static final boolean DISABLED_SKY;

    static {
        HAS_SODIUM = FabricLoader.getInstance().isModLoaded("sodium");
        HAS_IRIS = FabricLoader.getInstance().isModLoaded("iris");
        HAS_IMMEDIATELYFAST = FabricLoader.getInstance().isModLoaded("immediatelyfast");
        HAS_ENTITYCULLING = FabricLoader.getInstance().isModLoaded("entityculling");

        DISABLED_CULLING = HAS_SODIUM;
        DISABLED_FOG = HAS_SODIUM || HAS_IRIS;
        DISABLED_SKY = HAS_IRIS;

        if (HAS_SODIUM) LOGGER.info("Sodium обнаружен — отключаю чанковый каллинг OptiVisual (Sodium делает свой)");
        if (HAS_IRIS) LOGGER.info("Iris обнаружен — отключаю туман/небо OptiVisual (Iris контролирует их)");
    }
}
