package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;
import my.mod.module.setting.NumberSetting;

public class FovChanger extends Module {
    private final NumberSetting fov = num("FOV", 100, 30, 110, 1);
    private int original = 70;

    public FovChanger(int key) { super("FovChanger", Category.RENDER, key); }

    @Override
    protected void onEnable() { original = mc.options.getFov().getValue(); }

    @Override
    public void onTick() { mc.options.getFov().setValue(fov.i()); }

    @Override
    protected void onDisable() { mc.options.getFov().setValue(original); }
}
