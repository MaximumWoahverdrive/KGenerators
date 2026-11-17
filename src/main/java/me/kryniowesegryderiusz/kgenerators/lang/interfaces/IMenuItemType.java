package me.kryniowesegryderiusz.kgenerators.lang.interfaces;

import me.kryniowesegryderiusz.kgenerators.gui.objects.MenuItem;

public interface IMenuItemType {
	
	<T extends Enum<T> & IMenuInventoryType> T getMenuInventory();
	String getKey();
	MenuItem getMenuItem();

}
