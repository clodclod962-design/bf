package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;
import my.mod.module.setting.BoolSetting;
import my.mod.module.setting.ColorOptions;
import my.mod.module.setting.EnumSetting;
import my.mod.module.setting.NumberSetting;
import net.minecraft.util.math.Vec3d;

/** Своё небо (цвет/радуга), облака, время суток и погода - только на клиенте. */
public class Ambience extends Module {
    public enum Weather { DEFAULT, CLEAR, RAIN, STORM }

    private final BoolSetting changeTime = bool("ChangeTime", false);
    private final NumberSetting time = num("Time", 18000, 0, 24000, 500);
    private final EnumSetting<Weather> weather = mode("Weather", Weather.DEFAULT);
    private final BoolSetting sky = bool("CustomSky", true);
    private final BoolSetting clouds = bool("CustomClouds", true);
    private final ColorOptions color = color("Sky", ColorOptions.Mode.RAINBOW, 0x8A2BE2);

    public Ambience(int key) { super("Ambience", Category.EFFECTS, key); }

    @Override
    public void onTick() {
        if (mc.world == null) return;
        if (changeTime.get()) mc.world.setTimeOfDay(time.i());
        switch (weather.get()) {
            case CLEAR:
                mc.world.setRainGradient(0f);
                mc.world.setThunderGradient(0f);
                break;
            case RAIN:
                mc.world.setRainGradient(1f);
                mc.world.setThunderGradient(0f);
                break;
            case STORM:
                mc.world.setRainGradient(1f);
                mc.world.setThunderGradient(1f);
                break;
            default:
                break;
        }
    }

    public boolean skyActive() { return sky.get(); }
    public boolean cloudsActive() { return clouds.get(); }

    public Vec3d skyColor() {
        int c = color.get(0f);
        return new Vec3d(((c >> 16) & 255) / 255.0, ((c >> 8) & 255) / 255.0, (c & 255) / 255.0);
    }
}
