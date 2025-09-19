package com.redlimerl.guiscalesplitter.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.redlimerl.guiscalesplitter.GuiScaleSplitter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class MixinBossBarHud {

    /*

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;getScaledWindowWidth()I"))
    public int onBossBarWidth(DrawContext instance, Operation<Integer> original) {
        float barScale = GuiScaleSplitter.getOption("bossBarScale");
        return (int) (original.call(instance) / barScale);
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;createNewRootLayer()V"))
    public void onBossBarCreate(DrawContext instance, Operation<Void> original) {
        float barScale = GuiScaleSplitter.getOption("bossBarScale");
        instance.getMatrices().pushMatrix();
        instance.getMatrices().scale(barScale, barScale);
        original.call(instance);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void onBossBarTail(DrawContext context, CallbackInfo ci) {
        context.getMatrices().popMatrix();
    }

     */

}
