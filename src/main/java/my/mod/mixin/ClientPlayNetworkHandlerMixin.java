package my.mod.mixin;

import my.mod.MyMod;
import my.mod.module.impl.NoHunger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void mymod$send(Packet<?> packet, CallbackInfo ci) {
        if (MyMod.MODULES == null) return;
        NoHunger nh = MyMod.MODULES.get(NoHunger.class);
        if (nh == null || !nh.isEnabled()) return;

        if (nh.spoofSprint() && packet instanceof ClientCommandC2SPacket cmd
                && cmd.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING) {
            ci.cancel();
            return;
        }
        if (nh.spoofGround() && packet instanceof PlayerMoveC2SPacket move) {
            var p = MinecraftClient.getInstance().player;
            if (p != null && !p.isFallFlying()) ((PlayerMoveC2SPacketAccessor) move).mymod$setOnGround(true);
        }
    }
}
