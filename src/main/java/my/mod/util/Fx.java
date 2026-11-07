package my.mod.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

import java.util.concurrent.ThreadLocalRandom;

/** Список эффектов-частиц + хелперы. */
public enum Fx {
    FLAME(ParticleTypes.FLAME),
    SOUL_FLAME(ParticleTypes.SOUL_FIRE_FLAME),
    HEART(ParticleTypes.HEART),
    END_ROD(ParticleTypes.END_ROD),
    CRIT(ParticleTypes.CRIT),
    ENCHANT_HIT(ParticleTypes.ENCHANTED_HIT),
    TOTEM(ParticleTypes.TOTEM_OF_UNDYING),
    WITCH(ParticleTypes.WITCH),
    NOTE(ParticleTypes.NOTE),
    FIREWORK(ParticleTypes.FIREWORK),
    CLOUD(ParticleTypes.CLOUD),
    DRAGON(ParticleTypes.DRAGON_BREATH),
    GLOW(ParticleTypes.GLOW),
    ELECTRIC(ParticleTypes.ELECTRIC_SPARK),
    HAPPY(ParticleTypes.HAPPY_VILLAGER);

    public final ParticleEffect effect;

    Fx(ParticleEffect effect) { this.effect = effect; }

    public void spawn(double x, double y, double z, double vx, double vy, double vz) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world != null) mc.world.addParticle(effect, x, y, z, vx, vy, vz);
    }

    /** случайное число в диапазоне [-a, a] */
    public static double rnd(double a) {
        return (ThreadLocalRandom.current().nextDouble() * 2.0 - 1.0) * a;
    }

    public static double rnd01() { return ThreadLocalRandom.current().nextDouble(); }
}
