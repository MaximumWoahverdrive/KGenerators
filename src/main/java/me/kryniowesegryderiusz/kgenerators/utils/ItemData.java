package me.kryniowesegryderiusz.kgenerators.utils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

/**
 * Custom data on items, stored in Paper's persistent data container.
 */
public final class ItemData {

	private ItemData() {}

	/** The generator an item belongs to. */
	public static final NamespacedKey GENERATOR_ID = new NamespacedKey(Main.getInstance(), "generator-id");

	/**
	 * Where KGenerators 7.3.1 and older kept the generator id: a plain NBT string tag written with
	 * NBT-API. It is not readable through the persistent data container, so it is found in the
	 * item's data as text, written either quoted or not depending on the server version.
	 */
	private static final Pattern LEGACY_GENERATOR_ID = Pattern
			.compile("\"?kgenerators-generator-id\"?\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");

	public static void setString(ItemStack item, NamespacedKey key, String value) {
		ItemMeta meta = item.getItemMeta();
		if (meta == null)
			return;
		meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
		item.setItemMeta(meta);
	}

	@Nullable
	public static String getString(ItemStack item, NamespacedKey key) {
		if (!item.hasItemMeta())
			return null;
		return item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
	}

	/** The generator id written by KGenerators 7.3.1 or older, if the item has one. */
	@Nullable
	public static String getLegacyGeneratorId(ItemStack item) {
		if (!item.hasItemMeta())
			return null;
		Matcher m = LEGACY_GENERATOR_ID.matcher(item.getItemMeta().getAsString());
		return m.find() ? m.group(1).replace("\\\"", "\"").replace("\\\\", "\\") : null;
	}

	/**
	 * Applies a "custom-nbt" entry from an item configuration as a persistent data string:
	 * {@code key:value} under the kgenerators namespace, or {@code namespace:key:value}.
	 */
	public static void applyConfigEntry(ItemStack item, String place, String entry) {
		String[] parts = entry.split(":", 3);
		NamespacedKey key = null;
		String value = null;
		if (parts.length == 3) {
			key = NamespacedKey.fromString((parts[0] + ":" + parts[1]).toLowerCase(Locale.ROOT));
			value = parts[2];
		} else if (parts.length == 2) {
			key = NamespacedKey.fromString(parts[0].toLowerCase(Locale.ROOT), Main.getInstance());
			value = parts[1];
		}
		if (key == null) {
			Logger.error("ItemData: " + place + ": custom-nbt entry \"" + entry
					+ "\" should be key:value or namespace:key:value, with keys of a-z, 0-9, . _ - /");
			return;
		}
		setString(item, key, value);
	}
}
