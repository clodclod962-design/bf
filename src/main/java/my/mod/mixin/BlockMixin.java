package my.mod.mixin;

import my.mod.module.impl.Xray;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Inject(method = "shouldDrawSide", at = @At("RETURN"), cancellable = true)
    private static void mymod$xray(BlockState state, BlockView world, BlockPos pos, Direction side,
                                   BlockPos otherPos, CallbackInfoReturnable<Boolean> cir) {
        if (!Xray.active || Xray.INSTANCE == null) return;
        cir.setReturnValue(Xray.INSTANCE.isVisible(state));
    }
}
