package com.redlimerl.guiscalesplitter.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.redlimerl.guiscalesplitter.GuiScaleScreen;
import com.redlimerl.guiscalesplitter.GuiScaleSplitter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {

    @Shadow @Final private MinecraftClient client;

    @Shadow
    public abstract void tick(boolean paused);

    @Unique
    private boolean isConfigScreen() {
        return this.client.currentScreen instanceof GuiScaleScreen;
    }

    /*

    @WrapOperation(method = "renderPlayerList", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z"))
    public boolean keyPressed(KeyBinding instance, Operation<Boolean> original) {
        return original.call(instance) || this.isConfigScreen();
    }

    @WrapOperation(method = "renderPlayerList", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;isInSingleplayer()Z"))
    public boolean modifySingleplay(MinecraftClient instance, Operation<Boolean> original) {
        return original.call(instance) && !FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @WrapOperation(method = "renderPlayerList", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;render(Lnet/minecraft/client/gui/DrawContext;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"))
    public void onPlayerListRender(PlayerListHud instance, DrawContext context, int scaledWindowWidth, Scoreboard scoreboard, ScoreboardObjective objective, Operation<Void> original) {
        float listScale = GuiScaleSplitter.getOption("playerListScale");
        context.getMatrices().pushMatrix();
        context.getMatrices().scale(listScale, listScale);
        original.call(instance, context, (int) (scaledWindowWidth / listScale), scoreboard, objective);
        context.getMatrices().popMatrix();
    }

    @WrapOperation(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;getScaledWindowHeight()I"))
    public int onStartScoreboardRenderHeight(DrawContext instance, Operation<Integer> original) {
        float listScale = GuiScaleSplitter.getOption("scoreboardScale");
        return (int) (original.call(instance) / listScale);
    }

    @WrapOperation(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;getScaledWindowWidth()I"))
    public int onStartScoreboardRenderWidth(DrawContext instance, Operation<Integer> original) {
        float listScale = GuiScaleSplitter.getOption("scoreboardScale");
        return (int) (original.call(instance) / listScale);
    }

    @WrapOperation(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 0))
    public void onScoreboardFill(DrawContext instance, int x1, int y1, int x2, int y2, int color, Operation<Void> original) {
        float listScale = GuiScaleSplitter.getOption("scoreboardScale");
        float listOffset = GuiScaleSplitter.getOption("scoreboardOffset");
        instance.getMatrices().pushMatrix();
        instance.getMatrices().scale(listScale, listScale);
        instance.getMatrices().translate(0, listOffset);
        original.call(instance, x1, y1, x2, y2, color);
    }

    @WrapOperation(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)V", ordinal = 2))
    public void onScoreboardScore(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow, Operation<Integer> original) {
        boolean activate = GuiScaleSplitter.getOption("disableScoreboardScore") != 0;
        if (!activate) {
            original.call(instance, textRenderer, text, x, y, color, shadow);
        }
    }

    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At("TAIL"))
    public void onScoreboardTail(DrawContext drawContext, ScoreboardObjective objective, CallbackInfo ci) {
        drawContext.getMatrices().popMatrix();
    }

    @WrapOperation(method = "renderTitleAndSubtitle", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3x2fStack;translate(FF)Lorg/joml/Matrix3x2f;", remap = false))
    public Matrix3x2f onTranslateRender(Matrix3x2fStack instance, float x, float y, Operation<Matrix3x2f> original) {
        float listScale = GuiScaleSplitter.getOption("titleScale");
        instance.scale(listScale, listScale);
        return instance.translate(x / listScale, y / listScale);
    }

    @WrapOperation(method = "renderTitleAndSubtitle", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/InGameHud;title:Lnet/minecraft/text/Text;"))
    public Text onTitleModify(InGameHud instance, Operation<Text> original) {
        return this.isConfigScreen() ? GuiScaleScreen.EXAMPLE_TITLE : original.call(instance);
    }

    @WrapOperation(method = "renderTitleAndSubtitle", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/InGameHud;titleRemainTicks:I"))
    public int onTitleTimeModify(InGameHud instance, Operation<Integer> original) {
        return this.isConfigScreen() ? 100 : original.call(instance);
    }

    @WrapOperation(method = "renderTitleAndSubtitle", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/InGameHud;titleStayTicks:I"))
    public int onTitleStayTimeModify(InGameHud instance, Operation<Integer> original) {
        return this.isConfigScreen() ? 200 : original.call(instance);
    }

    @WrapOperation(method = "renderTitleAndSubtitle", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/InGameHud;subtitle:Lnet/minecraft/text/Text;"))
    public Text onSubtitleModify(InGameHud instance, Operation<Text> original) {
        return this.isConfigScreen() ? GuiScaleScreen.EXAMPLE_SUBTITLE : original.call(instance);
    }

     */

    @WrapMethod(method = "renderBossBarHud")
    public void scaleBossBarHud(DrawContext context, RenderTickCounter tickCounter, Operation<Void> original) {
        GuiScaleSplitter.CURRENT_RENDERING = "bossBarScale";
        context.getMatrices().pushMatrix();
        context.getMatrices().scale(GuiScaleSplitter.getOption(GuiScaleSplitter.CURRENT_RENDERING));
        original.call(context, tickCounter);
        context.getMatrices().popMatrix();
        GuiScaleSplitter.CURRENT_RENDERING = "";
    }

    @WrapMethod(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V")
    public void scaleScoreboardSidebar(DrawContext context, RenderTickCounter tickCounter, Operation<Void> original) {
        GuiScaleSplitter.CURRENT_RENDERING = "scoreboardScale";
        context.getMatrices().pushMatrix();
        context.getMatrices().scale(GuiScaleSplitter.getOption(GuiScaleSplitter.CURRENT_RENDERING));
        original.call(context, tickCounter);
        context.getMatrices().popMatrix();
        GuiScaleSplitter.CURRENT_RENDERING = "";
    }

    @WrapMethod(method = "renderTitleAndSubtitle")
    public void scaleTitleAndSubtitle(DrawContext context, RenderTickCounter tickCounter, Operation<Void> original) {
        GuiScaleSplitter.CURRENT_RENDERING = "titleScale";
        float scale = GuiScaleSplitter.getOption(GuiScaleSplitter.CURRENT_RENDERING);
        context.getMatrices().pushMatrix();
        context.getMatrices().scale(scale);
        original.call(context, tickCounter);
        context.getMatrices().popMatrix();
        GuiScaleSplitter.CURRENT_RENDERING = "";
    }

    @WrapMethod(method = "renderPlayerList")
    public void scalePlayerList(DrawContext context, RenderTickCounter tickCounter, Operation<Void> original) {
        GuiScaleSplitter.CURRENT_RENDERING = "playerListScale";
        float scale = GuiScaleSplitter.getOption(GuiScaleSplitter.CURRENT_RENDERING);
        context.getMatrices().pushMatrix();
        context.getMatrices().scale(scale);
        original.call(context, tickCounter);
        context.getMatrices().popMatrix();
        GuiScaleSplitter.CURRENT_RENDERING = "";
    }

}
