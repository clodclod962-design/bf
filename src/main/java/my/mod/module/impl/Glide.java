package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;
import my.mod.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Glide extends Module {
    private final NumberSetting fall = num("FallSpeed", 0.1, 0.02, 0.5, 0.02);

    public Glide(int key) { super("Glide", Category.MOVEMENT, key); }

    @Override
    public void onTick() {
        ClientPlayerEntity p = mc.player;
        if (p == null || p.isOnGround() || p.isFallFlying() || p.getAbilities().flying) return;
        Vec3d v = p.getVelocity();
        if (v.y < -fall.get()) p.setVelocity(v.x, -fall.get(), v.z);
    }
}
