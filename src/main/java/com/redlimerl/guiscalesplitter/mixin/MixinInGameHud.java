package com.redlimerl.guiscalesplitter.mixin;

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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {

    @Shadow @Final private MinecraftClient client;

    @Unique
    private boolean isConfigScreen() {
        return this.client.currentScreen instanceof GuiScaleScreen;
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z"))
    public boolean keyPressed(KeyBinding instance, Operation<Boolean> original) {
        return original.call(instance) || this.isConfigScreen();
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;isInSingleplayer()Z"))
    public boolean modifySingleplay(MinecraftClient instance, Operation<Boolean> original) {
        return original.call(instance) && !FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;render(Lnet/minecraft/client/gui/DrawContext;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"))
    public void onPlayerListRender(PlayerListHud instance, DrawContext context, int scaledWindowWidth, Scoreboard scoreboard, ScoreboardObjective objective, Operation<Void> original) {
        float listScale = GuiScaleSplitter.getOption("playerListScale");
        context.getMatrices().push();
        context.getMatrices().scale(listScale, listScale, 1);
        original.call(instance, context, (int) (scaledWindowWidth / listScale), scoreboard, objective);
        context.getMatrices().pop();
    }

    @WrapOperation(method = "renderScoreboardSidebar", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/InGameHud;scaledHeight:I"))
    public int onStartScoreboardRenderHeight(InGameHud instance, Operation<Integer> original) {
        float listScale = GuiScaleSplitter.getOption("scoreboardScale");
        return (int) (original.call(instance) / listScale);
    }

    @WrapOperation(method = "renderScoreboardSidebar", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/InGameHud;scaledWidth:I"))
    public int onStartScoreboardRenderWidth(InGameHud instance, Operation<Integer> original) {
        float listScale = GuiScaleSplitter.getOption("scoreboardScale");
        return (int) (original.call(instance) / listScale);
    }

    @Unique
    private DrawContext lastDrawContext = null;
    @WrapOperation(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 0))
    public void onScoreboardFill(DrawContext instance, int x1, int y1, int x2, int y2, int color, Operation<Void> original) {
        float listScale = GuiScaleSplitter.getOption("scoreboardScale");
        float listOffset = GuiScaleSplitter.getOption("scoreboardOffset");
        lastDrawContext = instance;
        instance.getMatrices().push();
        instance.getMatrices().scale(listScale, listScale, 1);
        instance.getMatrices().translate(0, listOffset, 0);
        original.call(instance, x1, y1, x2, y2, color);
    }

    @WrapOperation(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I"))
    public int onScoreboardScore(DrawContext instance, TextRenderer textRenderer, String text, int x, int y, int color, boolean shadow, Operation<Integer> original) {
        boolean activate = GuiScaleSplitter.getOption("disableScoreboardScore") != 0;
        return activate ? instance.drawText(textRenderer, "", x, y, 0, shadow) : original.call(instance, textRenderer, text, x, y, color, shadow);
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("TAIL"))
    public void onScoreboardTail(CallbackInfo ci) {
        lastDrawContext.getMatrices().pop();
        lastDrawContext = null;
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", ordinal = 1))
    public void onTranslateRender(MatrixStack instance, float x, float y, float z, Operation<Void> original) {
        float listScale = GuiScaleSplitter.getOption("titleScale");
        instance.scale(listScale, listScale, 1);
        instance.translate(x / listScale, y / listScale, z);
    }

    @WrapOperation(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/InGameHud;title:Lnet/minecraft/text/Text;"))
    public Text onTitleModify(InGameHud instance, Operation<Text> original) {
        return this.isConfigScreen() ? GuiScaleScreen.EXAMPLE_TITLE : original.call(instance);
    }

    @WrapOperation(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/InGameHud;titleRemainTicks:I"))
    public int onTitleTimeModify(InGameHud instance, Operation<Integer> original) {
        return this.isConfigScreen() ? 100 : original.call(instance);
    }

    @WrapOperation(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/InGameHud;titleStayTicks:I"))
    public int onTitleStayTimeModify(InGameHud instance, Operation<Integer> original) {
        return this.isConfigScreen() ? 200 : original.call(instance);
    }

    @WrapOperation(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/InGameHud;subtitle:Lnet/minecraft/text/Text;"))
    public Text onSubtitleModify(InGameHud instance, Operation<Text> original) {
        return this.isConfigScreen() ? GuiScaleScreen.EXAMPLE_SUBTITLE : original.call(instance);
    }
}
