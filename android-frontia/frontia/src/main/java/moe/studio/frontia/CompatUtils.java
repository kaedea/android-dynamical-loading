/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import moe.studio.frontia.BuildConfig;
import moe.studio.frontia.ext.PluginError.LoadError;

import static moe.studio.frontia.ext.PluginError.ERROR_LOA_DEPENDENCY;

/**
 * @author kaede
 * @version date 2016/12/7
 */

class CompatUtils {

    private static final int NO_REGISTER = -1;
    private static final Map<String, Integer> sHostLibraries;

    static {
        sHostLibraries = new HashMap<>();
        registerLibrary(BuildConfig.NAME, BuildConfig.VERSION_CODE); // Frontia Version.
    }

    static void registerLibrary(String name, int version) {
        if (sHostLibraries.containsKey(name)) {
            throw new RuntimeException("Library duplicated.");
        }
        sHostLibraries.put(name, version);
    }

    static void checkCompat(Map<String, Integer> dependencies, Set<String> ignores)
            throws LoadError {

        if (dependencies != null && dependencies.size() > 0) {
            Set<String> keySet = dependencies.keySet();
            StringBuilder sb = null;

            for (String key : keySet) {
                int required = dependencies.get(key);
                int current = sHostLibraries.containsKey(key) ? sHostLibraries.get(key) : NO_REGISTER;
                if (current < required && (ignores == null || !ignores.contains(key))) {
                    if (sb == null) {
                        sb = new StringBuilder();
                    }
                    sb.append("Library not satisfied, name = ").append(key)
                            .append(", current = ").append(current)
                            .append(", required = ").append(required).append("\n");
                }
            }

            if (sb != null) {
                throw new LoadError(sb.toString(), ERROR_LOA_DEPENDENCY);
            }
        }
    }
}
