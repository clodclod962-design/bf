package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;
import my.mod.module.setting.EnumSetting;
import my.mod.module.setting.NumberSetting;
import my.mod.util.Fx;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

/** Всплеск частиц при ударе по цели (в том числе от KillAura). */
public class HitEffects extends Module {
    private final EnumSetting<Fx> type = mode("Particle", Fx.TOTEM);
    private final NumberSetting amount = num("Amount", 20, 1, 100, 1);
    private final NumberSetting speed = num("Speed", 0.25, 0.02, 1.0, 0.01);

    public HitEffects(int key) { super("HitEffects", Category.EFFECTS, key); }

    public void onHit(Entity target) {
        if (mc.world == null) return;
        Vec3d c = target.getPos().add(0, target.getHeight() / 2.0, 0);
        Fx fx = type.get();
        for (int i = 0; i < amount.i(); i++) {
            double dx = Fx.rnd(1), dy = Fx.rnd(1), dz = Fx.rnd(1);
            double len = Math.max(0.001, Math.sqrt(dx * dx + dy * dy + dz * dz));
            double s = speed.get();
            fx.spawn(c.x, c.y, c.z, dx / len * s, dy / len * s, dz / len * s);
        }
    }
}
