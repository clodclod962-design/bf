package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;
import my.mod.module.setting.BoolSetting;
import my.mod.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/** Сам ест еду из хотбара, когда голод ниже порога. */
public class AutoEat extends Module {
    private final NumberSetting hunger = num("Hunger", 14, 1, 19, 1);
    private final BoolSetting allowBad = bool("AllowBadFood", false);

    private boolean eating;
    private int prevSlot = -1;

    public AutoEat(int key) { super("AutoEat", Category.PLAYER, key); }

    @Override
    protected void onDisable() { if (eating) stop(); }

    @Override
    public void onTick() {
        ClientPlayerEntity p = mc.player;
        if (p == null) return;

        if (mc.currentScreen != null) { if (eating) stop(); return; }

        boolean hungry = p.getHungerManager().getFoodLevel() <= hunger.i();

        if (eating) {
            if (!hungry || !isGood(p.getMainHandStack())) stop();
            else mc.options.useKey.setPressed(true);
            return;
        }

        if (!hungry || p.isUsingItem()) return;

        int slot = findFood(p);
        if (slot < 0) return;
        prevSlot = p.getInventory().selectedSlot;
        p.getInventory().selectedSlot = slot;
        eating = true;
        mc.options.useKey.setPressed(true);
    }

    private void stop() {
        mc.options.useKey.setPressed(false);
        if (mc.player != null && prevSlot >= 0) mc.player.getInventory().selectedSlot = prevSlot;
        prevSlot = -1;
        eating = false;
    }

    private int findFood(ClientPlayerEntity p) {
        for (int i = 0; i < 9; i++) {
            if (isGood(p.getInventory().getStack(i))) return i;
        }
        return -1;
    }

    private boolean isGood(ItemStack s) {
        if (s.isEmpty() || !s.getItem().isFood() || s.isOf(Items.CHORUS_FRUIT)) return false;
        if (allowBad.get()) return true;
        FoodComponent fc = s.getItem().getFoodComponent();
        if (fc == null) return false;
        for (var e : fc.getStatusEffects()) {
            if (e.getFirst().getEffectType().getCategory() == StatusEffectCategory.HARMFUL) return false;
        }
        return true;
    }
}
