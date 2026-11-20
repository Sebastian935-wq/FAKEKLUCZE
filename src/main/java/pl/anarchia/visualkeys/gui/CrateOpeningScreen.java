package pl.anarchia.visualkeys.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import pl.anarchia.visualkeys.model.KeyType;
import pl.anarchia.visualkeys.util.CrateRewardPool;
import pl.anarchia.visualkeys.util.CrateRewardPool.CrateReward;
import pl.anarchia.visualkeys.util.KeyItemFactory;

import java.util.ArrayList;
import java.util.List;

public class CrateOpeningScreen extends Screen {

    private final KeyType keyType;
    private final Hand hand;

    private final List<CrateReward> reelItems = new ArrayList<>();
    private static final int TOTAL_REEL_ITEMS = 65;
    private static final int WINNING_INDEX = 54;
    private static final int SLOT_SIZE = 36;
    private static final int VISIBLE_SLOTS = 9;

    private double currentOffset = 0.0;
    private double targetOffset = 0.0;
    private int lastTickIndex = -1;
    private boolean finished = false;
    private int finishCooldownTicks = 0;
    private boolean keyConsumed = false;

    public CrateOpeningScreen(KeyType keyType, Hand hand) {
        super(Text.literal("Otwieranie skrzyni..."));
        this.keyType = keyType;
        this.hand = hand;
    }

    @Override
    protected void init() {
        if (this.client == null || this.client.world == null || this.client.player == null) return;

        // Zużycie 1 klucza z ręki gracza przy starcie losowania
        if (!keyConsumed) {
            consumeOneKey();
            keyConsumed = true;
        }

        if (reelItems.isEmpty()) {
            List<CrateReward> pool = CrateRewardPool.getRewardsForKey(keyType, this.client.world.getRegistryManager());
            for (int i = 0; i < TOTAL_REEL_ITEMS; i++) {
                reelItems.add(CrateRewardPool.getRandomReward(pool));
            }
            // Wyliczenie docelowego przesunięcia tak, aby WINNING_INDEX zatrzymał się idealnie pod środkowym wskaźnikiem (slot nr 4 z 0..8)
            double randomJitter = (Math.random() - 0.5) * (SLOT_SIZE * 0.45);
            this.targetOffset = (WINNING_INDEX - 4) * SLOT_SIZE + randomJitter;
            this.currentOffset = 0.0;
        }
    }

