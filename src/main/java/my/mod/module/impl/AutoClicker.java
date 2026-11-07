package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class AutoClicker extends Module {
    public AutoClicker(int key) { super("AutoClicker", Category.COMBAT, key); }

    @Override
    public void onTick() {
        ClientPlayerEntity p = mc.player;
        if (p == null || mc.interactionManager == null || mc.currentScreen != null) return;
        if (mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.ENTITY) return;
        if (p.getAttackCooldownProgress(0f) < 0.95f) return;
        Entity target = ((EntityHitResult) mc.crosshairTarget).getEntity();
        mc.interactionManager.attackEntity(p, target);
        p.swingHand(Hand.MAIN_HAND);
    }
}
