package pl.anarchia.visualkeys.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import pl.anarchia.visualkeys.model.KeyType;
import pl.anarchia.visualkeys.util.KeyItemFactory;

public class KeyGeneratorScreen extends Screen {

    private KeyType selectedKeyType = KeyType.ANARCHICZNY;
    private int selectedAmount = 1;
    private final int[] amountOptions = {1, 8, 16, 64};

    private static final int GUI_WIDTH = 320;
    private static final int GUI_HEIGHT = 230;

    public KeyGeneratorScreen() {
        super(Text.literal("ANARCHIA.GG - Generator Kluczy"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int startX = centerX - GUI_WIDTH / 2;
        int startY = centerY - GUI_HEIGHT / 2;

        // Przyciski wyboru rodzaju klucza (3 karty)
        KeyType[] types = KeyType.values();
        int cardWidth = 92;
        int cardSpacing = 10;
        int cardsStartX = centerX - ((cardWidth * 3 + cardSpacing * 2) / 2);
        int cardsY = startY + 46;

        for (int i = 0; i < types.length; i++) {
            final KeyType type = types[i];
            int btnX = cardsStartX + i * (cardWidth + cardSpacing);
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal(type.name()).formatted(type.getPrimaryFormatting(), Formatting.BOLD),
                    button -> {
                        this.selectedKeyType = type;
                        playClickSound();
                    }
            ).dimensions(btnX, cardsY + 48, cardWidth, 20).build());
        }

        // Przyciski wyboru ilości: 1x, 8x, 16x, 64x
        int amtBtnWidth = 58;
        int amtSpacing = 10;
        int amtStartX = centerX - ((amtBtnWidth * 4 + amtSpacing * 3) / 2);
        int amtY = startY + 135;

        for (int i = 0; i < amountOptions.length; i++) {
            final int amt = amountOptions[i];
            int btnX = amtStartX + i * (amtBtnWidth + amtSpacing);
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal(amt + "x").formatted(Formatting.WHITE, Formatting.BOLD),
                    button -> {
                        this.selectedAmount = amt;
                        playClickSound();
                    }
            ).dimensions(btnX, amtY, amtBtnWidth, 22).build());
        }

        // Główny przycisk "Odbierz klucze"
        int claimWidth = 200;
        int claimX = centerX - claimWidth / 2;
        int claimY = startY + 182;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("✔ ODBIERZ KLUCZE").formatted(Formatting.GREEN, Formatting.BOLD),
                button -> claimKeys()
        ).dimensions(claimX, claimY, claimWidth, 26).build());
    }

    private void claimKeys() {
        if (this.client == null || this.client.player == null) return;

        ItemStack generatedKeys = KeyItemFactory.createKey(selectedKeyType, selectedAmount);
        KeyItemFactory.giveClientSideItem(this.client, generatedKeys);

        this.client.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f, 1.2f);
        this.client.player.sendMessage(
                Text.literal("[").formatted(Formatting.DARK_GRAY)
                        .append(Text.literal("ANARCHIA.GG").formatted(Formatting.GOLD, Formatting.BOLD))
                        .append(Text.literal("] ").formatted(Formatting.DARK_GRAY))
                        .append(Text.literal("Odebrano ").formatted(Formatting.GRAY))
                        .append(Text.literal(selectedAmount + "x ").formatted(Formatting.YELLOW, Formatting.BOLD))
                        .append(selectedKeyType.getFormattedTitle())
                        .append(Text.literal("!").formatted(Formatting.GRAY)),
                false
        );
        this.close();
    }

    private void playClickSound() {
        if (this.client != null && this.client.player != null) {
            this.client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.6f, 1.0f);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int startX = centerX - GUI_WIDTH / 2;
        int startY = centerY - GUI_HEIGHT / 2;

        // Ciemne tło panelu w stylu Anarchia.gg
        context.fill(startX, startY, startX + GUI_WIDTH, startY + GUI_HEIGHT, 0xE6121216);

        // Zewnętrzna złoto-pomarańczowa ramka
        int borderColor = 0xFFFFAA00;
        context.fill(startX, startY, startX + GUI_WIDTH, startY + 2, borderColor);
        context.fill(startX, startY + GUI_HEIGHT - 2, startX + GUI_WIDTH, startY + GUI_HEIGHT, borderColor);
        context.fill(startX, startY, startX + 2, startY + GUI_HEIGHT, borderColor);
        context.fill(startX + GUI_WIDTH - 2, startY, startX + GUI_WIDTH, startY + GUI_HEIGHT, borderColor);

        // Pasek nagłówka
        context.fill(startX + 2, startY + 2, startX + GUI_WIDTH - 2, startY + 30, 0xFF1E1E26);
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("ANARCHIA.GG ").formatted(Formatting.GOLD, Formatting.BOLD)
                        .append(Text.literal("» ").formatted(Formatting.DARK_GRAY))
                        .append(Text.literal("GENERATOR KLUCZY").formatted(Formatting.WHITE, Formatting.BOLD)),
                centerX,
                startY + 12,
                0xFFFFFF
        );

        // Karty z podglądem 3 kluczy
        KeyType[] types = KeyType.values();
        int cardWidth = 92;
        int cardSpacing = 10;
        int cardsStartX = centerX - ((cardWidth * 3 + cardSpacing * 2) / 2);
        int cardsY = startY + 42;

        for (int i = 0; i < types.length; i++) {
            KeyType type = types[i];
            int cx = cardsStartX + i * (cardWidth + cardSpacing);
            boolean isSelected = (type == selectedKeyType);

            int bg = isSelected ? 0xFF2A2438 : 0xFF1A1A22;
            int outline = isSelected ? (0xFF000000 | type.getPrimaryColorHex()) : 0xFF3A3A48;

            context.fill(cx, cardsY, cx + cardWidth, cardsY + 46, bg);
            // Ramka karty
            context.fill(cx, cardsY, cx + cardWidth, cardsY + 1, outline);
            context.fill(cx, cardsY + 45, cx + cardWidth, cardsY + 46, outline);
            context.fill(cx, cardsY, cx + 1, cardsY + 46, outline);
            context.fill(cx + cardWidth - 1, cardsY, cx + cardWidth, cardsY + 46, outline);

            // Renderowanie przedmiotu klucza z blaskiem zaklęcia
            ItemStack previewStack = KeyItemFactory.createKey(type, 1);
            context.drawItem(previewStack, cx + (cardWidth / 2) - 8, cardsY + 10);

            String label = isSelected ? "★ WYBRANO ★" : "KLIKNIJ NIŻEJ";
            int labelColor = isSelected ? type.getPrimaryColorHex() : 0x888888;
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(label), cx + cardWidth / 2, cardsY + 32, labelColor);
        }

        // Etykieta sekcji ilości
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("Wybierz ilość kluczy: ").formatted(Formatting.GRAY)
                        .append(Text.literal(selectedAmount + "x").formatted(Formatting.YELLOW, Formatting.BOLD)),
                centerX,
                startY + 120,
                0xFFFFFF
        );

        // Podświetlenie aktywnego przycisku ilości
        int amtBtnWidth = 58;
        int amtSpacing = 10;
        int amtStartX = centerX - ((amtBtnWidth * 4 + amtSpacing * 3) / 2);
        int amtY = startY + 135;
        for (int i = 0; i < amountOptions.length; i++) {
            if (amountOptions[i] == selectedAmount) {
                int bx = amtStartX + i * (amtBtnWidth + amtSpacing);
                context.fill(bx - 2, amtY - 2, bx + amtBtnWidth + 2, amtY + 24, 0xFFFFAA00);
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
