package me.kryniowesegryderiusz.kgenerators.utils.objects;

import com.cryptomorin.xseries.XMaterial;
import me.kryniowesegryderiusz.kgenerators.utils.SkullUtils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class HeadCustomBlockData extends CustomBlockData {

    private final String skullBase64;

    public HeadCustomBlockData(XMaterial xMaterial, String skullBase64) {
        super(xMaterial);
        this.skullBase64 = skullBase64;
    }

    @Override
    public void setBlock(Location location) {
        SkullUtils.blockWithBase64(location.getBlock(), skullBase64);
    }

    @Override
    public void setBlock(Location location, boolean phyciss) {
        setBlock(location);
    }

    @Override
    public ItemStack getItem() {
        return SkullUtils.itemFromBase64(skullBase64);
    }

    @Override
    public String toString() {
        return "Player Head: " + this.skullBase64;
    }
}
