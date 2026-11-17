package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderHook {

    private static boolean enabled = false;

    public static void handleGeneratorLocationRemove(GeneratorLocation gLoc) {
        if (!Main.getDependencies().isEnabled(Dependency.ITEMS_ADDER)) return;
        CustomBlock.remove(gLoc.getGeneratedBlockLocation());
    }

    public static ItemStack getItemStack(String material) {
        if (!Main.getDependencies().isEnabled(Dependency.ITEMS_ADDER)) return null;

        CustomStack customStack = CustomStack.getInstance(material);
        if (customStack == null) return null;
        return customStack.getItemStack();
    }

    public class ItemsAdderListeners implements Listener {
        @EventHandler
        public void onItemsAdderLoadDataEvent(ItemsAdderLoadDataEvent e) {
            if (!enabled) {
                enabled = true;
                Main.getInstance().enable();
            }
        }
    }
}
