package com.iamkaf.arcanearmory.material.util;

import org.jetbrains.annotations.Nullable;

public class ColorUtil {
    /**
     * Provides an int corresponding to the provided #hex code.
     * <p>
     * Leave empty for no tint.
     * </p>
     */
    public static int tint(@Nullable String hexString) {
        if (hexString == null) {
            return -1;
        }

        // Remove the '#' symbol if present
        if (hexString.startsWith("#")) {
            hexString = hexString.substring(1);
        }

        return Integer.parseInt(hexString, 16);
    }

    /**
     * Provides an int corresponding to the provided #hex code.
     * <p>
     * Leave empty for no tint.
     * </p>
     */
    public static int tint() {
        return ColorUtil.tint(null);
    }
}
