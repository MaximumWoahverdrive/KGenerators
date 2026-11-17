package me.kryniowesegryderiusz.kgenerators.multiversion;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.BlocksUtils;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.RecipesLoader;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.SkullUtils;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.WorldGuardUtils;

/**
 * Holds the server-specific helpers. Every supported server (Paper 1.19.4 and newer) uses the
 * same implementations, so they are created directly rather than picked per version.
 */
public class MultiVersionManager {

	@Getter
	private RecipesLoader recipesLoader = new RecipesLoader_1_13();
	@Getter
	private BlocksUtils blocksUtils = new BlocksUtils_1_13();
	@Getter
	private WorldGuardUtils worldGuardUtils;
	@Getter
	private SkullUtils skullUtils = new SkullUtils_1_21();

	public MultiVersionManager() {
		// WorldGuard's classes are only on the classpath when the plugin is installed
		if (Main.getInstance().getServer().getPluginManager().getPlugin("WorldGuard") != null) {
			try {
				this.worldGuardUtils = new WorldGuardUtils_1_13();
				this.worldGuardUtils.worldGuardFlagsAdd();
			} catch (Exception | LinkageError e) {
				this.worldGuardUtils = null;
				Logger.error(e);
			}
		}
	}
}
