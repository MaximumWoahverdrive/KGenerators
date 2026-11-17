package me.kryniowesegryderiusz.kgenerators.lang.interfaces;

import me.kryniowesegryderiusz.kgenerators.gui.objects.MenuItem;

public interface IMenuItemType {

    IMenuInventoryType getMenuInventory();

    String getKey();

    MenuItem getMenuItem();
}
