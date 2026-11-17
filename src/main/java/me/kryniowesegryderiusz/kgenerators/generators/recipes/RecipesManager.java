package me.kryniowesegryderiusz.kgenerators.generators.recipes;

import java.util.Collection;
import java.util.HashMap;
import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.EcoItemsHook;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.recipes.objects.Recipe;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.ConfigManager;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class RecipesManager {

    @Getter
    private boolean enabled = false;

    private final HashMap<Generator, Recipe> recipes = new HashMap<>();

    public RecipesManager() {
        Logger.debugPluginLoad("RecipesManager: Setting up manager");

        Config file;

        try {
            file = ConfigManager.getConfig("recipes.yml", null, true, false);
            file.loadConfig();
        } catch (InvalidConfigurationException e) {
            Logger.error(
                    "Generators file: You've missconfigured recipes.yml file. Check your spaces! More info below. Disabling plugin.");
            Logger.error(e);
            Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
            return;
        } catch (Exception e) {
            Logger.error("Recipes file: Cant load recipes config. Disabling plugin.");
            Logger.error(e);
            Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
            return;
        }

        if (!file.contains("enabled") || !file.getBoolean("enabled")) {
            Logger.info("Recipes file: Recipes are disabled. You can enable them in recipes.yml");
            return;
        }

        this.enabled = true;

        for (String generatorID : file.getConfigurationSection("").getKeys(false)) {
            try {
                new Recipe(this, file, generatorID);
            } catch (Exception e) {
                Logger.error("Recipes file: Cannot load recipe for " + generatorID + ": " + e.getMessage());
            }
        }

        if (Main.getDependencies().isEnabled(Dependency.ECO_ITEMS)) EcoItemsHook.processRecipes(this);

        Logger.info("Recipes file: Loaded " + this.recipes.size() + " recipes");
    }

    public void add(Generator generator, Recipe recipe) {
        recipes.put(generator, recipe);
    }

    public Recipe get(Generator generator) {
        if (recipes.containsKey(generator)) return recipes.get(generator);
        else {
            for (org.bukkit.inventory.Recipe bukkitRecipe :
                    Main.getInstance().getServer().getRecipesFor(generator.getGeneratorItem())) {
                if (bukkitRecipe.getResult().equals(generator.getGeneratorItem())) {
                    if (bukkitRecipe instanceof ShapedRecipe shaped) return new Recipe(generator, shaped);
                    else if (bukkitRecipe instanceof ShapelessRecipe shapeless) return new Recipe(generator, shapeless);
                }
            }
            return null;
        }
    }

    public boolean hasRecipes() {
        return !this.recipes.isEmpty();
    }

    public Collection<Recipe> getAll() {
        return this.recipes.values();
    }
}
