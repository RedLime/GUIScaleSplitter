package com.redlimerl.guiscalesplitter;

import com.mojang.blaze3d.systems.RenderSystem;
import com.redlimerl.guiscalesplitter.mixin.InGameHudAccessor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.scoreboard.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.function.Supplier;

public class GuiScaleScreen extends Screen {

    public static final Text EXAMPLE_TITLE = Text.literal("Test Title Text");
    public static final Text EXAMPLE_SUBTITLE = Text.literal("Test Subtitle Text");
    private final Screen parent;
    private ButtonWidget saveButton;
    private boolean sliderMode = true;
    private boolean refreshing = false;
    private final List<ClickableWidget> sliders = Lists.newArrayList();
    private final List<ClickableWidget> fields = Lists.newArrayList();

    public GuiScaleScreen(Screen parent) {
        super(Text.literal("Gui Scale Splitter"));
        this.parent = parent;
    }

    @Override
    public void close() {
        GuiScaleSplitter.loadOptions();
        super.close();
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }

    @Override
    protected void init() {
        super.init();
        this.sliders.clear();
        this.fields.clear();
        int percentWidth = this.textRenderer.getWidth("%") + 2;

        this.saveButton = this.addDrawableChild(ButtonWidget.builder(Text.literal("Save"), button -> {
            GuiScaleSplitter.saveOptions();
            button.active = false;
        }).dimensions(this.width / 2 - 61, this.height - 38, 58, 20).build());
        this.saveButton.active = false;

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close())
                .dimensions(this.width / 2 + 1, this.height - 38, 58, 20).build());

        Supplier<Text> toggleSliderMode = () -> Text.literal(this.sliderMode ? "\uD83C\uDF9A" : "\uD83D\uDD8D");
        this.addDrawableChild(ButtonWidget.builder(toggleSliderMode.get(), button -> {
                    this.sliderMode = !this.sliderMode;
                    button.setMessage(toggleSliderMode.get());
                    this.updateSliderMode();
                }).dimensions(this.width / 2 - 85, this.height - 38, 20, 20).build());

        Supplier<Text> playerListScale = () -> Text.literal("Player List Scale : " + MathHelper.ceil(GuiScaleSplitter.getOption("playerListScale") * 100) + "%");
        this.sliders.add(this.addDrawableChild(new SliderWidget(this.width / 2 - 152, this.height - 82, 150, 20, playerListScale.get(), GuiScaleSplitter.getOption("playerListScale") / 2f) {
            @Override
            protected void updateMessage() {
                this.setMessage(playerListScale.get());
            }

            @Override
            protected void applyValue() {
                GuiScaleSplitter.setOption("playerListScale", Math.round((float) this.value * 2f * 20f)/20f);
                GuiScaleScreen.this.saveButton.active = true;
            }
        }));
        this.fields.add(this.addDrawableChild(new NumberFieldWidget(this.textRenderer, this.width / 2 - 32 - percentWidth, this.height - 82, 36 - percentWidth, 20, MathHelper.ceil(GuiScaleSplitter.getOption("playerListScale") * 100)) {
            @Override
            public void onUpdateValue(int value) {
                GuiScaleSplitter.setOption("playerListScale", value / 100f);
                GuiScaleScreen.this.saveButton.active = true;
            }

            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                super.renderWidget(context, mouseX, mouseY, delta);
                context.drawText(textRenderer, "Player List Scale", GuiScaleScreen.this.width / 2 - 152, this.getY() + 6, 0xFFFFFF, true);
                context.drawText(textRenderer, "%", this.getX() + this.getWidth() + 2, this.getY() + 6, 0xFFFFFF, true);
            }
        }));

        Supplier<Text> titleScale = () -> Text.literal("Title Text Scale : " + MathHelper.ceil(GuiScaleSplitter.getOption("titleScale") * 100) + "%");
        this.sliders.add(this.addDrawableChild(new SliderWidget(this.width / 2 + 2, this.height - 82, 150, 20, titleScale.get(), GuiScaleSplitter.getOption("titleScale") / 2f) {
            @Override
            protected void updateMessage() {
                this.setMessage(titleScale.get());
            }

            @Override
            protected void applyValue() {
                GuiScaleSplitter.setOption("titleScale", Math.round((float) this.value * 2f * 20f)/20f);
                GuiScaleScreen.this.saveButton.active = true;
            }
        }));
        this.fields.add(this.addDrawableChild(new NumberFieldWidget(this.textRenderer, this.width / 2 + 122 - percentWidth, this.height - 82, 36 - percentWidth, 20, MathHelper.ceil(GuiScaleSplitter.getOption("titleScale") * 100)) {
            @Override
            public void onUpdateValue(int value) {
                GuiScaleSplitter.setOption("titleScale", value / 100f);
                GuiScaleScreen.this.saveButton.active = true;
            }

            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                super.renderWidget(context, mouseX, mouseY, delta);
                context.drawText(textRenderer, "Title Text Scale", GuiScaleScreen.this.width / 2 + 2, this.getY() + 6, 0xFFFFFF, true);
                context.drawText(textRenderer, "%", this.getX() + this.getWidth() + 2, this.getY() + 6, 0xFFFFFF, true);
            }
        }));

        Supplier<Text> scoreboardScale = () -> Text.literal("Scoreboard Scale : " + MathHelper.ceil(GuiScaleSplitter.getOption("scoreboardScale") * 100) + "%");
        this.sliders.add(this.addDrawableChild(new SliderWidget(this.width / 2 - 152, this.height - 60, 150, 20, scoreboardScale.get(), GuiScaleSplitter.getOption("scoreboardScale") / 2f) {
            @Override
            protected void updateMessage() {
                this.setMessage(scoreboardScale.get());
            }

            @Override
            protected void applyValue() {
                GuiScaleSplitter.setOption("scoreboardScale", Math.round((float) this.value * 2f * 20f)/20f);
                GuiScaleScreen.this.saveButton.active = true;
            }
        }));
        this.fields.add(this.addDrawableChild(new NumberFieldWidget(this.textRenderer, this.width / 2 - 32 - percentWidth, this.height - 60, 36 - percentWidth, 20, MathHelper.ceil(GuiScaleSplitter.getOption("scoreboardScale") * 100)) {
            @Override
            public void onUpdateValue(int value) {
                GuiScaleSplitter.setOption("scoreboardScale", value / 100f);
                GuiScaleScreen.this.saveButton.active = true;
            }

            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                super.renderWidget(context, mouseX, mouseY, delta);
                context.drawText(textRenderer, "Scoreboard Scale", GuiScaleScreen.this.width / 2 - 152, this.getY() + 6, 0xFFFFFF, true);
                context.drawText(textRenderer, "%", this.getX() + this.getWidth() + 2, this.getY() + 6, 0xFFFFFF, true);
            }
        }));

        Supplier<Text> scoreboardOffset = () -> Text.literal("Scoreboard Y Offset : " + MathHelper.ceil(GuiScaleSplitter.getOption("scoreboardOffset", 0)));
        this.sliders.add(this.addDrawableChild(new SliderWidget(this.width / 2 + 2, this.height - 60, 150, 20, scoreboardOffset.get(), (GuiScaleSplitter.getOption("scoreboardOffset", 0) + 250) / 500) {
            @Override
            protected void updateMessage() {
                this.setMessage(scoreboardOffset.get());
            }

            @Override
            protected void applyValue() {
                GuiScaleSplitter.setOption("scoreboardOffset", Math.round(this.value * 500 - 250));
                GuiScaleScreen.this.saveButton.active = true;
            }
        }));
        this.fields.add(this.addDrawableChild(new NumberFieldWidget(this.textRenderer, this.width / 2 + 122, this.height - 60, 30, 20, (int) GuiScaleSplitter.getOption("scoreboardOffset")) {
            @Override
            public void onUpdateValue(int value) {
                GuiScaleSplitter.setOption("scoreboardOffset", value);
                GuiScaleScreen.this.saveButton.active = true;
            }

            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                super.renderWidget(context, mouseX, mouseY, delta);
                context.drawText(textRenderer, "Scoreboard Y Offset", GuiScaleScreen.this.width / 2 + 2, this.getY() + 6, 0xFFFFFF, true);
            }
        }));

        Supplier<Text> scoreboardScore = () -> Text.literal("Scoreboard Score : " + (GuiScaleSplitter.getOption("disableScoreboardScore") != 0 ? "Hide" : "Show"));
        this.addDrawableChild(ButtonWidget.builder(scoreboardScore.get(), button -> {
            GuiScaleSplitter.setOption("disableScoreboardScore", GuiScaleSplitter.getOption("disableScoreboardScore") != 0 ? 0 : 1);
            GuiScaleScreen.this.saveButton.active = true;
            button.setMessage(scoreboardScore.get());
        }).dimensions(this.width / 2 - 152, this.height - 104, 150, 20).build());

        if (!this.refreshing) this.updateSliderMode();
    }

    private void updateSliderMode() {
        this.refreshing = true;
        this.clearAndInit();
        this.refreshing = false;
        if (this.sliderMode) {
            this.sliders.forEach(slider -> slider.visible = true);
            this.fields.forEach(field -> field.visible = false);
        } else {
            this.sliders.forEach(slider -> slider.visible = false);
            this.fields.forEach(field -> field.visible = true);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (this.client != null && this.client.world == null) {
            context.getMatrices().push();
            float titleScale = GuiScaleSplitter.getOption("titleScale");
            context.getMatrices().scale(titleScale, titleScale, 1);
            context.getMatrices().translate((float) this.width / 2 / titleScale, (float) this.height / 2 / titleScale, 0);
            RenderSystem.enableBlend();
            context.getMatrices().push();
            context.getMatrices().scale(4.0F, 4.0F, 4.0F);
            int m = textRenderer.getWidth(EXAMPLE_TITLE);
            context.drawTextWithShadow(textRenderer, EXAMPLE_TITLE, -m / 2, -10, 0xFFFFFF);
            context.getMatrices().pop();
            context.getMatrices().push();
            context.getMatrices().scale(2.0F, 2.0F, 2.0F);
            int n = textRenderer.getWidth(EXAMPLE_SUBTITLE);
            context.drawTextWithShadow(textRenderer, EXAMPLE_SUBTITLE, -n / 2, 5, 0xFFFFFF);
            context.getMatrices().pop();

            RenderSystem.disableBlend();
            context.getMatrices().pop();

            context.getMatrices().push();
            Scoreboard scoreboard = new Scoreboard();
            ScoreboardObjective objective = scoreboard.addObjective("test", ScoreboardCriterion.DUMMY, Text.literal("Test Scoreboard"), ScoreboardCriterion.RenderType.INTEGER, false, null);
            ScoreAccess score = scoreboard.getOrCreateScore(ScoreHolder.fromName("test1"), objective);
            score.setScore(10);
            scoreboard.setObjectiveSlot(ScoreboardDisplaySlot.SIDEBAR, objective);
            ((InGameHudAccessor) this.client.inGameHud).invokeRenderScoreboardSidebar(context, objective);
            context.getMatrices().pop();

            context.getMatrices().push();
            float playerListScale = GuiScaleSplitter.getOption("playerListScale");
            context.getMatrices().scale(playerListScale, playerListScale, 1);
            this.client.inGameHud.getPlayerListHud().setVisible(true);
            this.client.inGameHud.getPlayerListHud().setHeader(Text.literal("  Some Header  "));
            this.client.inGameHud.getPlayerListHud().setFooter(Text.literal("  Some Footer  "));
            this.client.inGameHud.getPlayerListHud().render(context, (int) (context.getScaledWindowWidth() / playerListScale), scoreboard, null);
            context.getMatrices().pop();
        }
    }

    @Override
    public void renderInGameBackground(DrawContext context) {
        //super.renderInGameBackground(context);
    }

    @Override
    protected void applyBlur() {
//        super.applyBlur();
    }

    @Override
    protected void renderDarkening(DrawContext context, int x, int y, int width, int height) {
        //super.renderDarkening(context, x, y, width, height);
    }

    public abstract static class NumberFieldWidget extends TextFieldWidget {
        public NumberFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, int value) {
            super(textRenderer, x, y, width, height, Text.literal(String.valueOf(value)));
            this.setMaxLength(3);
            this.setText(String.valueOf(value));
            this.setTextPredicate(text -> {
                if (text.isEmpty() || text.equals("-")) return true;
                try {
                    Integer.parseInt(text);
                } catch (Exception e) {
                    return false;
                }
                return true;
            });

            this.setChangedListener(text -> {
                if (text.isEmpty() || text.equals("-")) this.onUpdateValue(0);
                else this.onUpdateValue(Integer.parseInt(text));
            });
        }

        public abstract void onUpdateValue(int value);
    }

}
