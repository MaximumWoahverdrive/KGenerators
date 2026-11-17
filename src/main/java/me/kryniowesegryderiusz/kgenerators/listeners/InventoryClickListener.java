package me.kryniowesegryderiusz.kgenerators.listeners;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.addons.events.CustomMenuClickEvent;
import me.kryniowesegryderiusz.kgenerators.gui.menus.GeneratedObjectsMenu;
import me.kryniowesegryderiusz.kgenerators.gui.menus.GeneratorMenu;
import me.kryniowesegryderiusz.kgenerators.gui.menus.LimitsMenu;
import me.kryniowesegryderiusz.kgenerators.gui.menus.MainMenu;
import me.kryniowesegryderiusz.kgenerators.gui.menus.RecipeMenu;
import me.kryniowesegryderiusz.kgenerators.gui.menus.UpgradeMenu;
import me.kryniowesegryderiusz.kgenerators.gui.objects.MenuPlayer;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.Sched;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;

public class InventoryClickListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent e) {

        try {

            if (e.isCancelled()) return;

            Player p = (Player) e.getWhoClicked();
            int slot = e.getSlot();

            /*
             * Grindstone and anvil change generator item protection
             */
            if ((e.getInventory().getType() == InventoryType.GRINDSTONE
                            || e.getInventory().getType() == InventoryType.ANVIL)
                    && e.getCurrentItem() != null
                    && Main.getGenerators().get(e.getCurrentItem()) != null) {

                Lang.getMessageStorage()
                        .send(e.getWhoClicked(), Message.GENERATOR_PROTECTION_CANT_CHANGE_GENERATOR_ITEM);
                e.setCancelled(true);
                Sched.entity(p, p::closeInventory);
            }

            /*
             * Menus
             */

            MenuPlayer mp = Main.getMenus().getMenuPlayer(p);
            if (mp != null) {
                Enum<?> m = mp.getMenuInventory();
                // Every click is cancelled while a menu is open, but only clicks in the menu itself are buttons
                e.setCancelled(true);
                if (e.getClickedInventory() != mp.getInventory()) return;

                try {
                    if (m == MenuInventoryType.GENERATOR) GeneratorMenu.onClick(p, slot);
                    else if (m == MenuInventoryType.MAIN) MainMenu.onClick(p, slot, e.getClick());
                    else if (m == MenuInventoryType.GENERATED_OBJECTS) GeneratedObjectsMenu.onClick(p, slot);
                    else if (m == MenuInventoryType.LIMITS) LimitsMenu.onClick(p, slot, e.getCurrentItem());
                    else if (m == MenuInventoryType.RECIPE) RecipeMenu.onClick(p, slot);
                    else if (m == MenuInventoryType.UPGRADE) UpgradeMenu.onClick(p, slot);

                    Main.getInstance()
                            .getServer()
                            .getPluginManager()
                            .callEvent(new CustomMenuClickEvent(p, m, slot, e.getClick(), e.getCurrentItem()));

                } catch (Exception ex) {
                    Lang.getMessageStorage().send(p, Message.COMMANDS_MENU_ERROR);
                    Logger.error("An error occurred while using menus!");
                    Logger.error(ex);
                    Main.getMenus().closeInv(p);
                }
            }

        } catch (Exception exception) {
            Logger.error(exception);
        }
    }

    @EventHandler
    public void inventoryCloseEvent(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        MenuPlayer mp = Main.getMenus().getMenuPlayer(p);
        if (mp != null && mp.getInventory() == e.getInventory()) Main.getMenus().remove(p, mp);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryDrag(InventoryDragEvent e) {
        MenuPlayer mp = Main.getMenus().getMenuPlayer((Player) e.getWhoClicked());
        if (mp == null) return;
        int menuSize = mp.getInventory().getSize();
        for (int rawSlot : e.getRawSlots()) {
            if (rawSlot < menuSize) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Main.getMenus().remove(e.getPlayer());
    }
}
