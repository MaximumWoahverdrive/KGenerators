package me.kryniowesegryderiusz.kgenerators.generators.locations.handlers;

import com.cryptomorin.xseries.XMaterial;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.events.PostGeneratorRegenerationEvent;
import me.kryniowesegryderiusz.kgenerators.api.events.PreGeneratorRegenerationEvent;
import me.kryniowesegryderiusz.kgenerators.api.objects.AbstractGeneratedObject;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.BlockUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class GeneratorLocationRegenerateHandler {

    static Material pistonHead = XMaterial.PISTON_HEAD.get();
    static Material pistonMoving = XMaterial.MOVING_PISTON.get();

    public void handle(GeneratorLocation gLocation) {

        if (!gLocation.isReadyForRegeneration()) {
            if (gLocation.getGenerator().getDelay() == 0) {
                gLocation.getGenerator().setDelay(10);
                Logger.warn(
                        "GeneratorLocationRegenerateHandler: Generator "
                                + gLocation.getGenerator().getId()
                                + " has delay set to 0 and is not ready for regeneration. Changing generator delay to 10 to prevent infinite loop crashes.");
            }
            gLocation.scheduleGeneratorRegeneration();
            return;
        }

        Location generatingLocation = gLocation.getGeneratedBlockLocation();
        Block generatingLocationBlock = generatingLocation.getBlock();

        if (generatingLocationBlock.getType() == pistonHead || generatingLocationBlock.getType() == pistonMoving) {
            gLocation.scheduleGeneratorRegeneration();
            return;
        }

        boolean isAir = BlockUtils.isAir(generatingLocationBlock);
        boolean isOnWhitelist = BlockUtils.isOnWhitelist(generatingLocationBlock);
        boolean isPlaceholder = gLocation.getGenerator().getPlaceholder() == null
                ? isAir
                : gLocation
                        .getGenerator()
                        .getPlaceholder()
                        .getItem()
                        .equals(BlockUtils.getItemStackByBlock(generatingLocationBlock));

        if (!isAir && !isOnWhitelist && !isPlaceholder) {
            // Already holds one of its generated blocks (a regeneration that wasn't needed, e.g. after a
            // cancelled break or piston push): nothing to do
            ItemStack current = BlockUtils.getItemStackByBlock(generatingLocationBlock);
            for (AbstractGeneratedObject generated : gLocation.getGenerator().getGeneratedObjects()) {
                if (generated.isBlockSimilar(current)) return;
            }

            Logger.debug("GeneratorLocationRegenerateHandler: Dropping generator " + gLocation
                    + ": its generated block was replaced by " + generatingLocationBlock.getType());
            gLocation.removeGenerator(true, null);
            return;
        }

        PreGeneratorRegenerationEvent event = new PreGeneratorRegenerationEvent(gLocation);
        Main.getInstance().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        AbstractGeneratedObject ago = gLocation.getGenerator().drawGeneratedObject();
        gLocation.setLastGeneratedObject(ago);
        ago.regenerate(gLocation);

        Main.getInstance().getServer().getPluginManager().callEvent(new PostGeneratorRegenerationEvent(gLocation));
    }
}
