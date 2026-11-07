package my.mod.gui;

import my.mod.MyMod;
import my.mod.config.Config;
import my.mod.module.Category;
import my.mod.module.Module;
import my.mod.module.setting.BoolSetting;
import my.mod.module.setting.EnumSetting;
import my.mod.module.setting.NumberSetting;
import my.mod.module.setting.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ClickGUI (Right Shift).
 * ЛКМ по модулю - вкл/выкл, ПКМ - настройки, СКМ - назначить клавишу (ESC - снять).
 * Колесо мыши - прокрутка вверх/вниз.
 */
public class ClickGui extends Screen {
    private static final int HEADER = 16, ROW = 14, SROW = 12, GAP = 8, X0 = 10, Y0 = 10;
    private static final Set<Module> EXPANDED = new HashSet<>();
    private static int scroll = 0;

    private record Hit(int x1, int y1, int x2, int y2, Object target) {
        boolean in(double x, double y) { return x >= x1 && x < x2 && y >= y1 && y < y2; }
    }

    private final List<Hit> hits = new ArrayList<>();
    private Module binding;
    private NumberSetting dragging;
    private int dragX, dragW;

    public ClickGui() { super(Text.literal("MyMod")); }

    @Override public boolean shouldPause() { return false; }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        ctx.fill(0, 0, width, height, 0x80000000);
        hits.clear();

        int n = Category.values().length;
        int w = MathHelper.clamp((width - 2 * X0 - GAP * (n - 1)) / n, 96, 124);

        int x = X0;
        for (Category cat : Category.values()) {
            int y = Y0 + scroll;
            ctx.fill(x, y, x + w, y + HEADER, 0xFF0B5D4B);
            ctx.drawText(textRenderer, cat.getLabel(), x + 4, y + 4, 0xFFFFFFFF, false);
            y += HEADER;

            for (Module m : MyMod.MODULES.byCategory(cat)) {
                boolean hover = mx >= x && mx < x + w && my >= y && my < y + ROW;
                int bg = m.isEnabled() ? (hover ? 0xFF3C8F66 : 0xFF2F7A56) : (hover ? 0xFF2A2A2A : 0xFF1C1C1C);
                ctx.fill(x, y, x + w, y + ROW, bg);
                ctx.drawText(textRenderer, m.getName(), x + 4, y + 3, 0xFFFFFFFF, false);

                String keyText = binding == m ? "..." : keyName(m.getKey());
                if (keyText != null) {
                    ctx.drawText(textRenderer, keyText, x + w - textRenderer.getWidth(keyText) - 4, y + 3, 0xFFB0B0B0, false);
                }
                hits.add(new Hit(x, y, x + w, y + ROW, m));
                y += ROW;

                if (EXPANDED.contains(m)) {
                    for (Setting<?> s : m.getSettings()) {
                        ctx.fill(x, y, x + w, y + SROW, 0xFF121212);
                        if (s instanceof NumberSetting ns) {
                            double t = (ns.get() - ns.getMin()) / (ns.getMax() - ns.getMin());
                            ctx.fill(x, y, x + (int) (w * t), y + SROW, 0xFF28507A);
                        } else if (s instanceof BoolSetting b && b.get()) {
                            ctx.fill(x, y, x + 3, y + SROW, 0xFF55FFAA);
                        }
                        ctx.drawText(textRenderer, s.getName(), x + 6, y + 2, 0xFFDDDDDD, false);
                        String v = s.display();
                        ctx.drawText(textRenderer, v, x + w - textRenderer.getWidth(v) - 4, y + 2, 0xFFAAAAAA, false);
                        hits.add(new Hit(x, y, x + w, y + SROW, s));
                        y += SROW;
                    }
                }
            }
            x += w + GAP;
        }

        ctx.fill(0, height - 14, width, height, 0xC0000000);
        ctx.drawText(textRenderer, "LMB toggle | RMB settings | MMB bind key (ESC = clear) | Wheel scroll | RShift close",
                10, height - 11, 0xFFAAAAAA, false);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        for (Hit h : hits) {
            if (!h.in(mx, my)) continue;
            Object t = h.target();
            if (t instanceof Module m) {
                if (button == 0) m.toggle();
                else if (button == 1) { if (!EXPANDED.remove(m)) EXPANDED.add(m); }
                else if (button == 2) binding = m;
            } else if (t instanceof BoolSetting b) {
                if (button == 0) { b.toggle(); Config.save(); }
            } else if (t instanceof EnumSetting<?> e) {
                if (button == 0) { e.next(); Config.save(); }
            } else if (t instanceof NumberSetting ns) {
                if (button == 0) {
                    dragging = ns;
                    dragX = h.x1();
                    dragW = h.x2() - h.x1();
                    updateDrag(mx);
                }
            }
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    private void updateDrag(double mx) {
        double t = MathHelper.clamp((mx - dragX) / dragW, 0.0, 1.0);
        dragging.set(dragging.getMin() + t * (dragging.getMax() - dragging.getMin()));
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (dragging != null) { updateDrag(mx); return true; }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (dragging != null) { dragging = null; Config.save(); }
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double amount) {
        scroll = MathHelper.clamp(scroll + (int) (amount * 24), -3000, 0);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (binding != null) {
            binding.setKey(keyCode == GLFW.GLFW_KEY_ESCAPE ? 0 : keyCode);
            binding = null;
            Config.save();
            return true;
        }
        if (keyCode == MyMod.GUI_KEY || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void removed() {
        Config.save();
        super.removed();
    }

    private static String keyName(int key) {
        if (key <= 0) return null;
        String s = InputUtil.fromKeyCode(key, 0).getLocalizedText().getString();
        return s.length() > 7 ? s.substring(0, 7) : s;
    }
}
