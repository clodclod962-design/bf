package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;
import my.mod.module.setting.BoolSetting;
import my.mod.module.setting.NumberSetting;
import my.mod.util.RenderUtil;
import net.minecraft.block.entity.*;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;

/** Подсветка сундуков, шалкеров, эндер-сундуков и бочек сквозь стены. */
public class StorageEsp extends Module {
    private final BoolSetting chests = bool("Chests", true);
    private final BoolSetting enderChests = bool("EnderChests", true);
    private final BoolSetting shulkers = bool("Shulkers", true);
    private final BoolSetting barrels = bool("Barrels", true);
    private final NumberSetting range = num("Range", 96, 16, 256, 8);

    public StorageEsp(int key) { super("StorageESP", Category.RENDER, key); }

    private int colorOf(BlockEntity be) {
        if (be instanceof TrappedChestBlockEntity) return chests.get() ? 0xFF5555 : -1;
        if (be instanceof ChestBlockEntity) return chests.get() ? 0xFFAA00 : -1;
        if (be instanceof EnderChestBlockEntity) return enderChests.get() ? 0xAA55FF : -1;
        if (be instanceof ShulkerBoxBlockEntity) return shulkers.get() ? 0xFF66CC : -1;
        if (be instanceof BarrelBlockEntity) return barrels.get() ? 0xC08040 : -1;
        return -1;
    }

    @Override
    public void onRender(MatrixStack ms, float td, Camera camera) {
        Matrix4f m = ms.peek().getPositionMatrix();
        int rd = mc.options.getViewDistance().getValue();
        ChunkPos cp = mc.player.getChunkPos();
        double r2 = range.get() * range.get();
        Vec3d me = mc.player.getPos();

        BufferBuilder bb = RenderUtil.begin();
        for (int dx = -rd; dx <= rd; dx++) {
            for (int dz = -rd; dz <= rd; dz++) {
                WorldChunk ch = mc.world.getChunkManager().getWorldChunk(cp.x + dx, cp.z + dz);
                if (ch == null) continue;
                for (BlockEntity be : ch.getBlockEntities().values()) {
                    int color = colorOf(be);
                    if (color < 0) continue;
                    BlockPos p = be.getPos();
                    if (Vec3d.ofCenter(p).squaredDistanceTo(me) > r2) continue;
                    RenderUtil.box(bb, m, new Box(p), color, 1f);
                }
            }
        }
        RenderUtil.end(bb);
    }
}
