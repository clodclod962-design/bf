package my.mod.module.impl;

import my.mod.module.Category;
import my.mod.module.Module;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.InputUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Команды на клавиши и по таймеру. Файл: .minecraft/config/mymod-commands.txt
 * Формат строк:
 *   KEY_R:/home            - по нажатию R отправить /home
 *   EVERY 300:/spawn       - каждые 300 секунд отправлять /spawn
 *   # комментарий
 * Названия клавиш как в GLFW без GLFW_ (KEY_R, KEY_F6, KEY_KP_1 ...).
 */
public class CommandBinds extends Module {
    private static class Bind { int key; boolean down; String cmd; }
    private static class Timer { int period; int left; String cmd; }

    private final List<Bind> binds = new ArrayList<>();
    private final List<Timer> timers = new ArrayList<>();

    public CommandBinds(int key) { super("CommandBinds", Category.MISC, key); }

    private Path file() { return FabricLoader.getInstance().getConfigDir().resolve("mymod-commands.txt"); }

    @Override
    protected void onEnable() {
        binds.clear();
        timers.clear();
        Path f = file();
        try {
            if (!Files.exists(f)) {
                Files.writeString(f, "# KEY_R:/home\n# EVERY 300:/spawn\n");
                return;
            }
            for (String line : Files.readAllLines(f)) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int i = line.indexOf(':');
                if (i < 0) continue;
                String left = line.substring(0, i).trim().toUpperCase(Locale.ROOT);
                String cmd = line.substring(i + 1).trim();
                if (cmd.isEmpty()) continue;
                if (left.startsWith("EVERY")) {
                    Timer t = new Timer();
                    t.period = Math.max(1, Integer.parseInt(left.substring(5).trim())) * 20;
                    t.left = t.period;
                    t.cmd = cmd;
                    timers.add(t);
                } else {
                    int code = keyCode(left);
                    if (code <= 0) continue;
                    Bind b = new Bind();
                    b.key = code;
                    b.cmd = cmd;
                    binds.add(b);
                }
            }
        } catch (Exception ignored) {}
    }

    private static int keyCode(String name) {
        try {
            var field = org.lwjgl.glfw.GLFW.class.getField("GLFW_" + name);
            return field.getInt(null);
        } catch (Exception e) { return -1; }
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        long w = mc.getWindow().getHandle();
        for (Bind b : binds) {
            boolean d = mc.currentScreen == null && InputUtil.isKeyPressed(w, b.key);
            if (d && !b.down) send(b.cmd);
            b.down = d;
        }
        for (Timer t : timers) {
            if (--t.left <= 0) { t.left = t.period; send(t.cmd); }
        }
    }

    private void send(String cmd) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        if (cmd.startsWith("/")) mc.getNetworkHandler().sendChatCommand(cmd.substring(1));
        else mc.getNetworkHandler().sendChatMessage(cmd);
    }
}
