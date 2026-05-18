package dev.solerbus.playerscale.screen;

import dev.solerbus.playerscale.PlayerScaleMod;
import dev.solerbus.playerscale.ScaleConfig;
import dev.solerbus.playerscale.ScaleManager;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class PlayerScaleScreen extends Screen {

    private static final double MIN_SCALE = 0.1;
    private static final double MAX_SCALE = 10.0;
    private static final float[] PRESETS = {0.5f, 1.0f, 2.0f, 5.0f};
    private static final String[] PRESET_LABELS = {"Tiny", "Normal", "Large", "Giant"};

    private final Screen parent;
    private ScaleSlider selfSlider;
    private ScaleSlider othersSlider;
    private ButtonWidget keybindButton;
    private boolean listeningForKey;

    public PlayerScaleScreen(Screen parent) {
        super(Text.literal("PlayerScale Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int y = this.height / 2 - 85;

        selfSlider = new ScaleSlider(centerX - 100, y, 200, 20,
                "My Size", ScaleManager.getSelfScale(), ScaleManager::setSelfScale);
        addDrawableChild(selfSlider);

        addPresetButtons(centerX - 100, y + 22, selfSlider);

        othersSlider = new ScaleSlider(centerX - 100, y + 52, 200, 20,
                "Others Size", ScaleManager.getOthersScale(), ScaleManager::setOthersScale);
        addDrawableChild(othersSlider);

        addPresetButtons(centerX - 100, y + 74, othersSlider);

        keybindButton = ButtonWidget.builder(getKeybindText(), button -> {
            listeningForKey = true;
            button.setMessage(Text.literal("Open Key: ").append(Text.literal("> ... <").formatted(Formatting.YELLOW)));
        }).dimensions(centerX - 100, y + 104, 200, 20).build();
        addDrawableChild(keybindButton);

        addDrawableChild(ButtonWidget.builder(getCrosshairToggleText(), button -> {
            ScaleManager.setShowCrosshairInThirdPerson(!ScaleManager.isShowCrosshairInThirdPerson());
            button.setMessage(getCrosshairToggleText());
        }).dimensions(centerX - 100, y + 134, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Reset All"), button -> {
            ScaleManager.resetAll();
            selfSlider.setScaleValue(1.0f);
            othersSlider.setScaleValue(1.0f);
            ScaleConfig.save();
            clearAndInit();
        }).dimensions(centerX - 100, y + 169, 95, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> {
            close();
        }).dimensions(centerX + 5, y + 169, 95, 20).build());
    }

    private void addPresetButtons(int x, int y, ScaleSlider targetSlider) {
        int buttonWidth = 47;
        int gap = 2;
        for (int i = 0; i < PRESETS.length; i++) {
            float preset = PRESETS[i];
            addDrawableChild(ButtonWidget.builder(Text.literal(PRESET_LABELS[i]), button -> {
                targetSlider.setScaleValue(preset);
            }).dimensions(x + i * (buttonWidth + gap), y, buttonWidth, 20).build());
        }
    }

    private Text getCrosshairToggleText() {
        boolean on = ScaleManager.isShowCrosshairInThirdPerson();
        return Text.literal("3rd Person Crosshair: ").append(
                on ? Text.literal("ON").formatted(Formatting.GREEN)
                   : Text.literal("OFF").formatted(Formatting.GRAY));
    }

    private Text getKeybindText() {
        KeyBinding key = PlayerScaleMod.openConfigKey;
        if (key.isUnbound()) {
            return Text.literal("Open Key: ").append(Text.literal("[NOT SET]").formatted(Formatting.GRAY));
        }
        return Text.literal("Open Key: ").append(key.getBoundKeyLocalizedText().copy().formatted(Formatting.AQUA));
    }

    @Override
    public void close() {
        ScaleConfig.save();
        if (client != null) client.setScreen(parent);
    }

    @Override
    public boolean keyPressed(KeyInput keyInput) {
        if (listeningForKey) {
            listeningForKey = false;
            if (keyInput.key() == InputUtil.GLFW_KEY_ESCAPE) {
                keybindButton.setMessage(getKeybindText());
                return true;
            }
            PlayerScaleMod.openConfigKey.setBoundKey(InputUtil.Type.KEYSYM.createFromCode(keyInput.key()));
            KeyBinding.updateKeysByCode();
            keybindButton.setMessage(getKeybindText());
            return true;
        }
        return super.keyPressed(keyInput);
    }

    @Override
    public boolean mouseClicked(Click click, boolean occupied) {
        if (listeningForKey) {
            listeningForKey = false;
            PlayerScaleMod.openConfigKey.setBoundKey(InputUtil.Type.MOUSE.createFromCode(click.button()));
            KeyBinding.updateKeysByCode();
            keybindButton.setMessage(getKeybindText());
            return true;
        }
        return super.mouseClicked(click, occupied);
    }

    static class ScaleSlider extends SliderWidget {
        private final String label;
        private final java.util.function.Consumer<Float> onChange;

        ScaleSlider(int x, int y, int width, int height, String label, float initialScale,
                    java.util.function.Consumer<Float> onChange) {
            super(x, y, width, height, Text.empty(), scaleToSlider(initialScale));
            this.label = label;
            this.onChange = onChange;
            updateMessage();
        }

        float getScaleValue() {
            return sliderToScale(this.value);
        }

        void setScaleValue(float scale) {
            this.value = scaleToSlider(scale);
            updateMessage();
            onChange.accept(scale);
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.literal(label + ": " + String.format("%.1f", getScaleValue())));
        }

        @Override
        protected void applyValue() {
            onChange.accept(getScaleValue());
        }

        static double scaleToSlider(float scale) {
            return (scale - MIN_SCALE) / (MAX_SCALE - MIN_SCALE);
        }

        static float sliderToScale(double slider) {
            return (float) (MIN_SCALE + slider * (MAX_SCALE - MIN_SCALE));
        }
    }
}
