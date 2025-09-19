package com.redlimerl.guiscalesplitter.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.redlimerl.guiscalesplitter.GuiScaleSplitter;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DrawContext.class)
public abstract class MixinDrawContext {


    @ModifyReturnValue(method = "getScaledWindowWidth", at = @At("RETURN"))
    public int scaleWindowWidth(int original) {
        return (int) (original / GuiScaleSplitter.getOption(GuiScaleSplitter.CURRENT_RENDERING));
    }

    @ModifyReturnValue(method = "getScaledWindowHeight", at = @At("RETURN"))
    public int scaleWindowHeight(int original) {
        return (int) (original / GuiScaleSplitter.getOption(GuiScaleSplitter.CURRENT_RENDERING));
    }

}
