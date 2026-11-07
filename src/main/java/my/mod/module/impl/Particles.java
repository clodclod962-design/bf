package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;
import my.mod.module.setting.BoolSetting;
import my.mod.module.setting.EnumSetting;
import my.mod.module.setting.NumberSetting;
import my.mod.util.Fx;
import net.minecraft.entity.player.PlayerEntity;

/** Эффекты частиц вокруг игрока: след, аура, нимб, дождь. */
public class Particles extends Module {
    public enum Mode { TRAIL, AURA, HALO, RAIN }

    private final EnumSetting<Mode> mode = mode("Mode", Mode.AURA);
    private final EnumSetting<Fx> type = mode("Particle", Fx.END_ROD);
    private final NumberSetting amount = num("Amount", 3, 1, 12, 1);
    private final NumberSetting radius = num("Radius", 0.7, 0.2, 2.0, 0.05);
    private final NumberSetting height = num("Height", 1.8, 0.5, 3.0, 0.1);
    private final BoolSetting others = bool("Others", false);

    private int tick;

    public Particles(int key) { super("Particles", Category.EFFECTS, key); }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        tick++;
        spawn(mc.player);
        if (others.get()) {
            for (PlayerEntity p : mc.world.getPlayers()) {
                if (p != mc.player && p.distanceTo(mc.player) < 48) spawn(p);
            }
        }
    }

    private void spawn(PlayerEntity p) {
        double x = p.getX(), y = p.getY(), z = p.getZ();
        int n = amount.i();
        double rad = radius.get(), h = height.get();
        Fx fx = type.get();

        for (int i = 0; i < n; i++) {
            switch (mode.get()) {
                case TRAIL:
                    fx.spawn(x + Fx.rnd(0.3), y + 0.1 + Fx.rnd01() * 0.4, z + Fx.rnd(0.3),
                            Fx.rnd(0.02), 0.02, Fx.rnd(0.02));
                    break;
                case AURA: {
                    double a = tick * 0.25 + i * Math.PI * 2.0 / n;
                    double yy = y + ((tick * 0.04 + i / (double) n) % 1.0) * h;
                    fx.spawn(x + Math.cos(a) * rad, yy, z + Math.sin(a) * rad, 0, 0, 0);
                    break;
                }
                case HALO: {
                    double a = tick * 0.2 + i * Math.PI * 2.0 / n;
                    fx.spawn(x + Math.cos(a) * rad, y + p.getHeight() + 0.25, z + Math.sin(a) * rad, 0, 0, 0);
                    break;
                }
                case RAIN:
                    fx.spawn(x + Fx.rnd(rad), y + h + 0.3, z + Fx.rnd(rad), 0, -0.1, 0);
                    break;
            }
        }
    }
}
