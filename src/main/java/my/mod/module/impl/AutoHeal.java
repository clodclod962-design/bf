package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;
import my.mod.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/** Когда здоровье ниже порога, берёт из хотбара золотое яблоко и ест его. */
public class AutoHeal extends Module {
    private final NumberSetting health = num("Health", 10, 1, 19, 1);

    private boolean using;
    private int prevSlot = -1;
    private int timeout;

    public AutoHeal(int key) { super("AutoHeal", Category.PLAYER, key); }

    @Override
    protected void onDisable() { if (using) stop(); }

    @Override
    public void onTick() {
        ClientPlayerEntity p = mc.player;
        if (p == null) return;
        if (mc.currentScreen != null) { if (using) stop(); return; }

        if (using) {
            // ест до тех пор, пока предмет используется (максимум ~3 сек.)
            if (++timeout > 60 || !isHealItem(p.getMainHandStack())) { stop(); return; }
            mc.options.useKey.setPressed(true);
            return;
        }

        if (p.getHealth() > health.f() || p.isUsingItem()) return;
        int slot = find(p);
        if (slot < 0) return;
        prevSlot = p.getInventory().selectedSlot;
        p.getInventory().selectedSlot = slot;
        using = true;
        timeout = 0;
        mc.options.useKey.setPressed(true);
    }

    private void stop() {
        mc.options.useKey.setPressed(false);
        if (mc.player != null && prevSlot >= 0) mc.player.getInventory().selectedSlot = prevSlot;
        prevSlot = -1;
        using = false;
    }

    private int find(ClientPlayerEntity p) {
        for (int i = 0; i < 9; i++) if (isHealItem(p.getInventory().getStack(i))) return i;
        return -1;
    }

    private boolean isHealItem(ItemStack s) {
        return s.isOf(Items.GOLDEN_APPLE) || s.isOf(Items.ENCHANTED_GOLDEN_APPLE);
    }
}
