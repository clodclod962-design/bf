package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;
import my.mod.module.setting.BoolSetting;
import my.mod.module.setting.NumberSetting;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Автоматически пишет /login (или /register) при входе на сервер.
 * Пароль берётся из файла .minecraft/config/mymod-login.txt (первая строка).
 * Файл хранится локально у вас, в мод/репозиторий пароль не попадает.
 */
public class AutoLogin extends Module {
    private final NumberSetting delay = num("DelaySec", 2, 1, 10, 1);
    private final BoolSetting register = bool("Register", false);

    private boolean wasInWorld;
    private int ticks = -1;

    public AutoLogin(int key) { super("AutoLogin", Category.MISC, key); }

    private Path file() { return FabricLoader.getInstance().getConfigDir().resolve("mymod-login.txt"); }

    @Override
    public void onTick() {
        boolean in = mc.player != null && mc.world != null;
        if (in && !wasInWorld) ticks = 0;
        wasInWorld = in;
        if (!in || ticks < 0) return;
        if (++ticks < delay.i() * 20) return;
        ticks = -1;

        try {
            Path f = file();
            if (!Files.exists(f)) { Files.writeString(f, "your_password_here\n"); return; }
            String pass = Files.readAllLines(f).get(0).trim();
            if (pass.isEmpty() || pass.equals("your_password_here") || mc.getNetworkHandler() == null) return;
            if (register.get()) mc.getNetworkHandler().sendChatCommand("register " + pass + " " + pass);
            else mc.getNetworkHandler().sendChatCommand("login " + pass);
        } catch (Exception ignored) {}
    }
}
