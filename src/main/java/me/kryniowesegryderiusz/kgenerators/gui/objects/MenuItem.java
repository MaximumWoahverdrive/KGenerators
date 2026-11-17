package me.kryniowesegryderiusz.kgenerators.gui.objects;

import me.kryniowesegryderiusz.kgenerators.utils.Text;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import com.cryptomorin.xseries.XEnchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.PlaceholderAPIHook;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.lang.objects.StringContent;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.ItemUtils;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;

public class MenuItem implements Cloneable {
	@Getter
	private boolean enabled = true;
	@Getter
	private String itemType;
	@Setter
	private ItemStack itemStack;
	@Getter
	private String name = "";
	private ArrayList<String> lore = new ArrayList<>();
	private String slots = "";
	@Setter
	private boolean glow = false;

	public MenuItem(String itemType) {
		this.itemType = itemType;
	}

	public MenuItem(String itemType, String name, boolean glow, String slots) {
		this.itemType = itemType;
		setName(name);
		this.glow = glow;
		this.slots = slots.replace(" ", "");
	}

	public MenuItem(boolean enabled, String itemType, String name, boolean glow, String slots, ArrayList<String> lore) {
		this.enabled = enabled;
		this.itemType = itemType;
		setName(name);
		this.glow = glow;
		this.slots = slots;
		this.lore.clear();
		addLore(lore);
	}

	public MenuItem(String itemType, String name, boolean glow, String... loreAndSlot) {
		this.itemType = itemType;
		setName(name);
		this.glow = glow;
		boolean first = true;
		for (String s : loreAndSlot) {
			if (first) {
				this.slots = s.replace(" ", "");
				first = false;
			} else {
				addLore(s);
			}
		}
	}

	public void addLore(String loreLine) {
		if (loreLine != null)
			this.lore.add(Text.colorize(loreLine));
	}

	public void addLore(String... lore) {
		for (String s : lore) {
			if (s != null)
				this.lore.add(Text.colorize(s));
		}
	}

	public void addLore(StringContent stringContent) {
		this.addLore(stringContent.getLines());
	}

	public void addLore(List<String> lore) {
		for (String s : lore) {
			if (s != null)
				this.lore.add(Text.colorize(s));
		}
	}

	public void setName(String name) {
		if (name != null)
			this.name = Text.colorize(name);
	}

	public void replace(String key, String value) {
		this.name = this.name.replace(key, value);
        this.lore.replaceAll(s -> s.replace(key, value));
	}

	public void replaceLore(String key, StringContent stringContent) {
		ArrayList<String> newDesc = new ArrayList<>();
		for (String s : this.lore) {
			if (s.contains(key)) {
				if (stringContent != null) {
					for (String as : stringContent.getLines()) {
						newDesc.add(Text.colorize(as));
					}
				}
			} else {
				newDesc.add(s);
			}
		}
		this.lore = newDesc;
	}

	public ItemStack build(Player p) {
		ItemStack item;
		if (itemStack != null)
			item = itemStack;
		else
			item = ItemUtils.parseItemStack(this.itemType, "MenuItem", false);

		if (item.getType().equals(Material.AIR))
			return item;

		ItemMeta meta;
		if (item.getItemMeta() != null) {
			meta = item.getItemMeta();
		} else {
			meta = Main.getInstance().getServer().getItemFactory().getItemMeta(item.getType());
		}

		Text.setName(meta, this.name);

		if (!lore.isEmpty())
			Text.setLore(meta, this.lore);

		if (glow)
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

		item.setItemMeta(meta);

		if (glow)
			item.addUnsafeEnchantment(XEnchantment.FORTUNE.get(), 1);
		
		item = PlaceholderAPIHook.translatePlaceholders(p, item);
		
		return item;

	}

	public void load(MenuItemType menu, Config config) {
		load(menu.getKey(), config);
	}

	@SuppressWarnings("unchecked")
	public void load(String path, Config config) {
		if (config.contains(path)) {
			this.itemType = config.getString(path + ".item");
			if (config.contains(path + ".name"))
				setName(config.getString(path + ".name"));
			this.lore.clear();
			if (config.contains(path + ".lore"))
				addLore((ArrayList<String>) config.getList(path + ".lore"));
			if (config.contains(path + ".glow"))
				this.glow = config.getBoolean(path + ".glow");
			if (config.contains(path + ".slots"))
				this.slots = config.getString(path + ".slots");
			if (config.contains(path + ".enabled"))
				this.enabled = config.getBoolean(path + ".enabled");
		} else {
			config.set(path + ".enabled", true);
			config.set(path + ".item", this.itemType);
			config.set(path + ".name", this.name);
			config.set(path + ".lore", this.lore);
			config.set(path + ".slots", this.slots);
			config.set(path + ".glow", this.glow);
			try {
				config.saveConfig();
			} catch (IOException e) {
				Logger.error("Lang: Cant save lang file!");
				Logger.error(e);
			}
		}
	}

	public ArrayList<Integer> getSlots() {
		ArrayList<Integer> slots = new ArrayList<>();
		String[] s = this.slots.split(",");
        for (String string : s) slots.add(Integer.valueOf(string));
		return slots;
	}

	public MenuItem clone() {
		return new MenuItem(this.enabled, this.itemType, this.name, this.glow, this.slots,
            new ArrayList<>(this.lore));
	}

}
