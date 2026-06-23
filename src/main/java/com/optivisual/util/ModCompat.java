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

    static {
        HAS_SODIUM = FabricLoader.getInstance().isModLoaded("sodium");
        HAS_IRIS = FabricLoader.getInstance().isModLoaded("iris");
        HAS_IMMEDIATELYFAST = FabricLoader.getInstance().isModLoaded("immediatelyfast");
        HAS_ENTITYCULLING = FabricLoader.getInstance().isModLoaded("entityculling");

        if (HAS_SODIUM) LOGGER.info("Sodium обнаружен — OptiVisual работает в режиме совместимости");
        if (HAS_IRIS) LOGGER.info("Iris обнаружен — туман/небо OptiVisual через Iris");
    }
}
