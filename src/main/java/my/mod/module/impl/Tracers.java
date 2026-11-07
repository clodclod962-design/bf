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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

/** Линии от прицела к игрокам (и мобам). */
public class Tracers extends Module {
    private final BoolSetting players = bool("Players", true);
    private final BoolSetting mobs = bool("Mobs", false);
    private final BoolSetting typeColors = bool("TypeColors", true);
    private final NumberSetting range = num("Range", 128, 16, 256, 8);
    private final ColorOptions color = color("", ColorOptions.Mode.RAINBOW, 0xFF00FF);

    public Tracers(int key) { super("Tracers", Category.RENDER, key); }

    @Override
    public void onRender(MatrixStack ms, float td, Camera camera) {
        Matrix4f m = ms.peek().getPositionMatrix();
        Vec3d start = camera.getPos().add(Vec3d.fromPolar(camera.getPitch(), camera.getYaw()));

        BufferBuilder bb = RenderUtil.begin();
        int idx = 0;
        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player || !(e instanceof LivingEntity) || e instanceof ArmorStandEntity) continue;
            boolean isPlayer = e instanceof PlayerEntity;
            if (isPlayer ? !players.get() : !mobs.get()) continue;
            if (e.distanceTo(mc.player) > range.get()) continue;

            double x = MathHelper.lerp(td, e.prevX, e.getX());
            double y = MathHelper.lerp(td, e.prevY, e.getY()) + e.getHeight() / 2.0;
            double z = MathHelper.lerp(td, e.prevZ, e.getZ());
            int c = typeColors.get() ? (isPlayer ? 0xFF5555 : 0x55FF55) : color.get((idx++ % 16) / 16f);
            RenderUtil.line(bb, m, start.x, start.y, start.z, x, y, z, c, 0.9f);
        }
        RenderUtil.end(bb);
    }
}
