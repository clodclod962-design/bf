package my.mod.module;

import my.mod.module.impl.HudModule;
import my.mod.module.setting.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    private final String name;
    private final Category category;
    private int key; // GLFW key code, 0 = не назначен
    private boolean enabled;
    boolean keyWasDown;
    private final List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, Category category, int key) {
        this.name = name;
        this.category = category;
        this.key = key;
    }

    public String getName() { return name; }
    public Category getCategory() { return category; }
    public int getKey() { return key; }
    public void setKey(int key) { this.key = key; }
    public boolean isEnabled() { return enabled; }
    public List<Setting<?>> getSettings() { return settings; }

    public void toggle() { setEnabled(!enabled); }

    public void setEnabled(boolean value) {
        if (enabled == value) return;
        enabled = value;
        if (enabled) onEnable(); else onDisable();
        if (mc.player != null && !(this instanceof HudModule)) {
            mc.player.sendMessage(Text.literal(name + (enabled ? " §aON" : " §cOFF")), true);
        }
    }

    protected void onEnable() {}
    protected void onDisable() {}
    public void onTick() {}

    /** Вызывается при отрисовке мира. Координаты мира уже сдвинуты относительно камеры. */
    public void onRender(MatrixStack ms, float tickDelta, Camera camera) {}

    // --- helpers для объявления настроек ---
    protected <S extends Setting<?>> S add(S s) { settings.add(s); return s; }
    protected BoolSetting bool(String n, boolean def) { return add(new BoolSetting(n, def)); }
    protected NumberSetting num(String n, double def, double min, double max, double step) {
        return add(new NumberSetting(n, def, min, max, step));
    }
    protected <E extends Enum<E>> EnumSetting<E> mode(String n, E def) { return add(new EnumSetting<>(n, def)); }

    /** Добавляет 5 настроек цвета (режим, R, G, B, скорость) с префиксом в названии. */
    protected ColorOptions color(String prefix, ColorOptions.Mode defMode, int rgb) {
        EnumSetting<ColorOptions.Mode> m = mode(prefix + "Color", defMode);
        NumberSetting r = num(prefix + "Red", (rgb >> 16) & 255, 0, 255, 1);
        NumberSetting g = num(prefix + "Green", (rgb >> 8) & 255, 0, 255, 1);
        NumberSetting b = num(prefix + "Blue", rgb & 255, 0, 255, 1);
        NumberSetting sp = num(prefix + "Speed", 1.0, 0.1, 5.0, 0.1);
        return new ColorOptions(m, r, g, b, sp);
    }
}
