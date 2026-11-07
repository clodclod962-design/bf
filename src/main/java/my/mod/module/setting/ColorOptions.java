package my.mod.module.setting;

import net.minecraft.util.math.MathHelper;

/** Набор настроек цвета: режим (STATIC / RAINBOW / PULSE), RGB и скорость. */
public class ColorOptions {
    public enum Mode { STATIC, RAINBOW, PULSE }

    private final EnumSetting<Mode> mode;
    private final NumberSetting r, g, b, speed;

    public ColorOptions(EnumSetting<Mode> mode, NumberSetting r, NumberSetting g, NumberSetting b, NumberSetting speed) {
        this.mode = mode;
        this.r = r;
        this.g = g;
        this.b = b;
        this.speed = speed;
    }

    /** offset 0..1 сдвигает фазу (для радуги вдоль кольца/между объектами). Возвращает 0xRRGGBB. */
    public int get(float offset) {
        float t = (System.currentTimeMillis() % 100000L) / 1000f * speed.f() * 0.25f;
        int base = (r.i() << 16) | (g.i() << 8) | b.i();
        switch (mode.get()) {
            case RAINBOW: {
                float hue = ((t + offset) % 1f + 1f) % 1f;
                return MathHelper.hsvToRgb(hue, 0.85f, 1f);
            }
            case PULSE: {
                float k = 0.55f + 0.45f * (float) Math.sin((t + offset) * Math.PI * 2.0);
                int rr = (int) (((base >> 16) & 255) * k);
                int gg = (int) (((base >> 8) & 255) * k);
                int bb = (int) ((base & 255) * k);
                return (rr << 16) | (gg << 8) | bb;
            }
            default:
                return base;
        }
    }
}
