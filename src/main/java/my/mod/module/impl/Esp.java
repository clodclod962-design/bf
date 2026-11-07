package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;
import my.mod.module.setting.BoolSetting;
import my.mod.module.setting.ColorOptions;
import my.mod.module.setting.NumberSetting;
import my.mod.util.RenderUtil;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

/** ESP: рамки вокруг игроков/мобов сквозь стены (+ опциональная светящаяся обводка). */
public class Esp extends Module {
    private final BoolSetting players = bool("Players", true);
    private final BoolSetting mobs = bool("Mobs", true);
    private final BoolSetting boxes = bool("Boxes", true);
    private final BoolSetting outline = bool("Outline", false);
    private final BoolSetting typeColors = bool("TypeColors", true); // красный игроки / зелёный мобы
    private final NumberSetting range = num("Range", 128, 16, 256, 8);
    private final ColorOptions color = color("", ColorOptions.Mode.RAINBOW, 0x00FFFF);

    public Esp(int key) { super("ESP", Category.RENDER, key); }

    public boolean matches(Entity e) {
        if (e == mc.player || !(e instanceof LivingEntity) || e instanceof ArmorStandEntity) return false;
        return e instanceof PlayerEntity ? players.get() : mobs.get();
    }

    /** Используется миксином MinecraftClient.hasOutline */
    public boolean shouldOutline(Entity e) { return outline.get() && matches(e); }

    @Override
    public void onRender(MatrixStack ms, float td, Camera camera) {
        if (!boxes.get()) return;
        Matrix4f m = ms.peek().getPositionMatrix();
        BufferBuilder bb = RenderUtil.begin();
        int idx = 0;
        for (Entity e : mc.world.getEntities()) {
            if (!matches(e) || e.distanceTo(mc.player) > range.get()) continue;
            Vec3d d = new Vec3d(
                    MathHelper.lerp(td, e.prevX, e.getX()) - e.getX(),
                    MathHelper.lerp(td, e.prevY, e.getY()) - e.getY(),
                    MathHelper.lerp(td, e.prevZ, e.getZ()) - e.getZ());
            Box b = e.getBoundingBox().offset(d);
            int c = typeColors.get() ? (e instanceof PlayerEntity ? 0xFF5555 : 0x55FF55)
                                     : color.get((idx++ % 16) / 16f);
            RenderUtil.box(bb, m, b, c, 1f);
        }
        RenderUtil.end(bb);
    }
}
