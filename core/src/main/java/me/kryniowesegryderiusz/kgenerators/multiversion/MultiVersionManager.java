package me.kryniowesegryderiusz.kgenerators.multiversion;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.ActionBar;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.BlocksUtils;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.ChatUtils;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.RecipesLoader;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.SkullUtils;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.WorldGuardUtils;

public class MultiVersionManager {

	/**
	 * Major version. 1 for every release up to 1.21.x, then 26 and up once
	 * Minecraft dropped the leading "1." from its version scheme.
	 */
	private short major;
	/**
	 * Minor version. For 1.x this is the part everything below gates on (the
	 * "21" of 1.21.7). For 26+ it is a much smaller number (the "2" of 26.2)
	 * and must not be compared against the 1.x thresholds -- see isHigher.
	 */
	private short minor;

	@Getter
	private RecipesLoader recipesLoader;
	@Getter
	private BlocksUtils blocksUtils;
	@Getter
	private WorldGuardUtils worldGuardUtils;
	@Getter
	private ActionBar actionBar;
	@Getter
	private ChatUtils chatUtils;
	@Getter
	private SkullUtils skullUtils;

	public MultiVersionManager() {
		try {
			String serverVersion = Main.getInstance().getServer().getVersion();
			serverVersion = serverVersion.split("MC: ")[1];
			serverVersion = serverVersion.split("\\)")[0];
			String[] serverVersionSplitted = serverVersion.split("\\.");
			major = Short.valueOf(serverVersionSplitted[0].trim());
			minor = serverVersionSplitted.length > 1 ? Short.valueOf(serverVersionSplitted[1].trim()) : 0;
			Logger.debugMultiVersionManager("MultiVersionManager: Found version " + serverVersion + " with major " + major + " and minor " + minor);
			
			String packageName = Main.class.getPackage().getName() + ".multiversion";

			String recipesPackage;
			String blocksPackage;
			String wgPackage;
			String actionBarPackage;
			String chatPackage;
			String skullPackage;

			if (isModern()) {
				// Minecraft 26+ -- always the newest adapters. Without this the
				// "2" of 26.2 would fall through the 1.x comparisons below.
				recipesPackage = packageName + ".RecipesLoader_1_13";
				blocksPackage = packageName + ".BlocksUtils_1_13";
				wgPackage = packageName + ".WorldGuardUtils_1_13";
				actionBarPackage = packageName + ".ActionBar_1_9";
				chatPackage = packageName + ".ChatUtils_1_16";
				skullPackage = packageName + ".SkullUtils_1_21";
			} else if (minor == 8) {
				recipesPackage = packageName + ".RecipesLoader_1_8";
				blocksPackage = packageName + ".BlocksUtils_1_8";
				wgPackage = packageName + ".WorldGuardUtils_1_8";
				actionBarPackage = packageName + ".ActionBar_1_8";
				chatPackage = packageName + ".ChatUtils_1_8";
				skullPackage = packageName + ".SkullUtils_1_8";
			} else if (minor == 9 || minor == 10 || minor == 11) {
				recipesPackage = packageName + ".RecipesLoader_1_8";
				blocksPackage = packageName + ".BlocksUtils_1_8";
				wgPackage = packageName + ".WorldGuardUtils_1_8";
				actionBarPackage = packageName + ".ActionBar_1_9";
				chatPackage = packageName + ".ChatUtils_1_8";
				skullPackage = packageName + ".SkullUtils_1_8";
			} else if (minor == 12) {
				recipesPackage = packageName + ".RecipesLoader_1_12";
				blocksPackage = packageName + ".BlocksUtils_1_8";
				wgPackage = packageName + ".WorldGuardUtils_1_8";
				actionBarPackage = packageName + ".ActionBar_1_9";
				chatPackage = packageName + ".ChatUtils_1_8";
				skullPackage = packageName + ".SkullUtils_1_8";
			} else if (minor == 13 || minor == 14 || minor == 15) {
				recipesPackage = packageName + ".RecipesLoader_1_13";
				blocksPackage = packageName + ".BlocksUtils_1_13";
				wgPackage = packageName + ".WorldGuardUtils_1_13";
				actionBarPackage = packageName + ".ActionBar_1_9";
				chatPackage = packageName + ".ChatUtils_1_8";
				skullPackage = packageName + ".SkullUtils_1_8";
			} else if (minor == 16 || minor == 17 || minor == 18 || minor == 19 || minor == 20) {
				recipesPackage = packageName + ".RecipesLoader_1_13";
				blocksPackage = packageName + ".BlocksUtils_1_13";
				wgPackage = packageName + ".WorldGuardUtils_1_13";
				actionBarPackage = packageName + ".ActionBar_1_9";
				chatPackage = packageName + ".ChatUtils_1_16";
				skullPackage = packageName + ".SkullUtils_1_8";
			} else {
				recipesPackage = packageName + ".RecipesLoader_1_13";
				blocksPackage = packageName + ".BlocksUtils_1_13";
				wgPackage = packageName + ".WorldGuardUtils_1_13";
				actionBarPackage = packageName + ".ActionBar_1_9";
				chatPackage = packageName + ".ChatUtils_1_16";
				skullPackage = packageName + ".SkullUtils_1_21";
			}
			
			try {
				this.recipesLoader = (RecipesLoader) Class.forName(recipesPackage).newInstance();
				this.blocksUtils = (BlocksUtils) Class.forName(blocksPackage).newInstance();

				if (Main.getInstance().getServer().getPluginManager().getPlugin("WorldGuard") != null) {
					
					if (wgPackage.contains("1_8") && Main.getInstance().getServer().getPluginManager().getPlugin("WorldGuard").getDescription().getVersion().equals("6.1")) {
						Logger.error("MultiVersionManager: You are using unsupported WorldGuard version! Use WorldGuard 6.2 or higher!");
					} else {
						this.worldGuardUtils = (WorldGuardUtils) Class.forName(wgPackage).newInstance();
						this.worldGuardUtils.worldGuardFlagsAdd();
					}
				}

				this.actionBar = (ActionBar) Class.forName(actionBarPackage).newInstance();
				this.chatUtils = (ChatUtils) Class.forName(chatPackage).newInstance();
				this.skullUtils = (SkullUtils) Class.forName(skullPackage).newInstance();

			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e3) {
				Logger.error(e3);
			}
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	/**
	 * Whether the server is newer than 1.&lt;minorVersion&gt;.
	 *
	 * <p>Every caller passes a 1.x minor (9, 11, 12, 13) to gate modern
	 * behaviour. On Minecraft 26+ the answer is unconditionally yes, and
	 * comparing the raw minor would get it exactly backwards: 26.2 would report
	 * "older than 1.9" and silently disable off-hand handling, drop suppression
	 * and the inventory checks.
	 */
	public boolean isHigher(int minorVersion) {
		if (isModern())
			return true;
		return minor > minorVersion;
	}

	/** True once Minecraft dropped the leading "1." from its versions (26+). */
	private boolean isModern() {
		return major > 1;
	}
}
