package pl.anarchia.visualkeys.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import pl.anarchia.visualkeys.model.KeyType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class CrateRewardPool {

    public record CrateReward(ItemStack displayStack, Text chatDisplayName, int rarityColor, String rarityLabel) {}

    private static final Random RANDOM = new Random();

    public static List<CrateReward> getRewardsForKey(KeyType keyType, DynamicRegistryManager registryManager) {
        List<CrateReward> rewards = new ArrayList<>();

        switch (keyType) {
            case RZADKI -> {
                rewards.add(createArmorReward(Items.DIAMOND_HELMET, "Diamentowy Hełm [Prot 3]", Formatting.AQUA, 0x55FFFF, "RZADKA", registryManager,
                        Enchantments.PROTECTION, 3, Enchantments.UNBREAKING, 3));
                rewards.add(createArmorReward(Items.DIAMOND_CHESTPLATE, "Diamentowy Napierśnik [Prot 3]", Formatting.AQUA, 0x55FFFF, "RZADKA", registryManager,
                        Enchantments.PROTECTION, 3, Enchantments.UNBREAKING, 3));
                rewards.add(createSwordReward(Items.DIAMOND_SWORD, "Diamentowy Miecz [Sharp 4]", Formatting.AQUA, 0x55FFFF, "RZADKA", registryManager,
                        Enchantments.SHARPNESS, 4, Enchantments.UNBREAKING, 3));
                rewards.add(createSimpleReward(Items.GOLDEN_APPLE, 12, "Złote Jabłka x12", Formatting.YELLOW, 0xFFFF55, "POSPOLITA"));
                rewards.add(createSimpleReward(Items.ENDER_PEARL, 16, "Perły Endermana x16", Formatting.DARK_AQUA, 0x00AAAA, "POSPOLITA"));
                rewards.add(createSimpleReward(Items.DIAMOND_BLOCK, 8, "Bloki Diamentu x8", Formatting.AQUA, 0x55FFFF, "RZADKA"));
                rewards.add(createSimpleReward(Items.EXPERIENCE_BOTTLE, 32, "Buteleczki z Expem x32", Formatting.GREEN, 0x55FF55, "POSPOLITA"));
                rewards.add(createSimpleReward(Items.TOTEM_OF_UNDYING, 1, "Totem Nieśmiertelności", Formatting.GOLD, 0xFFAA00, "UNIKATOWA"));
            }
            case EPICKI -> {
                rewards.add(createArmorReward(Items.DIAMOND_HELMET, "Diamentowy Hełm [Prot 4]", Formatting.LIGHT_PURPLE, 0xFF55FF, "EPICKA", registryManager,
                        Enchantments.PROTECTION, 4, Enchantments.UNBREAKING, 3));
                rewards.add(createArmorReward(Items.DIAMOND_CHESTPLATE, "Diamentowy Napierśnik [Prot 4]", Formatting.LIGHT_PURPLE, 0xFF55FF, "EPICKA", registryManager,
                        Enchantments.PROTECTION, 4, Enchantments.UNBREAKING, 3));
                rewards.add(createArmorReward(Items.DIAMOND_LEGGINGS, "Diamentowe Nogawice [Prot 4]", Formatting.LIGHT_PURPLE, 0xFF55FF, "EPICKA", registryManager,
                        Enchantments.PROTECTION, 4, Enchantments.UNBREAKING, 3));
                rewards.add(createArmorReward(Items.DIAMOND_BOOTS, "Diamentowe Buty [Prot 4]", Formatting.LIGHT_PURPLE, 0xFF55FF, "EPICKA", registryManager,
                        Enchantments.PROTECTION, 4, Enchantments.UNBREAKING, 3));
                rewards.add(createSwordReward(Items.DIAMOND_SWORD, "Diamentowy Miecz [Sharp 5 / Fire 2]", Formatting.LIGHT_PURPLE, 0xFF55FF, "EPICKA", registryManager,
                        Enchantments.SHARPNESS, 5, Enchantments.FIRE_ASPECT, 2));
                rewards.add(createSimpleReward(Items.ENCHANTED_GOLDEN_APPLE, 3, "Koxy (Złote Jabłka) x3", Formatting.GOLD, 0xFFAA00, "EPICKA"));
                rewards.add(createSimpleReward(Items.TOTEM_OF_UNDYING, 2, "Totemy Nieśmiertelności x2", Formatting.GOLD, 0xFFAA00, "EPICKA"));
                rewards.add(createSimpleReward(Items.NETHERITE_INGOT, 2, "Sztabki Netherytu x2", Formatting.DARK_PURPLE, 0xAA00AA, "LEGENDARNA"));
            }
            case ANARCHICZNY -> {
                rewards.add(createArmorReward(Items.NETHERITE_HELMET, "Anarchiczny Hełm Netherite [Prot 4]", Formatting.GOLD, 0xFFAA00, "ANARCHICZNA", registryManager,
                        Enchantments.PROTECTION, 4, Enchantments.UNBREAKING, 3));
                rewards.add(createArmorReward(Items.NETHERITE_CHESTPLATE, "Anarchiczny Napierśnik Netherite [Prot 4]", Formatting.GOLD, 0xFFAA00, "ANARCHICZNA", registryManager,
                        Enchantments.PROTECTION, 4, Enchantments.UNBREAKING, 3));
                rewards.add(createArmorReward(Items.NETHERITE_LEGGINGS, "Anarchiczne Nogawice Netherite [Prot 4]", Formatting.GOLD, 0xFFAA00, "ANARCHICZNA", registryManager,
                        Enchantments.PROTECTION, 4, Enchantments.UNBREAKING, 3));
                rewards.add(createArmorReward(Items.NETHERITE_BOOTS, "Anarchiczne Buty Netherite [Prot 4]", Formatting.GOLD, 0xFFAA00, "ANARCHICZNA", registryManager,
                        Enchantments.PROTECTION, 4, Enchantments.UNBREAKING, 3));
                rewards.add(createSwordReward(Items.NETHERITE_SWORD, "Miecz Anarchii [Sharp 5 / Fire 2]", Formatting.RED, 0xFF5555, "MITYCZNA", registryManager,
                        Enchantments.SHARPNESS, 5, Enchantments.FIRE_ASPECT, 2));
                rewards.add(createSimpleReward(Items.ELYTRA, 1, "Anarchiczna Elytra", Formatting.LIGHT_PURPLE, 0xFF55FF, "MITYCZNA"));
                rewards.add(createSimpleReward(Items.ENCHANTED_GOLDEN_APPLE, 8, "Koxy (Enchanted Golden Apple) x8", Formatting.GOLD, 0xFFAA00, "ANARCHICZNA"));
                rewards.add(createSimpleReward(Items.TOTEM_OF_UNDYING, 4, "Totemy Nieśmiertelności x4", Formatting.YELLOW, 0xFFFF55, "ANARCHICZNA"));
                rewards.add(createSimpleReward(Items.BEACON, 1, "Magiczna Latarnia (Beacon)", Formatting.AQUA, 0x55FFFF, "LEGENDARNA"));
            }
        }

        return rewards;
    }

    public static CrateReward getRandomReward(List<CrateReward> pool) {
        return pool.get(RANDOM.nextInt(pool.size()));
    }

    private static CrateReward createSimpleReward(Item item, int count, String name, Formatting color, int hexColor, String rarityLabel) {
        ItemStack stack = new ItemStack(item, count);
        Text title = Text.literal(name).formatted(color, Formatting.BOLD)
                .setStyle(Style.EMPTY.withColor(color).withBold(true).withItalic(false));
        stack.set(DataComponentTypes.CUSTOM_NAME, title);
        stack.set(DataComponentTypes.LORE, new LoreComponent(List.of(
                Text.literal("Wygrana ze skrzyni Anarchia.gg").formatted(Formatting.GRAY)
                        .setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(false))
        )));
        return new CrateReward(stack, title, hexColor, rarityLabel);
    }

    private static CrateReward createArmorReward(Item item, String name, Formatting color, int hexColor, String rarityLabel,
                                                 DynamicRegistryManager registryManager,
                                                 RegistryKey<Enchantment> ench1, int lvl1,
                                                 RegistryKey<Enchantment> ench2, int lvl2) {
        ItemStack stack = new ItemStack(item, 1);
        Text title = Text.literal(name).formatted(color, Formatting.BOLD)
                .setStyle(Style.EMPTY.withColor(color).withBold(true).withItalic(false));
        stack.set(DataComponentTypes.CUSTOM_NAME, title);
        stack.set(DataComponentTypes.LORE, new LoreComponent(List.of(
                Text.literal("» ").formatted(Formatting.DARK_GRAY)
                        .append(Text.literal("Ekwipunek ze skrzyni Anarchia.gg").formatted(Formatting.GRAY))
                        .setStyle(Style.EMPTY.withItalic(false))
        )));
        applyEnchantment(stack, registryManager, ench1, lvl1);
        applyEnchantment(stack, registryManager, ench2, lvl2);
        applyEnchantment(stack, registryManager, Enchantments.MENDING, 1);
        return new CrateReward(stack, title, hexColor, rarityLabel);
    }

    private static CrateReward createSwordReward(Item item, String name, Formatting color, int hexColor, String rarityLabel,
                                                 DynamicRegistryManager registryManager,
                                                 RegistryKey<Enchantment> ench1, int lvl1,
                                                 RegistryKey<Enchantment> ench2, int lvl2) {
        ItemStack stack = new ItemStack(item, 1);
        Text title = Text.literal(name).formatted(color, Formatting.BOLD)
                .setStyle(Style.EMPTY.withColor(color).withBold(true).withItalic(false));
        stack.set(DataComponentTypes.CUSTOM_NAME, title);
        stack.set(DataComponentTypes.LORE, new LoreComponent(List.of(
                Text.literal("» ").formatted(Formatting.DARK_GRAY)
                        .append(Text.literal("Broń ze skrzyni Anarchia.gg").formatted(Formatting.GRAY))
                        .setStyle(Style.EMPTY.withItalic(false))
        )));
        applyEnchantment(stack, registryManager, ench1, lvl1);
        applyEnchantment(stack, registryManager, ench2, lvl2);
        applyEnchantment(stack, registryManager, Enchantments.UNBREAKING, 3);
        applyEnchantment(stack, registryManager, Enchantments.MENDING, 1);
        return new CrateReward(stack, title, hexColor, rarityLabel);
    }

    private static void applyEnchantment(ItemStack stack, DynamicRegistryManager registryManager, RegistryKey<Enchantment> key, int level) {
        if (registryManager == null) return;
        Optional<Registry<Enchantment>> registryOpt = registryManager.getOptional(RegistryKeys.ENCHANTMENT);
        registryOpt.ifPresent(registry -> {
            Optional<RegistryEntry.Reference<Enchantment>> entry = registry.getEntry(key.getValue());
            entry.ifPresent(e -> stack.addEnchantment(e, level));
        });
    }
}
