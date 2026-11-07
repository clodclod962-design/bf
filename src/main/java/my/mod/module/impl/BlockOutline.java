package my.mod.module.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import my.mod.module.Category;
import my.mod.module.Module;
import my.mod.module.setting.ColorOptions;
import my.mod.module.setting.NumberSetting;
import my.mod.util.RenderUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Matrix4f;

/** Цветная (неоновая/радужная) обводка блока, на который ты смотришь. */
public class BlockOutline extends Module {
    private final NumberSetting lineWidth = num("LineWidth", 3.0, 1.0, 8.0, 0.5);
    private final ColorOptions color = color("", ColorOptions.Mode.RAINBOW, 0xFF00FF);

    public BlockOutline(int key) { super("BlockOutline", Category.RENDER, key); }

    @Override
    public void onRender(MatrixStack ms, float td, Camera camera) {
        HitResult hr = mc.crosshairTarget;
        if (!(hr instanceof BlockHitResult bhr) || hr.getType() != HitResult.Type.BLOCK) return;

        BlockPos pos = bhr.getBlockPos();
        BlockState st = mc.world.getBlockState(pos);
        VoxelShape shape = st.getOutlineShape(mc.world, pos);
        if (shape.isEmpty()) return;

        Box b = shape.getBoundingBox().offset(pos).expand(0.002);
        Matrix4f m = ms.peek().getPositionMatrix();

        RenderSystem.enableDepthTest();
        RenderSystem.lineWidth(lineWidth.f());
        BufferBuilder bb = RenderUtil.begin();
        RenderUtil.box(bb, m, b, color.get(0f), 1f);
        RenderUtil.end(bb);
        RenderSystem.lineWidth(2.0f);
        RenderSystem.disableDepthTest();
    }
}
