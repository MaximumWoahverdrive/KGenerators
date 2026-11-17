package me.kryniowesegryderiusz.kgenerators.gui.objects;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.gui.menus.GeneratorMenu;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.lang.interfaces.IMenuInventoryType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MenuPlayer {

    @Getter
    Player player;

    @Getter
    Inventory inventory;

    @Getter
    Object menuInventory;

    /*
     * Additional info
     */
    @Getter
    GeneratorLocation gLocation;

    @Getter
    int page;

    @Getter
    Generator generator;

    /**
     * Standard menu, without any additional info
     * @param <T> menu inventory type enum
     * @param player player viewing the menu
     * @param menuInventory type of the opened menu
     * @param inventory inventory shown to the player
     */
    public <T extends Enum<T> & IMenuInventoryType> MenuPlayer(Player player, T menuInventory, Inventory inventory) {
        this.player = player;
        this.menuInventory = menuInventory;
        this.inventory = inventory;
    }

    /**
     * Used for specified menus, like Chances, Recipe, Upgrade
     * @param <T> menu inventory type enum
     * @param player player viewing the menu
     * @param menuInventory type of the opened menu
     * @param inventory inventory shown to the player
     * @param generator generator the menu is about
     */
    public <T extends Enum<T> & IMenuInventoryType> MenuPlayer(
            Player player, T menuInventory, Inventory inventory, Generator generator) {
        this.player = player;
        this.menuInventory = menuInventory;
        this.inventory = inventory;
        this.generator = generator;
    }

    /**
     * Used for paginated menus like Main
     * @param <T> menu inventory type enum
     * @param player player viewing the menu
     * @param menuInventory type of the opened menu
     * @param inventory inventory shown to the player
     * @param page currently shown page
     */
    public <T extends Enum<T> & IMenuInventoryType> MenuPlayer(
            Player player, T menuInventory, Inventory inventory, int page) {
        this.player = player;
        this.menuInventory = menuInventory;
        this.inventory = inventory;
        this.page = page;
    }

    /**
     * Used for menus linked to specific location
     * @param <T> menu inventory type enum
     * @param player player viewing the menu
     * @param menuInventory type of the opened menu
     * @param inventory inventory shown to the player
     * @param gLocation generator location the menu is linked to
     */
    public <T extends Enum<T> & IMenuInventoryType> MenuPlayer(
            Player player, T menuInventory, Inventory inventory, GeneratorLocation gLocation) {
        this.player = player;
        this.menuInventory = menuInventory;
        this.inventory = inventory;
        this.gLocation = gLocation;
    }

    /**
     * Updates and checks if player is no longer opening inventory
     * @return false, if player is no longer viewing inventory
     */
    public boolean update() {
        if (this.inventory.getViewers().isEmpty()) return false;

        if (menuInventory == MenuInventoryType.GENERATOR)
            GeneratorMenu.update(this.inventory, this.player, this.gLocation);

        return true;
    }
}
