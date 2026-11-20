package pl.anarchia.visualkeys;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import pl.anarchia.visualkeys.gui.CrateOpeningScreen;
import pl.anarchia.visualkeys.gui.KeyGeneratorScreen;
import pl.anarchia.visualkeys.model.KeyType;
import pl.anarchia.visualkeys.util.KeyItemFactory;

public class AnarchiaVisualKeysClient implements ClientModInitializer {

    public static KeyBinding openGeneratorKey;

    @Override
    public void onInitializeClient() {
        openGeneratorKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.anarchia_visual_keys.open_generator",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "category.anarchia_visual_keys"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGeneratorKey.wasPressed()) {
                if (client.player != null && client.currentScreen == null) {
                    client.setScreen(new KeyGeneratorScreen());
                }
            }
        });

        // Obsługa PPM w powietrze z kluczem w ręce
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient()) {
                return ActionResult.PASS;
            }
            ItemStack stack = player.getStackInHand(hand);
            KeyType keyType = KeyItemFactory.identifyKey(stack);
            if (keyType != null) {
                openCrateScreen(keyType, hand);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });

        // Obsługa PPM w skrzynię / Ender Chesta / Shulker Boxa z kluczem w ręce
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClient()) {
                return ActionResult.PASS;
            }
            ItemStack stack = player.getStackInHand(hand);
            KeyType keyType = KeyItemFactory.identifyKey(stack);
            if (keyType != null) {
                Block block = world.getBlockState(hitResult.getBlockPos()).getBlock();
                if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST || block == Blocks.ENDER_CHEST || block == Blocks.BARREL) {
                    openCrateScreen(keyType, hand);
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
    }

    private void openCrateScreen(KeyType keyType, Hand hand) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && !(client.currentScreen instanceof CrateOpeningScreen)) {
            client.execute(() -> client.setScreen(new CrateOpeningScreen(keyType, hand)));
        }
    }
}
