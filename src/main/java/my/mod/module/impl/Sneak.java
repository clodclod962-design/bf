package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;

public class Sneak extends Module {
    public Sneak(int key) { super("Sneak", Category.MOVEMENT, key); }

    @Override
    public void onTick() { mc.options.sneakKey.setPressed(true); }

    @Override
    protected void onDisable() { mc.options.sneakKey.setPressed(false); }
}
