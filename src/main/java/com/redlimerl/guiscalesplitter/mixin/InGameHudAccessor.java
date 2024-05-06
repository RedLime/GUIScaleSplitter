package com.redlimerl.guiscalesplitter.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(InGameHud.class)
public interface InGameHudAccessor {

    @Invoker("renderScoreboardSidebar")
    void invokeRenderScoreboardSidebar(DrawContext context, ScoreboardObjective objective);

}
