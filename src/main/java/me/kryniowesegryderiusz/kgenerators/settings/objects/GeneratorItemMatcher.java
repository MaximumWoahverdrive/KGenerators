package me.kryniowesegryderiusz.kgenerators.settings.objects;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.utils.ItemData;
import me.kryniowesegryderiusz.kgenerators.utils.ItemUtils;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GeneratorItemMatcher {

    /**
     * @deprecated The NBT tag KGenerators 7.3.1 and older stored the generator id in. Generator
     *             items now carry {@link ItemData#GENERATOR_ID}; the old tag is still recognised.
     */
    @Deprecated
    public static String GENERATOR_ID_NBT = "kgenerators-generator-id";

    @Getter
    boolean nbtCheck = true;

    @Getter
    boolean itemMetaCheck = true;

    public Generator getGenerator(ItemStack item) {

        if (item == null || item.getType() == Material.AIR) return null;

        if (nbtCheck) {
            String id = ItemData.getString(item, ItemData.GENERATOR_ID);
            // Items made by 7.3.1 and older; reading their tag is slower, so only for generator materials
            if (id == null && isGeneratorMaterial(item.getType())) id = ItemData.getLegacyGeneratorId(item);
            if (id != null) return Main.getGenerators().get(id);
        }
        if (itemMetaCheck) {
            for (Generator generator : Main.getGenerators().getAll()) {
                if (ItemUtils.compareSafe(item, generator.getGeneratorItem())) return generator;
            }
        }

        return null;
    }

    private static boolean isGeneratorMaterial(Material material) {
        for (Generator generator : Main.getGenerators().getAll()) {
            if (generator.getGeneratorItem().getType() == material) return true;
        }
        return false;
    }

    public void load(Config config) {

        if (config.contains("generator-item-matcher")) {
            if (config.contains("generator-item-matcher.check-by-nbt"))
                this.nbtCheck = config.getBoolean("generator-item-matcher.check-by-nbt");
            if (config.contains("generator-item-matcher.check-by-item-meta"))
                this.itemMetaCheck = config.getBoolean("generator-item-matcher.check-by-item-meta");
        }
    }
}
