package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;
import my.mod.module.setting.BoolSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

/**
 * Без голода.
 * - SingleplayerFill: в одиночной игре держит еду на 20 (полностью работает).
 * - SpoofSprint / SpoofGround: на серверах не даёт серверу считать бег и прыжки (меньше голода).
 * - FullBar: клиент всегда показывает полную шкалу (и позволяет бежать).
 */
public class NoHunger extends Module {
    private final BoolSetting singleplayerFill = bool("SingleplayerFill", true);
    private final BoolSetting spoofSprint = bool("SpoofSprint", true);
    private final BoolSetting spoofGround = bool("SpoofGround", true);
    private final BoolSetting fullBar = bool("FullBar", true);

    public NoHunger(int key) { super("NoHunger", Category.PLAYER, key); }

    public boolean spoofSprint() { return spoofSprint.get(); }
    public boolean spoofGround() { return spoofGround.get(); }

    @Override
    public void onTick() {
        ClientPlayerEntity p = mc.player;
        if (p == null) return;

        if (fullBar.get()) p.getHungerManager().setFoodLevel(20);

        MinecraftServer srv = mc.getServer();
        if (singleplayerFill.get() && mc.isInSingleplayer() && srv != null) {
            UUID id = p.getUuid();
            srv.execute(() -> {
                ServerPlayerEntity sp = srv.getPlayerManager().getPlayer(id);
                if (sp != null) sp.getHungerManager().setFoodLevel(20);
            });
        }
    }
}
