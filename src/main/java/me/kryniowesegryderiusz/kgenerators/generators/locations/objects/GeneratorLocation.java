package me.kryniowesegryderiusz.kgenerators.generators.locations.objects;

import me.kryniowesegryderiusz.kgenerators.utils.BlockUtils;
import me.kryniowesegryderiusz.kgenerators.utils.Text;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IGeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.api.objects.AbstractGeneratedObject;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.BentoBoxHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.FactionsUUIDHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.LandsHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.PlotSquaredHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.SuperiorSkyblock2Hook;
import me.kryniowesegryderiusz.kgenerators.generators.generator.enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.locations.PlacedGeneratorsManager.ChunkInfo;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.GeneratorLocationActionHandler;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.GeneratorLocationPickUpHandler;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.GeneratorLocationPlaceHandler;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.GeneratorLocationRegenerateHandler;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.GeneratorLocationRemoveHandler;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.enums.InteractionType;
import me.kryniowesegryderiusz.kgenerators.generators.players.objects.GeneratorPlayer;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class GeneratorLocation implements IGeneratorLocation {

	private static GeneratorLocationPickUpHandler pickUpHandler = new GeneratorLocationPickUpHandler();
	private static GeneratorLocationPlaceHandler placeHandler = new GeneratorLocationPlaceHandler();
	private static GeneratorLocationRegenerateHandler regenerateHandler = new GeneratorLocationRegenerateHandler();
	private static GeneratorLocationRemoveHandler removeHandler = new GeneratorLocationRemoveHandler();
	private static GeneratorLocationActionHandler actionHandler = new GeneratorLocationActionHandler();

	@Setter
	@Getter
	private int id;
	@Getter
	private Generator generator;
	@Getter
	private GeneratorPlayer owner;
	@Getter
	private Location location;

	private Location hologramLocation;
	private UUID hologramUUID = UUID.randomUUID();
	
	@Getter
	private ChunkInfo chunkInfo;

	@Setter
	@Getter
	AbstractGeneratedObject lastGeneratedObject;

	/**
	 * Creates GeneratorLocation. **Note that you probably should use
	 * GeneratorLocation#save() method**
	 * 
	 * @param id - generatorLocation database id (-1 if not yet in database)
	 * @param generator - generator placed at this location
	 * @param location - location of the generator
	 * @param chunkInfo - chunk containing the location
	 * @param owner - owner of the generator, nullable
	 * @param ago - last generated object, nullable
	 */
	public GeneratorLocation(int id, Generator generator, Location location, ChunkInfo chunkInfo, @Nullable GeneratorPlayer owner, @Nullable AbstractGeneratedObject ago) {
		this.id = id;
		this.generator = generator;
		this.owner = owner;
		this.location = location;

		this.hologramLocation = location.clone().add(0.5, 0, 0.5);
		
		if (generator.getType() == GeneratorType.DOUBLE)
			this.hologramLocation.add(0, 1, 0);

		if (getGenerator().getPlaceholder() != null)
			this.hologramLocation.add(0, 1, 0);

		this.chunkInfo = chunkInfo;
		
		this.setLastGeneratedObject(ago);
	}

	/*
	 * Getting GeneratorLocation info
	 */

	// api
	public Location getGeneratedBlockLocation() {
		if (this.getGenerator().getType() == GeneratorType.SINGLE)
			return location;
		else
			return location.clone().add(0, 1, 0);
	}

	// api
	public boolean isBlockPossibleToMine(Location location) {
		return this.getGeneratedBlockLocation().equals(location) && 
				(this.getGenerator().getPlaceholder() == null || !this.getGenerator().getPlaceholder().getItem().equals(BlockUtils.getItemStackByBlock(location.getBlock())));
	}

	// api
	/**
	 * Checks if player is permitted to mine generated block. You probably wanna use
	 * {@link #isBlockPossibleToMine(Location)} at first
	 * 
	 * @param player player trying to mine; is sent a message if not permitted
	 * @return whether its possible to mine
	 */
	public boolean isPermittedToMine(Player player) {
		String permission = "kgenerators.mine." + this.getGenerator().getId();
		if (!player.hasPermission(permission)) {
			Lang.getMessageStorage().send(player, Message.GENERATORS_DIGGING_NO_PERMISSION, "<permission>", permission,
					"<generator>", Text.name(this.getGenerator().getGeneratorItem().getItemMeta()));
			return false;
		}

		if (!BentoBoxHook.isAllowed(player, BentoBoxHook.Type.USE_FLAG, this.getGeneratedBlockLocation())
				|| !SuperiorSkyblock2Hook.isAllowed(player, SuperiorSkyblock2Hook.Type.USE_FLAG,
						this.getGeneratedBlockLocation())
				|| !PlotSquaredHook.isPlayerAllowedToMine(player, this.getGeneratedBlockLocation())
				|| !LandsHook.isPlayerAllowedToMine(player, this.getGeneratedBlockLocation())
				|| !FactionsUUIDHook.isPlayerAllowedToMine(player, this.getGeneratedBlockLocation())) {
			Lang.getMessageStorage().send(player, Message.GENERATORS_DIGGING_CANT_HERE);
			return false;
		}
		return true;
	}

	/**
	 * Returns whether this generatorLocation is corrupted. More precisely if there
	 * isnt any scheduled regeneration referred to it and there is nothing in place
	 * of that generator.
	 * 
	 * @return true if the generator is loaded, not scheduled and its generated
	 *         block is missing or only the placeholder
	 */
	// api
	public boolean isBroken() {
		if (!Main.getPlacedGenerators().isLoaded(this))
			return false;

		if (!Main.getSchedules().isScheduled(this)) {
			if (BlockUtils.isAir(this.getGeneratedBlockLocation().getBlock())) {
				return true;
			} else {
				if (this.getGenerator().getPlaceholder() != null) {
					ItemStack itemBlock = BlockUtils.getItemStackByBlock(this.getGeneratedBlockLocation().getBlock());
					if (itemBlock.isSimilar(this.getGenerator().getPlaceholder().getItem())) {
						for (AbstractGeneratedObject ago : this.getGenerator().getGeneratedObjects()) {
							if (ago.isBlockSimilar(itemBlock))
								return false;
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isLoaded() {
		return Main.getPlacedGenerators().isLoaded(this);
	}

	/*
	 * GeneratorLocation actions
	 */

	// api
	public void regenerateGenerator() {
		regenerateHandler.handle(this);
	}

	// api
	public void scheduleGeneratorRegeneration() {
		Main.getSchedules().schedule(this, false);
	}

	// api
	public void removeGenerator(boolean drop, @Nullable Player player) {
		removeHandler.handle(this, drop, player);
	}

	/**
	 * Places generator and saves it
	 * 
	 * @param player player placing the generator
	 * @return whether placing was successful
	 */
	public boolean placeGenerator(Player player) {
		return placeHandler.handle(this, player, false);
	}

	/**
	 * Places generator and saves it For commands/api usage
	 * 
	 * @param sender sender placing the generator, used for permission checks and messages
	 * @param generateGeneratorBlock whether to also set the generator block itself (only for double generators)
	 * @return whether placing was successful
	 */
	public boolean placeGenerator(CommandSender sender, boolean generateGeneratorBlock) {
		return placeHandler.handle(this, sender, generateGeneratorBlock);
	}

	public void pickUpGenerator(Player player) {
		pickUpHandler.handle(this, player);
	}

	/**
	 * Runs the configured generator action matching the used interaction.
	 * 
	 * @param usedActionType interaction the player performed
	 * @param player player interacting with the generator
	 * @return true if event should be cancelled
	 */
	public boolean handleAction(InteractionType usedActionType, Player player) {
		return actionHandler.handle(this, usedActionType, player);
	}

	/**
	 * Changes this generatorLocation to another generator
	 * 
	 * @param generator generator to change to
	 */
	// api
	public void changeTo(Generator generator) {
		Logger.info("Generator " + this.generator.getId() + " placed in " + this.toStringLocation()
				+ " was transformed to " + generator.getId());
		Main.getSchedules().remove(this);
		
		if (this.owner != null) {
			this.getOwner().removeGeneratorFromPlayer(this.getGenerator());
			this.getOwner().addGeneratorToPlayer(generator);
		}

		this.generator = generator;
		if (this.getGenerator().getType() == GeneratorType.SINGLE) {
			BlockUtils.setBlock(this.location, Material.AIR);
		} else {
			BlockUtils.setBlock(this.location, this.getGenerator().getGeneratorItem());
			BlockUtils.setBlock(this.location.clone().add(0, 1, 0), Material.AIR);
		}

		Main.getDatabases().getDb().saveGenerator(this);

		this.regenerateGenerator();
		
	}
	
	public int getLastGeneratedObjectId() {
		return this.getGenerator().getGeneratedObjectId(this.lastGeneratedObject);
	}
	
	public boolean isReadyForRegeneration() {
		return this.lastGeneratedObject == null ? true : this.lastGeneratedObject.isReady();
	}
	
	/*
	 * Hologram
	 */
	
	public Location getHologramLocation(int lines) {
		return this.hologramLocation.clone().add(0, 0.2*(lines+1), 0);
	}
	
	public String getHologramUUID() {
		return "kgenerators_"+this.hologramUUID.toString();
	}

	/*
	 * Other
	 */

	@Override
	public String toString() {
		if (this.owner != null)
			return "(" + this.id + ") " + this.generator.getId() + " owned by " + this.owner.getName() + " placed in " + toStringLocation()
					+ " (" + this.getChunkInfo().toString() + ")";
		else
			return  "(" + this.id + ") " + this.generator.getId() + " owned by no one" + " placed in " + toStringLocation() + " ("
					+ this.getChunkInfo().toString() + ")";
	}

	public String toStringLocation() {
		return "world " + this.location.getWorld().getName() + " at " + String.valueOf(this.location.getX()) + ", "
				+ String.valueOf(this.location.getY()) + ", " + String.valueOf(this.location.getZ());
	}
}
