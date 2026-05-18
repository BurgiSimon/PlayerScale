package dev.solerbus.playerscale.mixin;

import dev.solerbus.playerscale.ScaleManager;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

    @Inject(method = "scale", at = @At("TAIL"))
    private void applyCustomScale(PlayerEntityRenderState state,
                                  MatrixStack matrices,
                                  CallbackInfo ci) {
        float scale = ScaleManager.getScaleByEntityId(state.id);
        if (scale != 1.0f) {
            matrices.scale(scale, scale, scale);
        }
    }
}
