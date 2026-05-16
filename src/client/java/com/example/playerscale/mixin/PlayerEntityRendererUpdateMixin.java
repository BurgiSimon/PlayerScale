package com.example.playerscale.mixin;

import com.example.playerscale.ScaleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.PlayerLikeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererUpdateMixin {

    @Inject(method = "updateRenderState", at = @At("TAIL"))
    private void capturePlayerUuid(PlayerLikeEntity entity,
                                   PlayerEntityRenderState state,
                                   float tickDelta,
                                   CallbackInfo ci) {
        ScaleManager.mapEntityId(state.id, entity.getUuid());
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && entity.getUuid().equals(client.player.getUuid())) {
            ScaleManager.setLocalPlayerEntityId(state.id);
        }
    }
}
