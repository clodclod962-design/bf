package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;
import my.mod.module.setting.BoolSetting;
import net.minecraft.block.*;
import net.minecraft.registry.tag.BlockTags;

/**
 * X-Ray: рисуются только руды (и по желанию сундуки/спавнеры), остальные блоки скрыты.
 * Логика в BlockMixin (Block.shouldDrawSide). После смены настроек выключи и включи модуль.
 * Не работает вместе с Sodium/Embeddium (они заменяют рендер чанков).
 */
public class Xray extends Module {
    public static volatile boolean active;
    public static Xray INSTANCE;

    private final BoolSetting storage = bool("Storage", true);
    private final BoolSetting spawners = bool("Spawners", true);

    public Xray(int key) {
        super("Xray", Category.RENDER, key);
        INSTANCE = this;
    }

    @Override protected void onEnable() { active = true; reload(); }
    @Override protected void onDisable() { active = false; reload(); }

    private void reload() {
        if (mc.worldRenderer != null && mc.world != null) mc.worldRenderer.reload();
    }

    /** Вызывается из потока построения чанков - только чтение. */
    public boolean isVisible(BlockState s) {
        if (s.isIn(BlockTags.COAL_ORES) || s.isIn(BlockTags.IRON_ORES) || s.isIn(BlockTags.COPPER_ORES)
                || s.isIn(BlockTags.GOLD_ORES) || s.isIn(BlockTags.REDSTONE_ORES) || s.isIn(BlockTags.LAPIS_ORES)
                || s.isIn(BlockTags.DIAMOND_ORES) || s.isIn(BlockTags.EMERALD_ORES)) return true;
        Block b = s.getBlock();
        if (b == Blocks.NETHER_QUARTZ_ORE || b == Blocks.NETHER_GOLD_ORE || b == Blocks.ANCIENT_DEBRIS) return true;
        if (spawners.get() && b == Blocks.SPAWNER) return true;
        if (storage.get() && (b instanceof ChestBlock || b instanceof ShulkerBoxBlock
                || b instanceof BarrelBlock || b == Blocks.ENDER_CHEST)) return true;
        return false;
    }
}
