package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;

public class AutoWalk extends Module {
    public AutoWalk(int key) { super("AutoWalk", Category.MOVEMENT, key); }

    @Override
    public void onTick() { mc.options.forwardKey.setPressed(true); }

    @Override
    protected void onDisable() { mc.options.forwardKey.setPressed(false); }
}
