package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;

public class AutoJump extends Module {
    public AutoJump(int key) { super("AutoJump", Category.MOVEMENT, key); }

    @Override
    public void onTick() {
        ClientPlayerEntity p = mc.player;
        if (p == null) return;
        boolean moving = p.input.movementForward != 0 || p.input.movementSideways != 0;
        if (moving && p.isOnGround() && !p.isSneaking()) p.jump();
    }
}
