package pl.anarchia.visualkeys.model;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum KeyType {
    RZADKI(
            "rzadki",
            "RZADKI KLUCZ",
            Formatting.AQUA,
            0x55FFFF,
            0x00AAAA,
            "Skrzyni Rzadkiej"
    ),
    EPICKI(
            "epicki",
            "EPICKI KLUCZ",
            Formatting.LIGHT_PURPLE,
            0xFF55FF,
            0xAA00AA,
            "Skrzyni Epickiej"
    ),
    ANARCHICZNY(
            "anarchiczny",
            "ANARCHICZNY KLUCZ",
            Formatting.GOLD,
            0xFFAA00,
            0xFF5555,
            "Skrzyni Anarchicznej"
    );

    private final String id;
    private final String displayName;
    private final Formatting primaryFormatting;
    private final int primaryColorHex;
    private final int secondaryColorHex;
    private final String crateDisplayName;

    KeyType(String id, String displayName, Formatting primaryFormatting, int primaryColorHex, int secondaryColorHex, String crateDisplayName) {
        this.id = id;
        this.displayName = displayName;
        this.primaryFormatting = primaryFormatting;
        this.primaryColorHex = primaryColorHex;
        this.secondaryColorHex = secondaryColorHex;
        this.crateDisplayName = crateDisplayName;
    }

    public String getId() {
        return id;
    }

    public String getRawDisplayName() {
        return displayName;
    }

    public Formatting getPrimaryFormatting() {
        return primaryFormatting;
    }

    public int getPrimaryColorHex() {
        return primaryColorHex;
    }

    public int getSecondaryColorHex() {
        return secondaryColorHex;
    }

    public String getCrateDisplayName() {
        return crateDisplayName;
    }

    public MutableText getFormattedTitle() {
        if (this == ANARCHICZNY) {
            return Text.literal("ANARCHICZNY ")
                    .formatted(Formatting.GOLD, Formatting.BOLD)
                    .append(Text.literal("KLUCZ").formatted(Formatting.RED, Formatting.BOLD));
        }
        return Text.literal(displayName).formatted(primaryFormatting, Formatting.BOLD);
    }
}
