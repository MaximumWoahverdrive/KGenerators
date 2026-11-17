package me.kryniowesegryderiusz.kgenerators.generators.recipes.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.recipes.RecipesManager;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.FilesUtils;
import me.kryniowesegryderiusz.kgenerators.utils.Text;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class Recipe {

    @Getter
    Generator generator;

    @Getter
    private final HashMap<Character, ItemStack> ingredients = new HashMap<>();

    @Getter
    @Setter
    private ArrayList<String> shape = new ArrayList<>();

    /**
     * Loads recipe from file and adds it to RecipesManager
     *
     * @param recipesManager the manager the loaded recipe is added to
     * @param file the recipes config
     * @param generatorID    aka path
     */
    @SuppressWarnings("unchecked")
    public Recipe(RecipesManager recipesManager, Config file, String generatorID) {
        if (!generatorID.equals("example_generator") && !generatorID.equals("enabled")) {
            Generator generator = Main.getGenerators().get(generatorID);
            if (generator == null) {
                Logger.error("Recipes file: There isnt any `" + generatorID
                        + "` generator in generators.yml! Recipe NOT LOADED!");
            } else {
                this.generator = generator;
                ingredients.put(' ', new ItemStack(Material.AIR));

                ShapedRecipe shapedRecipe = new ShapedRecipe(
                        new NamespacedKey(Main.getInstance(), "KGenerators_" + generator.getId()),
                        generator.getGeneratorItem());

                ArrayList<String> shape = (ArrayList<String>) file.getList(generatorID + ".shape");
                shapedRecipe.shape(shape.get(0), shape.get(1), shape.get(2));
                this.setShape(shape);

                ConfigurationSection ingredientsSection = file.getConfigurationSection(generatorID + ".ingredients");
                for (String ingredientsString : ingredientsSection.getKeys(false)) {
                    char ingredientsChar = ingredientsString.charAt(0);

                    ItemStack item =
                            FilesUtils.loadItemStack(file, generatorID + ".ingredients", ingredientsString, false);

                    // ExactChoice is deprecated on 26.x, but what replaces it does not exist on 1.19.4
                    shapedRecipe.setIngredient(ingredientsChar, new RecipeChoice.ExactChoice(item));
                    this.addIngredient(ingredientsChar, item);
                }

                recipesManager.add(generator, this);

                Bukkit.addRecipe(shapedRecipe);

                Logger.debugPluginLoad("Recipes file: Loaded recipe for `" + generatorID + "`! Recipe: " + this);
            }
        }
    }

    public Recipe(Generator generator, ShapedRecipe shaped) {
        this.generator = generator;
        for (Map.Entry<Character, RecipeChoice> e : shaped.getChoiceMap().entrySet())
            ingredients.put(e.getKey(), toItem(e.getValue()));
        Collections.addAll(shape, shaped.getShape());
    }

    public Recipe(Generator generator, ShapelessRecipe shapeless) {
        this.generator = generator;
        char[] letters = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
        shape.add("ABC");
        shape.add("DEF");
        shape.add("GHI");
        for (char l : letters) {
            ingredients.put(l, new ItemStack(Material.AIR));
        }

        List<RecipeChoice> choices = shapeless.getChoiceList();
        for (int i = 0; i < choices.size(); i++) {
            ingredients.put(letters[i], toItem(choices.get(i)));
        }
    }

    /** An item a recipe ingredient accepts: its first exact item or material, or air if the slot is empty. */
    private static ItemStack toItem(RecipeChoice choice) {
        if (choice instanceof RecipeChoice.ExactChoice exact
                && !exact.getChoices().isEmpty())
            return exact.getChoices().get(0).clone();
        if (choice instanceof RecipeChoice.MaterialChoice material
                && !material.getChoices().isEmpty())
            return new ItemStack(material.getChoices().get(0));
        return new ItemStack(Material.AIR);
    }

    public void addIngredient(char charr, ItemStack item) {
        ingredients.put(charr, item);
    }

    public ItemStack getGeneratorItem() {
        return this.generator.getGeneratorItem();
    }

    /**
     * Get 9 full items from recipe
     *
     * @return the ingredients of the 3x3 grid, row by row
     */
    public ArrayList<ItemStack> getRecipe() {
        ArrayList<ItemStack> recipe = new ArrayList<>();
        for (String s : shape) {
            for (int i = 0; i < 3; i++) {
                recipe.add(ingredients.get(s.charAt(i)));
            }
        }
        return recipe;
    }

    public String toString() {
        ArrayList<String> string = new ArrayList<>();
        for (String s : this.shape) {
            ArrayList<String> line = new ArrayList<>();
            for (char c : s.toCharArray()) {
                if (this.ingredients.get(c).hasItemMeta()
                        && this.ingredients.get(c).getItemMeta().hasDisplayName())
                    line.add(Text.name(this.ingredients.get(c).getItemMeta()));
                else line.add(this.ingredients.get(c).getType().name());
            }
            string.add(line.toString());
        }
        return string.toString();
    }
}
