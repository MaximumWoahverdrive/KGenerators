package me.kryniowesegryderiusz.kgenerators.gui;

import java.util.Map.Entry;
import java.util.function.Supplier;
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

    public void remove(Player p, MenuPlayer mp) {
        guis.remove(p, mp);
    }

    public void remove(Player p) {
        guis.remove(p);
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

        open(p, () -> {
            Inventory menu = GeneratorMenu.get(p, gLocation);
            return new MenuPlayer(p, MenuInventoryType.GENERATOR, menu, gLocation);
        });
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
        open(p, () -> new MenuPlayer(p, MenuInventoryType.MAIN, MainMenu.get(p, page), page));
    }

    public void openChancesMenu(Player p, Generator generator) {
        open(
                p,
                () -> new MenuPlayer(
                        p, MenuInventoryType.GENERATED_OBJECTS, GeneratedObjectsMenu.get(p, generator), generator));
    }

    public void openRecipeMenu(Player p, Generator generator) {
        open(p, () -> new MenuPlayer(p, MenuInventoryType.RECIPE, RecipeMenu.get(p, generator), generator));
    }

    public void openUpgradeMenu(Player p, Generator generator) {
        open(p, () -> new MenuPlayer(p, MenuInventoryType.UPGRADE, UpgradeMenu.get(p, generator), generator));
    }

    public void openLimitsMenu(Player p) {
        open(p, () -> new MenuPlayer(p, MenuInventoryType.LIMITS, LimitsMenu.get(p)));
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

    /**
     * Builds and opens a menu on the player's own scheduler: never inside the click event that asked for it
     * (Bukkit forbids opening inventories there) and on the right thread on Folia.
     */
    private void open(Player p, Supplier<MenuPlayer> menu) {
        Sched.entity(p, () -> {
            try {
                MenuPlayer mp = menu.get();
                guis.put(p, mp);
                p.openInventory(mp.getInventory());
            } catch (Exception e) {
                Lang.getMessageStorage().send(p, Message.COMMANDS_MENU_ERROR);
                Logger.error("MenusManager: An error occurred while opening a menu");
                Logger.error(e);
            }
        });
    }
}
