/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.behavior.stackblur;

import java.util.HashSet;
import java.util.Set;

import moe.studio.frontia.SoLibPlugin;

/**
 * @author kaede
 * @version date 2016/12/5
 */

public class StackBlurPlugin extends SoLibPlugin<IStackBlur> {
    public StackBlurPlugin(String apkPath) {
        super(apkPath);

        Set<String> ignore = new HashSet<>();
        ignore.add("support_v4");
        setIgnoreDepencies(ignore);
    }
}
