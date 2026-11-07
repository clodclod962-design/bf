package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;
import my.mod.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;

public class AntiAfk extends Module {
    private final NumberSetting interval = num("IntervalSec", 20, 5, 120, 5);
    private int ticks;

    public AntiAfk(int key) { super("AntiAFK", Category.MISC, key); }

    @Override
    public void onTick() {
        ClientPlayerEntity p = mc.player;
        if (p == null) return;
        if (++ticks >= interval.i() * 20) {
            ticks = 0;
            p.swingHand(Hand.MAIN_HAND);
            p.setYaw(p.getYaw() + 5f);
        }
    }
}
