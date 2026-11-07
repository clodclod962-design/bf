package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class AutoArmor extends Module {
    private int cooldown;

    public AutoArmor(int key) { super("AutoArmor", Category.PLAYER, key); }

    @Override
    public void onTick() {
        ClientPlayerEntity p = mc.player;
        if (p == null || mc.interactionManager == null || mc.currentScreen != null) return;
        if (cooldown-- > 0) return;
        // Слоты инвентаря в PlayerScreenHandler: 9..44 (основной + хотбар)
        for (int slot = 9; slot <= 44; slot++) {
            ItemStack st = p.playerScreenHandler.getSlot(slot).getStack();
            if (!(st.getItem() instanceof ArmorItem armor)) continue;
            if (!p.getEquippedStack(armor.getSlotType()).isEmpty()) continue;
            mc.interactionManager.clickSlot(p.playerScreenHandler.syncId, slot, 0,
                    SlotActionType.QUICK_MOVE, p);
            cooldown = 4;
            return;
        }
    }
}
