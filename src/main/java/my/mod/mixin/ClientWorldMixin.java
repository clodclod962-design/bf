package my.mod.mixin;

import my.mod.MyMod;
import my.mod.module.impl.Ambience;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
    private Ambience mymod$ambience() {
        if (MyMod.MODULES == null) return null;
        Ambience a = MyMod.MODULES.get(Ambience.class);
        return (a != null && a.isEnabled()) ? a : null;
    }

    @Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true)
    private void mymod$sky(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        Ambience a = mymod$ambience();
        if (a != null && a.skyActive()) cir.setReturnValue(a.skyColor());
    }

    @Inject(method = "getCloudsColor", at = @At("RETURN"), cancellable = true)
    private void mymod$clouds(float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        Ambience a = mymod$ambience();
        if (a != null && a.cloudsActive()) cir.setReturnValue(a.skyColor());
    }
}
