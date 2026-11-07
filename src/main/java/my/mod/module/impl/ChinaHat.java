package my.mod.module.impl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import my.mod.module.Category;
import my.mod.module.Module;
import my.mod.module.setting.BoolSetting;
import my.mod.module.setting.ColorOptions;
import my.mod.module.setting.NumberSetting;
import my.mod.util.RenderUtil;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

/** Неоновая «китайская/шаолиньская» шляпа над головой игрока. */
public class ChinaHat extends Module {
    private final BoolSetting self = bool("Self", true);
    private final BoolSetting others = bool("Others", true);
    private final NumberSetting radius = num("Radius", 0.55, 0.2, 1.2, 0.01);
    private final NumberSetting height = num("Height", 0.30, 0.1, 0.9, 0.01);
    private final NumberSetting offset = num("HeightOffset", 0.05, -0.3, 0.6, 0.01);
    private final NumberSetting sides = num("Sides", 40, 6, 90, 1);
    private final NumberSetting rings = num("Rings", 3, 0, 8, 1);
    private final BoolSetting spokes = bool("Spokes", true);
    private final BoolSetting fill = bool("Fill", true);
    private final NumberSetting fillAlpha = num("FillAlpha", 0.35, 0.05, 1.0, 0.05);
    private final BoolSetting glow = bool("Glow", true);
    private final NumberSetting lineWidth = num("LineWidth", 2.0, 1.0, 6.0, 0.5);
    private final NumberSetting range = num("Range", 64, 8, 256, 8);
    private final ColorOptions color = color("Hat", ColorOptions.Mode.RAINBOW, 0x00FFFF);

    public ChinaHat(int key) { super("ChinaHat", Category.EFFECTS, key); }

    @Override
    public void onRender(MatrixStack ms, float td, Camera camera) {
        Matrix4f m = ms.peek().getPositionMatrix();
        boolean first = mc.options.getPerspective().isFirstPerson();

        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.lineWidth(lineWidth.f());
        if (glow.get()) RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);

        for (PlayerEntity p : mc.world.getPlayers()) {
            boolean isSelf = p == mc.player;
            if (isSelf ? (!self.get() || first) : !others.get()) continue;
            if (p.isInvisible() || p.isSpectator()) continue;
            if (!isSelf && p.distanceTo(mc.player) > range.get()) continue;

            double x = MathHelper.lerp(td, p.prevX, p.getX());
            double y = MathHelper.lerp(td, p.prevY, p.getY()) + p.getHeight() + offset.get();
            double z = MathHelper.lerp(td, p.prevZ, p.getZ());
            drawHat(m, x, y, z);
        }

        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(2.0f);
        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();
    }

    private void drawHat(Matrix4f m, double x, double y, double z) {
        int n = sides.i();
        double r = radius.get(), h = height.get();
        int a = (int) (fillAlpha.f() * 255);

        // заливка конуса
        if (fill.get()) {
            BufferBuilder bb = Tessellator.getInstance().getBuffer();
            bb.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
            int ac = color.get(0f);
            bb.vertex(m, (float) x, (float) (y + h), (float) z)
                    .color((ac >> 16) & 255, (ac >> 8) & 255, ac & 255, a).next();
            for (int i = 0; i <= n; i++) {
                double ang = Math.PI * 2.0 * i / n;
                int c = color.get(i / (float) n);
                bb.vertex(m, (float) (x + Math.cos(ang) * r), (float) y, (float) (z + Math.sin(ang) * r))
                        .color((c >> 16) & 255, (c >> 8) & 255, c & 255, a).next();
            }
            RenderUtil.end(bb);
        }

        // контур: нижнее кольцо + промежуточные кольца + спицы
        BufferBuilder lb = RenderUtil.begin();
        int rc = rings.i();
        for (int k = 0; k <= rc; k++) {
            double f = (double) k / (rc + 1);          // 0 = низ, ближе к 1 = вершина
            double rr = r * (1.0 - f), yy = y + h * f;
            for (int i = 0; i < n; i++) {
                double a1 = Math.PI * 2.0 * i / n, a2 = Math.PI * 2.0 * (i + 1) / n;
                RenderUtil.line(lb, m,
                        x + Math.cos(a1) * rr, yy, z + Math.sin(a1) * rr,
                        x + Math.cos(a2) * rr, yy, z + Math.sin(a2) * rr,
                        color.get(i / (float) n), 1f);
            }
        }
        if (spokes.get()) {
            int step = Math.max(1, n / 12);
            for (int i = 0; i < n; i += step) {
                double ang = Math.PI * 2.0 * i / n;
                RenderUtil.line(lb, m, x, y + h, z,
                        x + Math.cos(ang) * r, y, z + Math.sin(ang) * r,
                        color.get(i / (float) n), 1f);
            }
        }
        RenderUtil.end(lb);
    }
}
