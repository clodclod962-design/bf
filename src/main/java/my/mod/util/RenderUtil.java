package my.mod.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;

/** Рисование линий/рамок сквозь стены. */
public final class RenderUtil {
    private RenderUtil() {}

    public static void setup() {
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.lineWidth(2.0f);
    }

    public static void restore() {
        RenderSystem.lineWidth(1.0f);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }

    public static BufferBuilder begin() {
        BufferBuilder bb = Tessellator.getInstance().getBuffer();
        bb.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        return bb;
    }

    public static void end(BufferBuilder bb) {
        var built = bb.endNullable();
        if (built != null) BufferRenderer.drawWithGlobalProgram(built);
    }

    public static void line(BufferBuilder bb, Matrix4f m,
                            double x1, double y1, double z1, double x2, double y2, double z2,
                            int rgb, float alpha) {
        int r = (rgb >> 16) & 255, g = (rgb >> 8) & 255, b = rgb & 255, a = (int) (alpha * 255);
        bb.vertex(m, (float) x1, (float) y1, (float) z1).color(r, g, b, a).next();
        bb.vertex(m, (float) x2, (float) y2, (float) z2).color(r, g, b, a).next();
    }

    public static void box(BufferBuilder bb, Matrix4f m, Box bx, int rgb, float alpha) {
        double x1 = bx.minX, y1 = bx.minY, z1 = bx.minZ, x2 = bx.maxX, y2 = bx.maxY, z2 = bx.maxZ;
        // низ
        line(bb, m, x1, y1, z1, x2, y1, z1, rgb, alpha);
        line(bb, m, x2, y1, z1, x2, y1, z2, rgb, alpha);
        line(bb, m, x2, y1, z2, x1, y1, z2, rgb, alpha);
        line(bb, m, x1, y1, z2, x1, y1, z1, rgb, alpha);
        // верх
        line(bb, m, x1, y2, z1, x2, y2, z1, rgb, alpha);
        line(bb, m, x2, y2, z1, x2, y2, z2, rgb, alpha);
        line(bb, m, x2, y2, z2, x1, y2, z2, rgb, alpha);
        line(bb, m, x1, y2, z2, x1, y2, z1, rgb, alpha);
        // стойки
        line(bb, m, x1, y1, z1, x1, y2, z1, rgb, alpha);
        line(bb, m, x2, y1, z1, x2, y2, z1, rgb, alpha);
        line(bb, m, x2, y1, z2, x2, y2, z2, rgb, alpha);
        line(bb, m, x1, y1, z2, x1, y2, z2, rgb, alpha);
    }
}
