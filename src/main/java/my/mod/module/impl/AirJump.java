package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;

public class AirJump extends Module {
    private boolean wasDown;

    public AirJump(int key) { super("AirJump", Category.MOVEMENT, key); }

    @Override
    public void onTick() {
        ClientPlayerEntity p = mc.player;
        if (p == null) return;
        boolean down = mc.options.jumpKey.isPressed();
        if (down && !wasDown && !p.isOnGround() && !p.getAbilities().flying) p.jump();
        wasDown = down;
    }
}
