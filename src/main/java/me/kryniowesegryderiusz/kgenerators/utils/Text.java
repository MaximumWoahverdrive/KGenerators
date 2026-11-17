package me.kryniowesegryderiusz.kgenerators.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

/**
 * Text handling on Adventure.
 *
 * <p>Configs and language files use {@code &} colour codes and hex colours written as
 * {@code #RRGGBB} (or {@code &#RRGGBB}). Inside the plugin, text stays a string with section-sign
 * codes, so that placeholders can be replaced in it; it becomes an Adventure component when it
 * is sent to a player or put on an item, inventory or entity.
 */
public final class Text {

    private Text() {}

    /** A bare {@code #RRGGBB}, which configs have always been able to use as a colour. */
    private static final Pattern BARE_HEX = Pattern.compile("(?<![&#\\w:<])#([A-Fa-f0-9]{6})");

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    /** An {@code &} colour or format code, or an {@code &#RRGGBB} hex colour. */
    private static final Pattern LEGACY_CODE = Pattern.compile("&(?:([0-9a-fk-orA-FK-OR])|#([0-9a-fA-F]{6}))");

    private static final String[] LEGACY_COLOURS = {
        "black", "dark_blue", "dark_green", "dark_aqua", "dark_red", "dark_purple", "gold", "gray",
        "dark_gray", "blue", "green", "aqua", "red", "light_purple", "yellow", "white"
    };

    private static final LegacyComponentSerializer AMPERSAND = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    /** Section-sign codes, with hex colours in the {@code §x§r§r§g§g§b§b} form Bukkit uses. */
    private static final LegacyComponentSerializer SECTION = LegacyComponentSerializer.builder()
            .character(LegacyComponentSerializer.SECTION_CHAR)
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    /**
     * Config text as a section-sign string. Config text can use {@code &} colour codes, hex colours
     * ({@code #RRGGBB} or {@code &#RRGGBB}) and MiniMessage tags ({@code <gradient:...>},
     * {@code <bold>}, ...), also mixed. Text that already has section-sign codes, such as a name that
     * was coloured before, is read the same way.
     *
     * <p>Text without MiniMessage tags is read exactly as before MiniMessage support existed. Click
     * and hover tags have no effect: the result is a plain coloured string.
     *
     * @param configText text from the config
     * @return the text with all colour codes as section-sign codes
     */
    public static String colorize(String configText) {
        String text = configText.replace(LegacyComponentSerializer.SECTION_CHAR, '&');
        text = BARE_HEX.matcher(text).replaceAll("&#$1");
        // Unknown tags such as KGenerators' own <generator> placeholders are left alone by
        // MiniMessage, so stripping only changes text that has real MiniMessage tags in it
        if (!MINI_MESSAGE.stripTags(text).equals(text))
            return SECTION.serialize(MINI_MESSAGE.deserialize(legacyToMiniMessage(text)));
        return SECTION.serialize(AMPERSAND.deserialize(text));
    }

    /** {@code &} codes as MiniMessage tags; like in legacy text, a colour resets the formatting. */
    private static String legacyToMiniMessage(String text) {
        Matcher m = LEGACY_CODE.matcher(text);
        StringBuilder out = new StringBuilder();
        while (m.find()) {
            String tag;
            if (m.group(2) != null) tag = "<reset><#" + m.group(2) + ">";
            else {
                char code = Character.toLowerCase(m.group(1).charAt(0));
                tag = switch (code) {
                    case 'k' -> "<obfuscated>";
                    case 'l' -> "<bold>";
                    case 'm' -> "<strikethrough>";
                    case 'n' -> "<underlined>";
                    case 'o' -> "<italic>";
                    case 'r' -> "<reset>";
                    default -> "<reset><" + LEGACY_COLOURS[Character.digit(code, 16)] + ">";
                };
            }
            m.appendReplacement(out, Matcher.quoteReplacement(tag));
        }
        m.appendTail(out);
        return out.toString();
    }

    /**
     * A section-sign string as a component.
     *
     * @param text a string with section-sign codes
     * @return the deserialized component
     */
    public static Component component(String text) {
        return SECTION.deserialize(text);
    }

    /**
     * A component as a section-sign string.
     *
     * @param component the component to serialize
     * @return the component as a string with section-sign codes
     */
    public static String legacy(Component component) {
        return SECTION.serialize(component);
    }

    /**
     * A section-sign string as a component for an item name or lore line. Minecraft shows those in
     * italics unless told otherwise, which legacy Bukkit names never were.
     *
     * @param text a string with section-sign codes
     * @return the component, not italic unless the text sets italics itself
     */
    public static Component itemText(String text) {
        return component(text).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    /**
     * The item's custom name as a section-sign string, or an empty string if it has none.
     *
     * @param meta the item meta, may be null
     * @return the name, or an empty string if the meta is null or has no custom name
     */
    public static String name(@Nullable ItemMeta meta) {
        Component name = meta == null ? null : meta.displayName();
        return name == null ? "" : legacy(name);
    }

    public static void setName(ItemMeta meta, @Nullable String name) {
        meta.displayName(name == null ? null : itemText(name));
    }

    /**
     * The item's lore as section-sign strings; empty if it has none.
     *
     * @param meta the item meta, may be null
     * @return a new list of lore lines, empty if the meta is null or has no lore
     */
    public static List<String> lore(@Nullable ItemMeta meta) {
        List<String> lines = new ArrayList<>();
        List<Component> lore = meta == null ? null : meta.lore();
        if (lore != null) for (Component line : lore) lines.add(legacy(line));
        return lines;
    }

    public static void setLore(ItemMeta meta, @Nullable List<String> lore) {
        if (lore == null) {
            meta.lore(null);
            return;
        }
        List<Component> lines = new ArrayList<>();
        for (String line : lore) lines.add(itemText(line));
        meta.lore(lines);
    }
}
