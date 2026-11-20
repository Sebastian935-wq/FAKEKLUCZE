package pl.anarchia.visualkeys.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import pl.anarchia.visualkeys.model.KeyType;

import java.util.List;

public class KeyItemFactory {

    private static final String CUSTOM_KEY_TAG = "anarchia_visual_key_type";

    public static ItemStack createKey(KeyType type, int count) {
        ItemStack stack = new ItemStack(Items.TRIPWIRE_HOOK, Math.max(1, Math.min(64, count)));

        // Niestandardowa nazwa bez kursywy
        Text customName = type.getFormattedTitle().copy()
                .setStyle(type.getFormattedTitle().getStyle().withItalic(false));
        stack.set(DataComponentTypes.CUSTOM_NAME, customName);

        // Profesjonalne Lore w stylu Anarchia.gg
        List<Text> loreLines = List.of(
                Text.empty(),
                Text.literal("» ").formatted(Formatting.DARK_GRAY)
                        .append(Text.literal("Klucz do ").formatted(Formatting.GRAY))
                        .append(Text.literal(type.getCrateDisplayName()).formatted(type.getPrimaryFormatting(), Formatting.BOLD))
                        .setStyle(Style.EMPTY.withItalic(false)),
                Text.literal("» ").formatted(Formatting.DARK_GRAY)
                        .append(Text.literal("Użyj na ").formatted(Formatting.GRAY))
                        .append(Text.literal("/warp skrzynie").formatted(Formatting.YELLOW))
                        .setStyle(Style.EMPTY.withItalic(false)),
                Text.empty(),
                Text.literal("Kliknij PPM, aby otworzyć skrzynię!").formatted(Formatting.GREEN)
                        .setStyle(Style.EMPTY.withItalic(false))
        );
        stack.set(DataComponentTypes.LORE, new LoreComponent(loreLines));

        // Wymuszony blask zaklęcia (Enchantment Glint) w 1.21.4
        stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);

        // Wewnętrzny znacznik CUSTOM_DATA do pewnej identyfikacji klucza
        NbtCompound customData = new NbtCompound();
        customData.putString(CUSTOM_KEY_TAG, type.getId());
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(customData));

        return stack;
    }

    public static KeyType identifyKey(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.isOf(Items.TRIPWIRE_HOOK)) {
            return null;
        }
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData != null) {
            NbtCompound nbt = customData.copyNbt();
            if (nbt.contains(CUSTOM_KEY_TAG)) {
                String id = nbt.getString(CUSTOM_KEY_TAG);
                for (KeyType type : KeyType.values()) {
                    if (type.getId().equals(id)) {
                        return type;
                    }
                }
            }
        }
        // Fallback po nazwie jeśli CUSTOM_DATA zostało zresetowane
        Text name = stack.get(DataComponentTypes.CUSTOM_NAME);
        if (name != null) {
            String raw = name.getString();
            for (KeyType type : KeyType.values()) {
                if (raw.contains(type.getRawDisplayName())) {
                    return type;
                }
            }
        }
        return null;
    }

    /**
     * Dodaje przedmiot lokalnie do ekwipunku klienta (lub wysyła pakiet Creative, jeśli gracz jest na Creative).
     */
    public static void giveClientSideItem(MinecraftClient client, ItemStack stackToGive) {
        if (client.player == null) return;

        ItemStack copy = stackToGive.copy();
        boolean inserted = client.player.getInventory().insertStack(copy);

        if (!inserted && !copy.isEmpty()) {
            // Jeśli ekwipunek pełny, nadpisz aktualnie trzymany slot lub pierwszy slot paska
            int selected = client.player.getInventory().selectedSlot;
            client.player.getInventory().setStack(selected, copy);
        }

        // Synchronizacja pakietem na trybie Creative, żeby nie zniknęło przy otwarciu EQ
        if (client.player.getAbilities().creativeMode && client.getNetworkHandler() != null) {
            for (int i = 0; i < 36; i++) {
                ItemStack invStack = client.player.getInventory().getStack(i);
                int networkSlot = (i < 9) ? (i + 36) : i;
                client.getNetworkHandler().sendPacket(new CreativeInventoryActionC2SPacket(networkSlot, invStack.copy()));
            }
        }
    }
}
