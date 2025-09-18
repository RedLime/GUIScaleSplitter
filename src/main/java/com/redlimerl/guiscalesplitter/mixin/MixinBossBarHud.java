package com.redlimerl.guiscalesplitter.mixin;

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

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;getScaledWindowWidth()I"))
    public int onBossBarWidth(DrawContext context, Operation<Integer> original) {
        float listScale = GuiScaleSplitter.getOption("playerListScale");
        return (int) (original.call(context) / listScale);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void onBossBarHead(DrawContext context, CallbackInfo ci) {
        float listScale = GuiScaleSplitter.getOption("playerListScale");
        context.getMatrices().pushMatrix();
        context.getMatrices().scale(listScale, listScale);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void onBossBarTail(DrawContext context, CallbackInfo ci) {
        context.getMatrices().popMatrix();
    }

}
