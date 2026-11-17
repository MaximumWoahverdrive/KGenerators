package me.kryniowesegryderiusz.kgenerators.lang.storages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import me.kryniowesegryderiusz.kgenerators.gui.objects.MenuItem;
import me.kryniowesegryderiusz.kgenerators.lang.interfaces.IMenuItemType;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;

public class MenuItemStorage {

	private final HashMap<String, MenuItem> lang = new HashMap<>();
	Config config;
	ArrayList<Class<? extends Enum<?>>> classes = new ArrayList<>();

	public MenuItemStorage(Config config) {
		this.config = config;
	}

	public <T extends Enum<T> & IMenuItemType> void register(Class<T> c) {
		for (T en : c.getEnumConstants()) {
			lang.put(en.getKey(), en.getMenuItem());
			en.getMenuItem().load(en.getKey(), config);
		}

		if (!classes.contains(c))
			classes.add(c);
	}

	public void reload() {
		for (Entry<String, MenuItem> e : lang.entrySet()) {
			e.getValue().load(e.getKey(), config);
		}
	}

	public <T extends Enum<T> & IMenuItemType> MenuItem get(T m) {
		return lang.get(m.getKey()).clone();
	}

	public Collection<MenuItem> values() {
		return lang.values();
	}

	/** @return the constants of every registered menu item enum */
	public ArrayList<Enum<?>> getAllEnums() {
		ArrayList<Enum<?>> enums = new ArrayList<>();
		for (Class<? extends Enum<?>> c : this.classes) {
			Collections.addAll(enums, c.getEnumConstants());
		}
		return enums;
	}

}
