package my.mod.module;

import my.mod.module.impl.*;
import my.mod.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public void init() {
        // Combat
        register(new AutoTotem(GLFW.GLFW_KEY_Z));
        register(new AutoPearl(GLFW.GLFW_KEY_X));
        register(new KillAura(GLFW.GLFW_KEY_R));
        register(new Velocity(GLFW.GLFW_KEY_V));
        register(new AutoClicker(0));
        // Movement
        register(new Sprint(0));
        register(new Flight(GLFW.GLFW_KEY_G));
        register(new Speed(0));
        register(new NoFall(0));
        register(new Spider(0));
        register(new AutoWalk(0));
        register(new AutoJump(0));
        register(new AirJump(0));
        register(new Step(0));
        register(new Sneak(0));
        register(new Glide(0));
        // Player
        register(new FastPlace(0));
        register(new AutoRespawn(0));
        register(new NoHunger(0));
        register(new AutoEat(0));
        register(new AutoArmor(0));
        register(new AutoLog(0));
        register(new AutoHeal(0));
        // Render
        register(new Fullbright(GLFW.GLFW_KEY_B));
        register(new Esp(GLFW.GLFW_KEY_N));
        register(new StorageEsp(GLFW.GLFW_KEY_J));
        register(new Tracers(0));
        register(new Xray(GLFW.GLFW_KEY_H));
        register(new BlockOutline(0));
        register(new FovChanger(0));
        // Effects
        register(new ChinaHat(GLFW.GLFW_KEY_K));
        register(new Particles(0));
        register(new HitEffects(0));
        register(new Ambience(0));
        // Misc
        register(new AntiAfk(0));
        register(new CommandBinds(0));
        register(new AutoLogin(0));
        HudModule hud = new HudModule(0);
        register(hud);
        hud.setEnabled(true);
    }

    public void register(Module m) { modules.add(m); }

    public List<Module> getAll() { return modules; }

    public List<Module> byCategory(Category c) {
        List<Module> out = new ArrayList<>();
        for (Module m : modules) if (m.getCategory() == c) out.add(m);
        return out;
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T get(Class<T> clazz) {
        for (Module m : modules) if (m.getClass() == clazz) return (T) m;
        return null;
    }

    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        long window = mc.getWindow().getHandle();
        for (Module m : modules) {
            boolean down = mc.currentScreen == null && m.getKey() > 0
                    && InputUtil.isKeyPressed(window, m.getKey());
            if (down && !m.keyWasDown) m.toggle();
            m.keyWasDown = down;

            if (m.isEnabled()) m.onTick();
        }
    }

    public void onRender(MatrixStack ms, float tickDelta, Camera camera) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        boolean any = false;
        for (Module m : modules) if (m.isEnabled()) { any = true; break; }
        if (!any) return;

        Vec3d c = camera.getPos();
        RenderUtil.setup();
        ms.push();
        ms.translate(-c.x, -c.y, -c.z);
        for (Module m : modules) {
            if (m.isEnabled()) m.onRender(ms, tickDelta, camera);
        }
        ms.pop();
        RenderUtil.restore();
    }
}
