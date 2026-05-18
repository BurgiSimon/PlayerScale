package dev.solerbus.playerscale.mixin;

import dev.solerbus.playerscale.ScaleManager;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.Perspective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Redirect(
            method = "renderCrosshair",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/Perspective;isFirstPerson()Z")
    )
    private boolean forceFirstPersonForCrosshair(Perspective perspective) {
        if (ScaleManager.isShowCrosshairInThirdPerson()) {
            return true;
        }
        return perspective.isFirstPerson();
    }
}
