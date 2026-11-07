package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;
import my.mod.module.setting.NumberSetting;

public class Step extends Module {
    private final NumberSetting height = num("Height", 1.0, 0.6, 2.5, 0.1);

    public Step(int key) { super("Step", Category.MOVEMENT, key); }

    @Override
    public void onTick() { if (mc.player != null) mc.player.setStepHeight(height.f()); }

    @Override
    protected void onDisable() { if (mc.player != null) mc.player.setStepHeight(0.6f); }
}