    private void consumeOneKey() {
        if (this.client == null || this.client.player == null) return;
        ItemStack held = this.client.player.getStackInHand(hand);
        if (KeyItemFactory.identifyKey(held) == keyType) {
            held.decrement(1);
            if (this.client.player.getAbilities().creativeMode && this.client.getNetworkHandler() != null) {
                int slot = (hand == Hand.MAIN_HAND) ? (this.client.player.getInventory().selectedSlot + 36) : 45;
                this.client.getNetworkHandler().sendPacket(
                        new net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket(slot, held.copy())
                );
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.client == null || this.client.player == null) return;

        if (!finished) {
            double remaining = targetOffset - currentOffset;
            if (remaining <= 0.35) {
                currentOffset = targetOffset;
                finished = true;
                onCrateFinished();
            } else {
                // Płynne zwalnianie ruletki
                double speed = Math.max(0.45, remaining * 0.085);
                currentOffset += speed;

                int currentCenterIndex = (int) Math.round(currentOffset / SLOT_SIZE) + 4;
                if (currentCenterIndex != lastTickIndex) {
                    lastTickIndex = currentCenterIndex;
                    float pitch = (float) Math.max(0.65, 1.45 - (currentOffset / targetOffset) * 0.75);
                    this.client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), 0.75f, pitch);
                }
            }
        } else {
            finishCooldownTicks++;
            if (finishCooldownTicks >= 45) {
                this.close();
            }
        }
    }

    private void onCrateFinished() {
        if (this.client == null || this.client.player == null) return;

        CrateReward wonReward = reelItems.get(WINNING_INDEX);

        // Fanfary wygranej
        this.client.player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.15f);
        this.client.player.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.85f, 1.0f);

        // Dodanie nagrody lokalnie do ekwipunku gracza
        KeyItemFactory.giveClientSideItem(this.client, wonReward.displayStack());

        // Realistyczny komunikat na czacie w stylu Anarchia.gg
        String playerName = this.client.player.getName().getString();
        Text broadcastMsg = Text.literal("[").formatted(Formatting.DARK_GRAY)
                .append(Text.literal("ANARCHIA.GG").formatted(Formatting.GOLD, Formatting.BOLD))
                .append(Text.literal("] ").formatted(Formatting.DARK_GRAY))
                .append(Text.literal("Gracz ").formatted(Formatting.WHITE))
                .append(Text.literal(playerName).formatted(Formatting.YELLOW))
                .append(Text.literal(" otworzył ").formatted(Formatting.WHITE))
                .append(keyType.getFormattedTitle())
                .append(Text.literal(" i wygrał: ").formatted(Formatting.WHITE))
                .append(wonReward.chatDisplayName())
                .append(Text.literal("!").formatted(Formatting.WHITE));

        this.client.player.sendMessage(broadcastMsg, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int stripWidth = VISIBLE_SLOTS * SLOT_SIZE;
        int boxWidth = stripWidth + 24;
        int boxHeight = 135;

        int boxX = centerX - boxWidth / 2;
        int boxY = centerY - boxHeight / 2;

        // Tło głównego okna ruletki
        context.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xEC101014);

        // Ramka w kolorze otwieranego klucza
        int frameColor = 0xFF000000 | keyType.getPrimaryColorHex();
        context.fill(boxX, boxY, boxX + boxWidth, boxY + 2, frameColor);
        context.fill(boxX, boxY + boxHeight - 2, boxX + boxWidth, boxY + boxHeight, frameColor);
        context.fill(boxX, boxY, boxX + 2, boxY + boxHeight, frameColor);
        context.fill(boxX + boxWidth - 2, boxY, boxX + boxWidth, boxY + boxHeight, frameColor);

        // Nagłówek
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("OTWIERANIE: ").formatted(Formatting.GRAY, Formatting.BOLD)
                        .append(keyType.getFormattedTitle()),
                centerX,
                boxY + 12,
                0xFFFFFF
        );

        // Pasek 9 widocznych slotów ruletki
        int stripX = centerX - stripWidth / 2;
        int stripY = boxY + 42;

        context.fill(stripX - 2, stripY - 2, stripX + stripWidth + 2, stripY + SLOT_SIZE + 2, 0xFF08080A);

        // Przycięcie (scissor) do obszaru 9 slotów
        context.enableScissor(stripX, stripY, stripX + stripWidth, stripY + SLOT_SIZE);

        for (int i = 0; i < reelItems.size(); i++) {
            double itemX = stripX + (i * SLOT_SIZE) - currentOffset;
            if (itemX + SLOT_SIZE < stripX || itemX > stripX + stripWidth) {
                continue;
            }

            CrateReward reward = reelItems.get(i);
            int ix = (int) Math.round(itemX);

            // Tło pojedynczego kafelka nagrody
            context.fill(ix + 1, stripY + 1, ix + SLOT_SIZE - 1, stripY + SLOT_SIZE - 1, 0xFF1C1C24);
            // Pasek rzadkości na dole kafelka
            context.fill(ix + 2, stripY + SLOT_SIZE - 4, ix + SLOT_SIZE - 2, stripY + SLOT_SIZE - 2, 0xFF000000 | reward.rarityColor());

            // Renderowanie ikony przedmiotu
            context.drawItem(reward.displayStack(), ix + (SLOT_SIZE / 2) - 8, stripY + (SLOT_SIZE / 2) - 9);
            context.drawItemInSlot(this.textRenderer, reward.displayStack(), ix + (SLOT_SIZE / 2) - 8, stripY + (SLOT_SIZE / 2) - 9);
        }

        context.disableScissor();

        // Środkowy wskaźnik (czerwono-złota linia + trójkąty góra/dół)
        int pointerX = centerX;
        context.fill(pointerX - 1, stripY - 6, pointerX + 1, stripY + SLOT_SIZE + 6, 0xFFFF3333);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("▼").formatted(Formatting.RED, Formatting.BOLD), pointerX, stripY - 13, 0xFF3333);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("▲").formatted(Formatting.RED, Formatting.BOLD), pointerX, stripY + SLOT_SIZE + 5, 0xFF3333);

        // Dolny pasek statusu / nazwa wylosowanego przedmiotu
        if (finished) {
            CrateReward won = reelItems.get(WINNING_INDEX);
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    Text.literal("WYGRANA: ").formatted(Formatting.GREEN, Formatting.BOLD)
                            .append(won.chatDisplayName()),
                    centerX,
                    boxY + 104,
                    0xFFFFFF
            );
        } else {
            int currentIdx = Math.min(reelItems.size() - 1, Math.max(0, (int) Math.round(currentOffset / SLOT_SIZE) + 4));
            CrateReward hovering = reelItems.get(currentIdx);
            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    hovering.chatDisplayName(),
                    centerX,
                    boxY + 104,
                    0xFFFFFF
            );
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
