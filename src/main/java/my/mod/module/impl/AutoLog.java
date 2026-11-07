package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;
import my.mod.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public class AutoLog extends Module {
    private final NumberSetting health = num("Health", 6, 1, 20, 1);

    public AutoLog(int key) { super("AutoLog", Category.PLAYER, key); }

    @Override
    public void onTick() {
        ClientPlayerEntity p = mc.player;
        if (p == null || mc.getNetworkHandler() == null) return;
        if (p.getHealth() <= health.f()) {
            setEnabled(false);
            mc.getNetworkHandler().getConnection().disconnect(Text.literal("AutoLog: HP " + (int) p.getHealth()));
        }
    }
}
