package me.kryniowesegryderiusz.kgenerators.gui;

import java.util.Map.Entry;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.BentoBoxHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.SuperiorSkyblock2Hook;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.gui.menus.GeneratedObjectsMenu;
import me.kryniowesegryderiusz.kgenerators.gui.menus.GeneratorMenu;
import me.kryniowesegryderiusz.kgenerators.gui.menus.LimitsMenu;
import me.kryniowesegryderiusz.kgenerators.gui.menus.MainMenu;
import me.kryniowesegryderiusz.kgenerators.gui.menus.RecipeMenu;
import me.kryniowesegryderiusz.kgenerators.gui.menus.UpgradeMenu;
import me.kryniowesegryderiusz.kgenerators.gui.objects.MenuPlayer;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.Sched;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

public class MenusManager implements Listener {

    private final java.util.concurrent.ConcurrentHashMap<Player, MenuPlayer> guis =
            new java.util.concurrent.ConcurrentHashMap<>();

    public MenusManager() {

        Logger.debugPluginLoad("MenusManager: Setting up manager");

        if (Main.getSettings().getGuiUpdateFrequency() > 0) {
            Sched.globalTimer(
                    () -> {
                        try {
                            // SWU (Folia): each open menu is updated on its player's own scheduler
                            for (Entry<Player, MenuPlayer> e : guis.entrySet()) {
                                final Player p = e.getKey();
                                final MenuPlayer mp = e.getValue();
                                Sched.entity(p, () -> {
                                    if (!mp.update()) guis.remove(p, mp);
                                });
                            }
                        } catch (Exception e) {
                            Logger.error("MenusManager: An error occurred at menus task");
                            Logger.error(e);
                        }
                    },
                    1L,
                    Main.getSettings().getGuiUpdateFrequency());
        }
    }

    public MenuPlayer getMenuPlayer(Player p) {
        return guis.get(p);
    }

    public void closeInv(Player p) {
        Sched.entity(p, p::closeInventory);
    }

    public void openGeneratorMenu(Player p, GeneratorLocation gLocation) {
        if (!BentoBoxHook.isAllowed(p, BentoBoxHook.Type.OPEN_MENU_FLAG, gLocation.getLocation())
                || !SuperiorSkyblock2Hook.isAllowed(
                        p, SuperiorSkyblock2Hook.Type.OPEN_MENU_FLAG, gLocation.getLocation())) {
            Lang.getMessageStorage().send(p, Message.GENERATOR_MENU_CANT_OPEN_HERE);
            return;
        }

        Inventory menu = GeneratorMenu.get(p, gLocation);
        guis.put(p, new MenuPlayer(p, MenuInventoryType.GENERATOR, menu, gLocation));
        p.openInventory(menu);
    }

    /**
     * Opens main menu on first page
     * @param p player to open the menu for
     */
    public void openMainMenu(Player p) {
        openMainMenu(p, 0);
    }

    /**
     * Opens main menu page containing that generator
     * @param p player to open the menu for
     * @param g generator whose page should be opened
     */
    public void openMainMenu(Player p, Generator g) {
        int nr = 0;
        for (Entry<String, Generator> e : Main.getGenerators().getEntrySet()) {
            if (e.getValue() == g) {
                openMainMenu(
                        p,
                        nr
                                / Lang.getMenuItemStorage()
                                        .get(MenuItemType.MAIN_MENU_GENERATOR)
                                        .getSlots()
                                        .size());
            }
            nr++;
        }
    }

    /**
     * Opens main menu on specified page
     * @param p player to open the menu for
     * @param page (starts from 0)
     */
    public void openMainMenu(Player p, int page) {
        Inventory menu = MainMenu.get(p, page);
        guis.put(p, new MenuPlayer(p, MenuInventoryType.MAIN, menu, page));
        p.openInventory(menu);
    }

    public void openChancesMenu(Player p, Generator generator) {
        Inventory menu = GeneratedObjectsMenu.get(p, generator);
        guis.put(p, new MenuPlayer(p, MenuInventoryType.GENERATED_OBJECTS, menu, generator));
        p.openInventory(menu);
    }

    public void openRecipeMenu(Player p, Generator generator) {
        Inventory menu = RecipeMenu.get(p, generator);
        guis.put(p, new MenuPlayer(p, MenuInventoryType.RECIPE, menu, generator));
        p.openInventory(menu);
    }

    public void openUpgradeMenu(Player p, Generator generator) {
        Inventory menu = UpgradeMenu.get(p, generator);
        guis.put(p, new MenuPlayer(p, MenuInventoryType.UPGRADE, menu, generator));
        p.openInventory(menu);
    }

    public void openLimitsMenu(Player p) {
        Inventory menu = LimitsMenu.get(p);
        guis.put(p, new MenuPlayer(p, MenuInventoryType.LIMITS, menu));
        p.openInventory(menu);
    }

    public Enum<?> getVieving(Player p) {
        MenuPlayer mp = guis.get(p);
        return mp != null ? mp.getMenuInventory() : null;
    }

    public void closeAll() {
        for (Entry<Player, MenuPlayer> e : guis.entrySet()) {
            e.getKey().closeInventory();
        }
    }
}
